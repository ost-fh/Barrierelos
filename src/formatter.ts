import {AxeResults, CheckResult, NodeResult, Result} from "axe-core";
import {AnalysisResult, Check, CheckElement, CheckType, Impact, Rule, WebpageResult} from "./model.js";


function formatFailedAnalysisResult(baseUrl: string, error: unknown): AnalysisResult {
    return {
        modelVersion: "0.0.0",
        website: baseUrl,
        scanTimestamp: new Date().toISOString(),
        scanStatus: "failed",
        errorMessage: JSON.stringify(error),
        webpages: []
    }
}

function formatAnalysisResults(baseUrl: string, webpageResults: WebpageResult[]): AnalysisResult {
    return {
        modelVersion: "0.0.0",
        website: baseUrl,
        scanTimestamp: new Date().toISOString(),
        scanStatus: "success",
        webpages: webpageResults
    }
}

function formatFailedWebpageResult(path: string, error: unknown): WebpageResult {
    return {
        path,
        scanStatus: "failed",
        errorMessage: JSON.stringify(error),
        rules: []
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
        scanStatus: "success",
        rules
    }
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
        target: JSON.stringify(axeCheck.element.target),
        html: axeCheck.element.html,
        issueDescription: axeCheck.check.message,
        data: JSON.stringify(axeCheck.check.data),
        relatedElements: axeCheck.check.relatedNodes?.map(node => {
            return {
                target: JSON.stringify(node.target),
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
