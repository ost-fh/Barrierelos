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
    websiteBaseUrl: string
    webpagePaths: string[]
}

interface WebsiteResult {
    modelVersion: string
    jobId: number
    website: string
    scanTimestamp: string
    scanStatus: ScanStatus
    errorMessage?: string
    webpages: WebpageResult[]
}

interface WebpageResult {
    path: string
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

export {
    AppProperties,
    ScanJob,
    WebsiteResult,
    ScanStatus,
    WebpageResult,
    Rule,
    Check,
    CheckType,
    Impact,
    Element,
    CheckElement
}