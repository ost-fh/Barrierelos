import {ScanJob} from "../model.js";

function getEmptyScanJob(): ScanJob {
    return {
        jobId: 0,
        jobTimestamp: "",
        modelVersion: "",
        webpagePaths: [],
        websiteBaseUrl: ""
    }
}

export {getEmptyScanJob}
