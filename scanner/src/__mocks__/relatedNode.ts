import {RelatedNode} from "axe-core";

function getEmptyRelatedNode(): RelatedNode {
    return {
        html: "",
        target: [],
    }
}

export {getEmptyRelatedNode}