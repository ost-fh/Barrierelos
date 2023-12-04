import {ScanJob} from "../model.js";

function getEmptyScanJob(): ScanJob {
    return {
        jobId: 0,
        jobTimestamp: "",
        modelVersion: "",
        locale: "en",
        webpagePaths: [],
        websiteBaseUrl: ""
    }
}

export {getEmptyScanJob}
