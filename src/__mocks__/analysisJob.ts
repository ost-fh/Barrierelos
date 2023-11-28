import {AnalysisJob} from "../model.js";

function getEmptyAnalysisJob(): AnalysisJob {
    return {
        jobId: 0,
        jobTimestamp: "",
        modelVersion: "",
        webpagePaths: [],
        websiteBaseUrl: ""
    }
}

export {getEmptyAnalysisJob}
