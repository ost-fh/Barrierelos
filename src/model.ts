interface AnalysisJob {
    modelVersion: string
    jobTimestamp: string
    websiteBaseUrl: string
    webpagePaths: string[]
}

interface AnalysisResult {
    modelVersion: string
    website: string
    scanTimestamp: string
    scanStatus: "success" | "failed"
    errorMessage?: string
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

type CheckType = "any" | "all" | "none"

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

export {AnalysisJob, AnalysisResult, WebpageResult, Rule, Check, CheckType, Impact, Element, CheckElement}