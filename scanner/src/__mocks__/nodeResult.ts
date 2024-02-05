import {NodeResult} from "axe-core";
import {getPopulatedCheckResult} from "./checkResult.js";

function getEmptyNodeResult(): NodeResult {
    return {
        html: "",
        target: [],
        any: [],
        all: [],
        none: []
    }
}

function getPopulatedNodeResult() {
    const nodeResult = getEmptyNodeResult()
    nodeResult.all.push(...[
        getPopulatedCheckResult(),
        getPopulatedCheckResult(),
    ])
    nodeResult.any.push(...[
        getPopulatedCheckResult(),
        getPopulatedCheckResult(),
    ])
    nodeResult.none.push(...[
        getPopulatedCheckResult(),
        getPopulatedCheckResult(),
    ])
    return nodeResult
}

export {getEmptyNodeResult, getPopulatedNodeResult}
