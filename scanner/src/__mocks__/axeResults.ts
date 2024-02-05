import {AxeResults} from "axe-core";
import {getPopulatedResult} from "./result.js";

function getEmptyAxeResults(): AxeResults {
    return {
        url: "",
        toolOptions: {},
        timestamp: "",
        passes: [],
        violations: [],
        inapplicable: [],
        incomplete: [],
        testEngine: {
            name: "",
            version: ""
        },
        testEnvironment: {
            userAgent: "",
            windowWidth: 0,
            windowHeight: 0
        },
        testRunner: {
            name: ""
        }
    }
}

function getPopulatedAxeResults() {
    const axeResults = getEmptyAxeResults()
    axeResults.passes.push(...[
        getPopulatedResult(),
        getPopulatedResult(),
    ])
    axeResults.violations.push(...[
        getPopulatedResult(),
        getPopulatedResult(),
    ])
    axeResults.incomplete.push(...[
        getPopulatedResult(),
        getPopulatedResult(),
    ])
    axeResults.inapplicable.push(...[
        getPopulatedResult(),
        getPopulatedResult(),
    ])
    return axeResults
}

export {getEmptyAxeResults, getPopulatedAxeResults}
