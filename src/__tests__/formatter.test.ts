import {describe, it, test} from "mocha"
import {expect} from "chai";
import {
    formatFailedWebpageResult,
    formatFailedWebsiteResult,
    formatWebpageResults,
    formatWebsiteResults
} from "../formatter.js";
import {AnalysisJob, ScanStatus, WebpageResult} from "../model.js";
import {getEmptyAxeResults, getPopulatedAxeResults} from "../__mocks__/axeResults.js";
import {getEmptyAnalysisJob} from "../__mocks__/analysisJob.js";

describe("formatWebsiteResults()", () => {
    let job: AnalysisJob
    beforeEach(() => {
        job = getEmptyAnalysisJob()
    })
    it("should contain the jobId and baseUrl as website property", () => {
        const jobId = 13
        const locale = "de"
        const baseUrl = "testBaseUrl"
        job.jobId = jobId
        job.locale = locale
        job.websiteBaseUrl = baseUrl
        const result = formatWebsiteResults(job, [])
        expect(result.jobId).to.equal(jobId)
        expect(result.website).to.equal(baseUrl)
    })
    it("should return the webpageResults as webpages property", () => {
        const webpages = [
            {test: 1} as unknown as WebpageResult,
            {test: 2} as unknown as WebpageResult,
            {test: 3} as unknown as WebpageResult,
        ]
        const result = formatWebsiteResults(job, webpages)
        expect(result.webpages.length).to.equal(3)
        expect(result.webpages).to.equal(webpages)
    })
    it("should return a success scanStatus and no errorMessage", () => {
        const result = formatWebsiteResults(job, [])
        expect(result.scanStatus).to.equal(ScanStatus.Success)
        expect(result.errorMessage).to.be.undefined
    })
    it("should return an accurate scanTimestamp", () => {
        const result = formatWebsiteResults(job, [])
        const timeDiffMilliseconds = new Date().getTime() - new Date(result.scanTimestamp).getTime()
        expect(timeDiffMilliseconds).to.be.lessThan(1000)
        expect(result.errorMessage).to.be.undefined
    })
})

describe("formatFailedWebsiteResult()", () => {
    it("should contain jobId 0 and baseUrl N/A as website properties", () => {
        const failedResult = formatFailedWebsiteResult("")
        expect(failedResult.jobId).to.equal(0)
        expect(failedResult.website).to.equal("N/A")
    })
    it("should return a failed scanStatus and an errorMessage", () => {
        const failedResult = formatFailedWebsiteResult("")
        expect(failedResult.scanStatus).to.equal(ScanStatus.Failed)
        expect(failedResult.errorMessage).not.to.be.undefined
    })
    it("should return an accurate scanTimestamp", () => {
        const failedResult = formatFailedWebsiteResult([])
        const timeDiffMilliseconds = new Date().getTime() - new Date(failedResult.scanTimestamp).getTime()
        expect(timeDiffMilliseconds).to.be.lessThan(1000)
        expect(failedResult.errorMessage).not.to.be.undefined
    })
    it("should return an empty webpages array", () => {
        const failedResult = formatFailedWebsiteResult("")
        expect(failedResult.webpages).to.be.empty
    })
})

describe("formatWebpageResults()", () => {
    it("should return the path as a property", () => {
        const path = "testPath"
        const result = formatWebpageResults(path, getEmptyAxeResults())
        expect(result.path).to.equal(path)
    })
    it("should return a success scanStatus and no errorMessage", () => {
        const result = formatWebpageResults("", getEmptyAxeResults())
        expect(result.scanStatus).to.equal(ScanStatus.Success)
        expect(result.errorMessage).to.be.undefined
    })
    it("should aggregate rule results", () => {
        const result = formatWebpageResults("", getPopulatedAxeResults())
        expect(result.rules.length).to.equal(1)
        expect(result.rules[0].checks.length).to.equal(1)
        const check = result.rules[0].checks[0]
        expect(check.testedCount).to.equal(96)
        expect(check.passedCount).to.equal(24)
        expect(check.violatedCount).to.equal(24)
        expect(check.incompleteCount).to.equal(24)
        expect(check.violatingElements.length).to.equal(24)
        expect(check.incompleteElements.length).to.equal(24)
        expect(check.violatingElements[0].relatedElements.length).to.equal(2)
        expect(check.incompleteElements[0].relatedElements.length).to.equal(2)
    })
})

describe("formatFailedWebpageResult()", () => {
    it("should still return the path as a property", () => {
        const path = "testPath"
        const failedResult = formatFailedWebpageResult(path, "")
        expect(failedResult.path).to.equal(path)
    })
    it("should return a failed scanStatus and an errorMessage", () => {
        const failedResult = formatFailedWebpageResult("", "")
        expect(failedResult.scanStatus).to.equal(ScanStatus.Failed)
        expect(failedResult.errorMessage).not.to.be.undefined
    })
    it("should return an empty rules array", () => {
        const failedResult = formatFailedWebpageResult("", "")
        expect(failedResult.rules.length).to.equal(0)
    })
})

describe(`Different error parameters being passed to formatFailedWebsiteResult`, () => {
    test("String should return the same as message", () => {
        const error = "errorString";
        const failedResult = formatFailedWebsiteResult(error)
        expect(failedResult.errorMessage).to.equal(error)
    })
    test("Error should return message property", () => {
        const errorMessage = "errorMessage"
        const failedResult = formatFailedWebsiteResult(Error(errorMessage))
        expect(failedResult.errorMessage).to.equal(errorMessage)
    })
    test("Object with message property should return that message", () => {
        const errorMessage = "errorMessage"
        const errorObject = {
            message: errorMessage
        }
        const failedResult = formatFailedWebsiteResult(errorObject)
        expect(failedResult.errorMessage).to.equal(errorMessage)
    })
    test("Object without message property should be stringified", () => {
        const errorObject = {
            someProperty: "someValue"
        }
        const failedResult = formatFailedWebsiteResult(errorObject)
        expect(failedResult.errorMessage).to.equal(JSON.stringify(errorObject))
    })
    test("Null should return default message", () => {
        const failedResult = formatFailedWebsiteResult(null)
        expect(failedResult.errorMessage).to.equal("An unknown error occurred.")
    })
})
