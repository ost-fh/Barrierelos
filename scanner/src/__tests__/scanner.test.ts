/* eslint-disable @typescript-eslint/no-unsafe-argument */
import {beforeEach, describe, it} from "mocha"
import * as td from "testdouble"
import {scanWebsite} from "../scanner.js";
import {ScanJob, ScanStatus} from "../model.js";
import puppeteer, {Browser, BrowserContext, Frame, Page} from "puppeteer";
import * as axePuppeteer from "@axe-core/puppeteer"
import logger from "../logger.js";
import {getPopulatedAxeResults} from "../__mocks__/axeResults.js";
import {expect} from "chai";
import axe from "axe-core";


before(() => {
    logger.transports.forEach(value => value.silent = true)
})
describe("scanWebsite()", () => {
    let job: ScanJob
    let page: Page
    let analyze: {
        (): Promise<axe.AxeResults>,
        <T extends axePuppeteer.AnalyzeCB>(callback?: (T | undefined)): Promise<axe.AxeResults | null>
    }
    beforeEach(() => {
        job = {
            modelVersion: "",
            jobId: 0,
            jobTimestamp: "",
            domain: "",
            webpages: ["", ""]
        }

        td.reset()
        const launch = td.replace(puppeteer, "launch")
        const browser = td.object<Browser>()
        td.when(launch(td.matchers.anything())).thenResolve(browser)
        const context = td.object<BrowserContext>()
        td.when(browser.createIncognitoBrowserContext()).thenResolve(context)
        page = td.object<Page>()
        td.when(context.newPage()).thenResolve(page)

        // The following two line suppress deprecation warnings
        td.when(page.mainFrame()).thenReturn(td.object<Frame>())
        td.when(page.browser()).thenReturn(browser)

        analyze = td.replace(axePuppeteer.AxePuppeteer.prototype, "analyze")
        td.when(analyze()).thenResolve(getPopulatedAxeResults())
    })
    it("should return a success result", async () => {
        const result = await scanWebsite(job)
        expect(result.scanStatus).to.equal(ScanStatus.Success)
        expect(result.webpages[0].scanStatus).to.equal(ScanStatus.Success)
        expect(result.webpages[1].scanStatus).to.equal(ScanStatus.Success)
    })
    it("should retry with waitForNavigation when ExecutionContextDestroyed Error is thrown", async () => {
        td.when(analyze()).thenReject(Error("Execution context was destroyed, most likely because of a navigation."))
        await scanWebsite(job)
        td.verify(page.waitForNavigation())
    })
});

