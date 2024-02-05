import puppeteer, {Browser, Page, TimeoutError} from "puppeteer";
import {AxePuppeteer} from "@axe-core/puppeteer";
import {formatFailedWebpageResult, formatWebpageResults, formatWebsiteResults} from "./formatter.js";
import {ScanJob, WebpageResult, WebsiteResult} from "./model.js";
import Logger from "./logger.js";


export async function scanWebsite(job: ScanJob): Promise<WebsiteResult> {
    Logger.info(`Starting to scan ${job.domain}, consisting of ${job.webpages.length} webpages`)
    const browser = await puppeteer.launch({headless: "new", args: ["--no-sandbox"]});

    const webpageResults = await Promise.all(
        job.webpages.map(async url => {
            return await scanWebpage({
                browser,
                job,
                url: url,
            })
        })
    )
    await browser.close();

    Logger.info(`Finished scanning ${job.domain}`)
    return formatWebsiteResults(job, webpageResults)
}

interface WebpageScanProperties {
    browser: Browser,
    job: ScanJob,
    url: string
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
    page.setDefaultNavigationTimeout(props.navigationTimeoutMs)
    // One of the most common desktop user agents, taken from https://www.useragents.me/
    await page.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");

    try {
        await navigateToUrl(page, props.url)
        // This prevents problems with JS redirects
        if (props.waitForNavigation) {
            await page.waitForNavigation()
        }

        const axePuppeteer = new AxePuppeteer(page)
        const results = await axePuppeteer.analyze();

        return formatWebpageResults(props.url, results)
    } catch (e) {
        if (
            !props.waitForNavigation &&
            e instanceof Error &&
            e.message === "Execution context was destroyed, most likely because of a navigation."
        ) {
            props.waitForNavigation = true
            return scanWebpage(props)
        }

        Logger.error(`Failed to scan ${props.url}`)
        Logger.error(e)

        if (props.retries > 0) {
            props.retries -= 1
            Logger.info(`Retrying to scan ${props.url}, ${props.retries} remaining retries`)
            if (e instanceof TimeoutError) props.navigationTimeoutMs += 10e3
            return scanWebpage(props)
        }

        return formatFailedWebpageResult(props.url, e)
    }
}

async function navigateToUrl(page: Page, url: string) {
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
