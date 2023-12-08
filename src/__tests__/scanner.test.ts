/* eslint-disable @typescript-eslint/no-unsafe-argument */
import {beforeEach, describe, it} from "mocha"
import * as td from "testdouble"
import {scanWebsite} from "../scanner.js";
import {ScanJob} from "../model.js";
import puppeteer, {Browser} from "puppeteer";
import logger from "../logger.js";


before(() => {
    logger.transports.forEach(value => value.silent = true)
})
describe("scanWebsite()", () => {
    let job: ScanJob
    beforeEach(() => {
        job = {
            modelVersion: "",
            jobId: 0,
            jobTimestamp: "",
            websiteBaseUrl: "",
            webpagePaths: []
        }
    })
    it("create a new browser", async () => {
        const launch = td.replace(puppeteer, "launch")
        const browser = td.object<Browser>()
        td.when(launch(td.matchers.anything())).thenResolve(browser)

        await scanWebsite(job)
    })
});

