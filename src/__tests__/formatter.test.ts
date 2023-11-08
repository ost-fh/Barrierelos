import {describe, it, test} from "mocha"
import {expect} from "chai";
import {
    formatAnalysisResults,
    formatFailedAnalysisResult,
    formatFailedWebpageResult,
    formatWebpageResults
} from "../formatter.js";
import {ScanStatus, WebpageResult} from "../model.js";
import {getEmptyAxeResults, getPopulatedAxeResults} from "../__mocks__/axeResults.js";

describe("formatAnalysisResults()", () => {
    it("should contain the baseUrl as website property", () => {
        const baseUrl = "testBaseUrl"
        const result = formatAnalysisResults(baseUrl, [])
        expect(result.website).to.equal(baseUrl)
    })
    it("should return the webpageResults as webpages property", () => {
        const webpages = [
            {test: 1} as unknown as WebpageResult,
            {test: 2} as unknown as WebpageResult,
            {test: 3} as unknown as WebpageResult,
        ]
        const result = formatAnalysisResults("", webpages)
        expect(result.webpages.length).to.equal(3)
        expect(result.webpages).to.equal(webpages)
    })
    it("should return a success scanStatus and no errorMessage", () => {
        const result = formatAnalysisResults("", [])
        expect(result.scanStatus).to.equal(ScanStatus.Success)
        expect(result.errorMessage).to.be.undefined
    })
    it("should return an accurate scanTimestamp", () => {
        const result = formatAnalysisResults("", [])
        const timeDiffMilliseconds = new Date().getTime() - new Date(result.scanTimestamp).getTime()
        expect(timeDiffMilliseconds).to.be.lessThan(1000)
        expect(result.errorMessage).to.be.undefined
    })
})

describe("formatFailedAnalysisResult()", () => {
    it("should still contain the baseUrl as website property", () => {
        const baseUrl = "testBaseUrl"
        const failedResult = formatFailedAnalysisResult(baseUrl, "")
        expect(failedResult.website).to.equal(baseUrl)
    })
    it("should return a failed scanStatus and an errorMessage", () => {
        const failedResult = formatFailedAnalysisResult("", "")
        expect(failedResult.scanStatus).to.equal(ScanStatus.Failed)
        expect(failedResult.errorMessage).not.to.be.undefined
    })
    it("should return an accurate scanTimestamp", () => {
        const failedResult = formatAnalysisResults("", [])
        const timeDiffMilliseconds = new Date().getTime() - new Date(failedResult.scanTimestamp).getTime()
        expect(timeDiffMilliseconds).to.be.lessThan(1000)
        expect(failedResult.errorMessage).to.be.undefined
    })
    it("should return an empty webpages array", () => {
        const failedResult = formatFailedAnalysisResult("", "")
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

const failedResultFunctions = [
    {fn: formatFailedAnalysisResult},
    {fn: formatFailedWebpageResult},
]
failedResultFunctions.forEach((param) => {
    describe(`Different error parameters being passed to $param.fn.name()`, () => {
        test("String should return the same as message", () => {
            const error = "errorString";
            const failedResult = param.fn("", error)
            expect(failedResult.errorMessage).to.equal(error)
        })
        test("Error should return message property", () => {
            const errorMessage = "errorMessage"
            const failedResult = param.fn("", Error(errorMessage))
            expect(failedResult.errorMessage).to.equal(errorMessage)
        })
        test("Object with message property should return that message", () => {
            const errorMessage = "errorMessage"
            const errorObject = {
                message: errorMessage
            }
            const failedResult = param.fn("", errorObject)
            expect(failedResult.errorMessage).to.equal(errorMessage)
        })
        test("Object without message property should be stringified", () => {
            const errorObject = {
                someProperty: "someValue"
            }
            const failedResult = param.fn("", errorObject)
            expect(failedResult.errorMessage).to.equal(JSON.stringify(errorObject))
        })
        test("Null should return default message", () => {
            const failedResult = param.fn("", null)
            expect(failedResult.errorMessage).to.equal("An unknown error occurred.")
        })
    })
})
