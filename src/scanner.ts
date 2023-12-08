import puppeteer, {Browser, TimeoutError} from "puppeteer";
import {AxePuppeteer} from "@axe-core/puppeteer";
import {formatFailedWebpageResult, formatWebpageResults, formatWebsiteResults} from "./formatter.js";
import {ScanJob, WebpageResult, WebsiteResult} from "./model.js";
import Logger from "./logger.js";
import {Locale} from "axe-core";
import * as fs from "fs";

const additionalLocales: Record<string, Locale> = {
    // Translations downloaded from: https://github.com/dequelabs/axe-core/tree/develop/locales
    "de": JSON.parse(fs.readFileSync("locales/de.json", "utf-8")) as Locale,
}

export async function scanWebsite(job: ScanJob): Promise<WebsiteResult> {
    Logger.info(`Starting to scan ${job.websiteBaseUrl}, consisting of ${job.webpagePaths.length} webpages`)
    const browser = await puppeteer.launch({headless: "new", args: ["--no-sandbox"]});
    const webpageResults = await Promise.all(
        job.webpagePaths.map(async path => {
            return await scanWebpage({
                browser,
                job,
                path,
            })
        })
    )
    await browser.close();
    Logger.info(`Finished scanning ${job.websiteBaseUrl}`)

    return formatWebsiteResults(job, webpageResults)
}

interface WebpageScanProperties {
    browser: Browser,
    job: ScanJob,
    path: string
    waitForNavigation?: boolean,
    retries?: number,
    navigationTimeoutMs?: number,
}

async function scanWebpage(webpageScanProperties: WebpageScanProperties): Promise<WebpageResult> {
    const props: Required<WebpageScanProperties> = {
        waitForNavigation: false,
        retries: 2,
        navigationTimeoutMs: 10e3,
        ...webpageScanProperties,
    }
    const context = await props.browser.createIncognitoBrowserContext()
    const page = await context.newPage()
    await page.setBypassCSP(true)
    await page.setExtraHTTPHeaders({
        'Accept-Language': props.job.locale
    });
    page.setDefaultNavigationTimeout(props.navigationTimeoutMs)
    // One of the most common desktop user agents, taken from https://www.useragents.me/
    await page.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
    const url = props.job.websiteBaseUrl + props.path

    try {
        await navigateToUrl()
        // This prevents problems with JS redirects
        if (props.waitForNavigation) {
            await page.waitForNavigation()
        }
        const axePuppeteer = new AxePuppeteer(page)

        if (props.job.locale !== "en") {
            axePuppeteer.configure({locale: additionalLocales[props.job.locale]})
        }

        const results = await axePuppeteer.analyze();
        return formatWebpageResults(props.path, results)
    } catch (e) {
        if (
            !props.waitForNavigation &&
            e instanceof Error &&
            e.message === "Execution context was destroyed, most likely because of a navigation."
        ) {
            props.waitForNavigation = true
            return scanWebpage(props)
        }

        Logger.error(`Failed to scan ${url}`)
        Logger.error(e)

        if (props.retries > 0) {
            Logger.info(`Retrying to scan ${url}, ${props.retries - 1} remaining retries`)
            if (e instanceof TimeoutError) props.navigationTimeoutMs += 10e3
            return scanWebpage(props)
        }

        return formatFailedWebpageResult(props.path, e)
    }

    async function navigateToUrl() {
        try {
            await page.goto(url, {waitUntil: "networkidle0"})
        } catch (e) {
            if (e instanceof TimeoutError) {
                // Stop long / infinitely loading pages and continue processing with what's already been loaded
                Logger.info(`Terminating loading and continuing processing ${url} `)
                await page.evaluate(() => {
                    window.stop()
                })
                // Wait for frame to be ready because there is no callback for that
                await new Promise((resolve) => {
                    setTimeout(resolve, 3e3)
                })
                return
            }
            throw e
        }
    }
}
