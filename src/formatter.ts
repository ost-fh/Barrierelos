import {AxeResults, CheckResult, NodeResult, Result} from "axe-core";
import {
    AnalysisJob,
    AnalysisResult,
    Check,
    CheckElement,
    CheckType,
    Impact,
    Rule,
    ScanStatus,
    WebpageResult
} from "./model.js";

function formatAnalysisResults(job: AnalysisJob, webpageResults: WebpageResult[]): AnalysisResult {
    return {
        modelVersion: "0.0.0",
        jobId: job.jobId,
        website: job.websiteBaseUrl,
        scanTimestamp: new Date().toISOString(),
        scanStatus: ScanStatus.Success,
        webpages: webpageResults
    }
}

function formatFailedAnalysisResult(error: unknown): AnalysisResult {
    return {
        modelVersion: "0.0.0",
        jobId: 0,
        website: "N/A",
        scanTimestamp: new Date().toISOString(),
        scanStatus: ScanStatus.Failed,
        errorMessage: errorToErrorMessage(error),
        webpages: []
    }
}

function formatWebpageResults(path: string, results: AxeResults): WebpageResult {
    const ruleOccurrencesMap = new Map<string, AxeRule[]>()

    results.passes.forEach(rule => {
        addToMapArray(ruleOccurrencesMap, rule.id, {rule, result: "pass"})
    })
    results.violations.forEach(rule => {
        addToMapArray(ruleOccurrencesMap, rule.id, {rule, result: "violation"})
    })
    results.incomplete.forEach(rule => {
        addToMapArray(ruleOccurrencesMap, rule.id, {rule, result: "incomplete"})
    })
    results.inapplicable.forEach(rule => {
        addToMapArray(ruleOccurrencesMap, rule.id, {rule, result: "inapplicable"})
    })

    const rules = Array.from(ruleOccurrencesMap.entries(), formatRuleOccurrences)

    return {
        path,
        scanStatus: ScanStatus.Success,
        rules
    }
}

function formatFailedWebpageResult(path: string, error: unknown): WebpageResult {
    return {
        path,
        scanStatus: ScanStatus.Failed,
        errorMessage: errorToErrorMessage(error),
        rules: []
    }
}

function errorToErrorMessage(error: unknown) {
    if (error === null) return "An unknown error occurred."
    if (typeof error === "string") return error
    if (typeof error === "object" && "message" in error && error.message != null && error !== error.message) {
        return errorToErrorMessage(error.message)
    }
    return JSON.stringify(error)
}

interface AxeRule {
    rule: Result
    result: AxeResultStatus
}

type AxeResultStatus = "pass" | "violation" | "incomplete" | "inapplicable"


function addToMapArray<K, V>(map: Map<K, V[]>, key: K, value: V) {
    const array = map.get(key) ?? []
    array.push(value)
    map.set(key, array)
}

function formatRuleOccurrences(axeRuleOccurrences: [string, AxeRule[]]): Rule {
    const [id, occurrences] = axeRuleOccurrences

    if (occurrences.length === 0) throw new Error("Found no rules for a website")

    const checkOccurrencesMap = new Map<string, AxeCheck[]>()

    occurrences.forEach(axeRule => {
        axeRule.rule.nodes.forEach(element => {
            element.all.forEach(check => {
                addToMapArray(checkOccurrencesMap, check.id, {check, element, type: "all", result: axeRule.result})
            })
            element.any.forEach(check => {
                addToMapArray(checkOccurrencesMap, check.id, {check, element, type: "any", result: axeRule.result})
            })
            element.none.forEach(check => {
                addToMapArray(checkOccurrencesMap, check.id, {check, element, type: "none", result: axeRule.result})
            })
        })
    })

    const checks = Array.from(checkOccurrencesMap.entries(), formatCheckOccurrences)

    return {
        id,
        description: occurrences[0].rule.description,
        checks
    }
}

function formatCheckOccurrences(axeCheckOccurrences: [string, AxeCheck[]]): Check {
    const [id, occurrences] = axeCheckOccurrences

    if (occurrences.length === 0) throw new Error("Found no checks for a rule")

    const check: Check = {
        id,
        type: occurrences[0].type,
        impact: occurrences[0].check.impact as Impact,
        testedCount: 0,
        passedCount: 0,
        violatedCount: 0,
        incompleteCount: 0,
        violatingElements: [],
        incompleteElements: [],
    }

    occurrences.forEach(axeCheck => {
        check.testedCount++
        switch (axeCheck.result) {
            case "pass":
                check.passedCount++
                break
            case "violation":
                check.violatedCount++
                check.violatingElements.push(formatElement(axeCheck))
                break
            case "incomplete":
                check.incompleteCount++
                check.incompleteElements.push(formatElement(axeCheck))
                break
        }
    })

    return check
}

function formatElement(axeCheck: AxeCheck): CheckElement {
    return {
        target: axeCheck.element.target.toString(),
        html: axeCheck.element.html,
        issueDescription: axeCheck.check.message,
        data: JSON.stringify(axeCheck.check.data),
        relatedElements: axeCheck.check.relatedNodes?.map(node => {
            return {
                target: node.target.toString(),
                html: node.html
            }
        }) ?? []
    };
}

interface AxeCheck {
    check: CheckResult
    element: NodeResult
    type: CheckType
    result: AxeResultStatus
}

export {formatAnalysisResults, formatFailedAnalysisResult, formatWebpageResults, formatFailedWebpageResult}
