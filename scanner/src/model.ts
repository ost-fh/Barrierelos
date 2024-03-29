interface AppProperties {
    rabbitmqUser: string
    rabbitmqPassword: string
    rabbitmqHostname: string
    rabbitmqPort: string
}

interface ScanJob {
    modelVersion: string
    jobId: number
    jobTimestamp: string
    domain: string
    webpages: string[]
}

interface WebsiteResult {
    modelVersion: string
    jobId: number
    domain: string
    scanTimestamp: string
    scanStatus: ScanStatus
    errorMessage?: string
    webpages: WebpageResult[]
}

interface WebpageResult {
    url: string
    scanStatus: ScanStatus
    errorMessage?: string
    rules: Rule[]
}

enum ScanStatus {
    Success = "success",
    Failed = "failed",
}

interface Rule {
    id: string
    description: string
    axeUrl: string
    wcagReferences?: WcagReferences
    checks: Check[]
}

interface WcagReferences {
    version: string,
    level: WcagLevel
    criteria: string[]
}

type WcagLevel = "A" | "AA" | "AAA"

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

export {
    AppProperties,
    ScanJob,
    WebsiteResult,
    ScanStatus,
    WebpageResult,
    Rule,
    WcagReferences,
    WcagLevel,
    Check,
    CheckType,
    Impact,
    Element,
    CheckElement
}