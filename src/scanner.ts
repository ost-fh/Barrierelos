import {join} from "path";
import puppeteer, {Browser} from "puppeteer";
import {AxePuppeteer} from "@axe-core/puppeteer";
import {AxeResults, CheckResult, NodeResult, Result} from "axe-core";

async function analyzeWebsite(baseUrl: string, webpage_paths: string[]): Promise<AnalysisResult> {
    const browser = await puppeteer.launch({headless: "new"});
    const webpageResults = await Promise.all(
        webpage_paths.map(async path => {
            return await analyzeWebpage(browser, baseUrl, path)
        })
    )
    await browser.close();

    return {
        version: "0.0.0",
        website: baseUrl,
        scanDate: new Date().toISOString(),
        webpages: webpageResults
    }
}

async function analyzeWebpage(browser: Browser, baseUrl: string, path: string): Promise<WebpageResult> {
    const context = await browser.createIncognitoBrowserContext()
    const page = await context.newPage()
    await page.setBypassCSP(true)

    try {
        await page.goto(join(baseUrl, path));
        const results = await new AxePuppeteer(page).analyze();
        return formatResults(path, results)
    } catch (e) {
        console.log(e)
        return {
            path,
            scanStatus: "failed",
            errorMessage: JSON.stringify(e),
            rules: []
        }
    }
}

function formatResults(path: string, results: AxeResults): WebpageResult {
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

type CheckType = "any" | "all" | "none"

interface AnalysisResult {
    version: string
    website: string
    scanDate: string
    webpages: WebpageResult[]
}

interface WebpageResult {
    path: string
    scanStatus: "success" | "failed"
    errorMessage?: string
    rules: Rule[]
}

interface Rule {
    id: string
    checks: Check[]
}

interface Check {
    id: string
    type: CheckType
    impact: Impact
    testedCount: number
    passedCount: number
    violatedCount: number
    incompleteCount: number
    violatingElements: CheckElement[]
    incompleteElements: CheckElement[]
}

type Impact = "minor" | "moderate" | "serious" | "critical"

interface Element {
    target: string
    html: string
}

interface CheckElement extends Element {
    issueDescription: string
    data: string
    relatedElements: Element[]
}

export default analyzeWebsite
