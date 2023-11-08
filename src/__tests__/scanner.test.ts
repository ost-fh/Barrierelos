/* eslint-disable @typescript-eslint/no-unsafe-argument */
import {beforeEach, describe, it} from "mocha"
import * as td from "testdouble"
import {analyzeWebsite} from "../scanner.js";
import {AnalysisJob} from "../model.js";
import puppeteer, {Browser} from "puppeteer";

const launch = td.replace(puppeteer, "launch")
const browser = td.object<Browser>()
td.when(launch(td.matchers.anything())).thenResolve(browser)

describe("analyzeWebsite()", () => {
    let job: AnalysisJob
    beforeEach(() => {
        job = {
            modelVersion: "",
            jobTimestamp: "",
            websiteBaseUrl: "",
            webpagePaths: []
        }
    })
    it("create a new browser", async () => {
        await analyzeWebsite(job)
        td.verify(launch(td.matchers.anything()))
    })
});

