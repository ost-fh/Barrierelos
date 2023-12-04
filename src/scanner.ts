import puppeteer, {Browser, TimeoutError} from "puppeteer";
import {AxePuppeteer} from "@axe-core/puppeteer";
import {formatFailedWebpageResult, formatWebpageResults, formatWebsiteResults} from "./formatter.js";
import {ScanJob, WebpageResult, WebsiteResult} from "./model.js";
import Logger from "./logger.js";
import {Locale, Spec} from "axe-core";
import * as fs from "fs";

const locales: Record<string, Locale> = {
    "de": JSON.parse(fs.readFileSync("locales/de.json", "utf-8")) as Locale,
}

export async function analyzeWebsite(job: ScanJob): Promise<WebsiteResult> {
    Logger.info(`Starting to scan ${job.websiteBaseUrl}, consisting of ${job.webpagePaths.length} webpages`)
    const browser = await puppeteer.launch({headless: "new", args: ["--no-sandbox"]});
    const webpageResults = await Promise.all(
        job.webpagePaths.map(async path => {
            return await analyzeWebpage(browser, job, path)
        })
    )
    await browser.close();
    Logger.info(`Finished scanning ${job.websiteBaseUrl}`)

    return formatWebsiteResults(job, webpageResults)
}

async function analyzeWebpage(browser: Browser, job: ScanJob, path: string, waitForNavigation = false, retries = 2, navigationTimeout = 10e3): Promise<WebpageResult> {
    const context = await browser.createIncognitoBrowserContext()
    const page = await context.newPage()
    await page.setBypassCSP(true)
    page.setDefaultNavigationTimeout(navigationTimeout)
    // One of the most common desktop user agents, taken from https://www.useragents.me/
    await page.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");
    const url = job.websiteBaseUrl + path

    try {
        await navigateToUrl()
        // This prevents problems with JS redirects
        if (waitForNavigation) {
            await page.waitForNavigation()
        }
        const axePuppeteer = new AxePuppeteer(page)

        if (job.locale !== "en") {
            const axeSpec: Spec = {
                locale: locales[job.locale]
            }
            axePuppeteer.configure(axeSpec)
        }

        const results = await axePuppeteer.analyze();
        return formatWebpageResults(path, results)
    } catch (e) {
        if (
            !waitForNavigation &&
            e instanceof Error &&
            e.message === "Execution context was destroyed, most likely because of a navigation."
        ) {
            return analyzeWebpage(browser, job, path, true, retries, navigationTimeout)
        }

        Logger.error(`Failed to scan ${url}`)
        Logger.error(e)

        if (retries > 0) {
            Logger.info(`Retrying to scan ${url}, ${retries - 1} remaining retries`)
            const timeout = e instanceof TimeoutError ? navigationTimeout + 10e3 : navigationTimeout
            return analyzeWebpage(browser, job, path, waitForNavigation, retries - 1, timeout)
        }

        return formatFailedWebpageResult(path, e)
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
                await new Promise((resolve) => {
                    setTimeout(resolve, 3e3)
                })
                return
            }
            throw e
        }
    }
}
