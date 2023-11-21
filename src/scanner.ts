import {join} from "path";
import puppeteer, {Browser} from "puppeteer";
import {AxePuppeteer} from "@axe-core/puppeteer";
import {formatAnalysisResults, formatFailedWebpageResult, formatWebpageResults} from "./formatter.js";
import {AnalysisJob, AnalysisResult, WebpageResult} from "./model.js";
import Logger from "./logger.js";

export async function analyzeWebsite(job: AnalysisJob): Promise<AnalysisResult> {
    const browser = await puppeteer.launch({headless: "new", args: ["--no-sandbox"]});
    const webpageResults = await Promise.all(
        job.webpagePaths.map(async path => {
            return await analyzeWebpage(browser, job.websiteBaseUrl, path)
        })
    )
    await browser.close();

    return formatAnalysisResults(job.websiteBaseUrl, webpageResults)
}

async function analyzeWebpage(browser: Browser, baseUrl: string, path: string): Promise<WebpageResult> {
    const context = await browser.createIncognitoBrowserContext()
    const page = await context.newPage()
    await page.setBypassCSP(true)
    // One of the most common desktop user agents, taken from https://www.useragents.me/
    await page.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");

    try {
        await page.goto(join(baseUrl, path));
        const results = await new AxePuppeteer(page).analyze();
        return formatWebpageResults(path, results)
    } catch (e) {
        Logger.info(e)
        return formatFailedWebpageResult(path, e)
    }
}
