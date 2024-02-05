import {CheckResult} from "axe-core";
import {getEmptyRelatedNode} from "./relatedNode.js";

function getEmptyCheckResult(): CheckResult {
    return {
        id: "",
        impact: "",
        message: "",
        data: undefined,
    }
}

function getPopulatedCheckResult() {
    const checkResult: CheckResult = getEmptyCheckResult()
    checkResult.relatedNodes = [
        getEmptyRelatedNode(),
        getEmptyRelatedNode(),
    ]
    return checkResult
}

export {getEmptyCheckResult, getPopulatedCheckResult}
