import {AxeResults, CheckResult, NodeResult, Result} from "axe-core";
import {
    Check,
    CheckElement,
    CheckType,
    Impact,
    Rule,
    ScanJob,
    ScanStatus,
    WcagLevel,
    WcagReferences,
    WebpageResult,
    WebsiteResult
} from "./model.js";

function formatWebsiteResults(job: ScanJob, webpageResults: WebpageResult[]): WebsiteResult {
    return {
        modelVersion: "0.0.0",
        jobId: job.jobId,
        domain: job.domain,
        scanTimestamp: new Date().toISOString(),
        scanStatus: ScanStatus.Success,
        webpages: webpageResults
    }
}

function formatFailedWebsiteResult(error: unknown): WebsiteResult {
    return {
        modelVersion: "0.0.0",
        jobId: 0,
        domain: "N/A",
        scanTimestamp: new Date().toISOString(),
        scanStatus: ScanStatus.Failed,
        errorMessage: errorToErrorMessage(error),
        webpages: []
    }
}

function formatWebpageResults(url: string, results: AxeResults): WebpageResult {
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
        url: url,
        scanStatus: ScanStatus.Success,
        rules
    }
}

function formatFailedWebpageResult(url: string, error: unknown): WebpageResult {
    return {
        url: url,
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

    const tags = occurrences[0].rule.tags
    const wcagReference = extractWcagReference(tags)

    return {
        id,
        description: occurrences[0].rule.help,
        axeUrl: occurrences[0].rule.helpUrl,
        wcagReferences: wcagReference,
        checks
    }
}

function extractWcagReference(tags: string[]): WcagReferences | undefined {
    let wcagVersion: string | undefined
    let wcagLevel: WcagLevel | undefined
    const wcagCriteria: string[] = []

    // AxeCore defines that a WCAG criterion id can only consist of three numbers (https://github.com/dequelabs/axe-core/blob/develop/doc/API.md#axe-core-tags)
    // Even though this mean that some WCAG criteria cannot be represented (e.g. 1.4.10 -> Axe Tag: 'wcag1410'
    const tagWcagRegExp = /wcag(?<criterion>[0-9]{3})?((?<version>[0-9]{1,2})(?<level>a{1,3}))?/;
    tags.forEach((tag) => {
        const match = tag.match(tagWcagRegExp)
        if (match?.groups === undefined) return

        // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
        if (match.groups.criterion === undefined) {
            wcagVersion = match.groups.version.split("").join(".")
            wcagLevel = match.groups.level.toUpperCase() as WcagLevel
        } else {
            wcagCriteria.push(match.groups.criterion.split("").join("."))
        }
    })

    if (wcagCriteria.length === 0) return undefined
    if (wcagLevel === undefined) throw new Error("Tags contained WCAG criterion but no WCAG level")
    if (wcagVersion === undefined) throw new Error("Tags contained WCAG criterion but no WCAG version")
    if (wcagVersion.length === 1) wcagVersion = wcagVersion + ".0"

    return {
        version: wcagVersion,
        level: wcagLevel as WcagLevel,
        criteria: wcagCriteria,
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

export {formatWebsiteResults, formatFailedWebsiteResult, formatWebpageResults, formatFailedWebpageResult}
