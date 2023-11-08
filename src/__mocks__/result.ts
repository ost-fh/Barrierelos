import {Result} from "axe-core";
import {getPopulatedNodeResult} from "./nodeResult.js";

function getEmptyResult(): Result {
    return {
        description: "",
        help: "",
        helpUrl: "",
        id: "",
        tags: [],
        nodes: []
    }
}

function getPopulatedResult() {
    const result = getEmptyResult()
    result.nodes.push(...[
        getPopulatedNodeResult(),
        getPopulatedNodeResult(),
    ])
    return result
}

export {getEmptyResult, getPopulatedResult}