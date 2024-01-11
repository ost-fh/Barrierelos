import {WebsiteTag} from "../lib/api-client";
import {ParseKeys, t} from "i18next";

const REGION_TAG_PREFIXES = [
  "canton.",
  "country.",
]

export function isRegionTag(tag: string) {
  return REGION_TAG_PREFIXES.some(prefix => tag.startsWith(prefix))
}

export function mapWebsiteTag(websiteTag: WebsiteTag | undefined): string {
  if (websiteTag === undefined)
    return ""

  const tag = websiteTag.tag.name
  if (!isRegionTag(tag))
    return websiteTag.tag.name

  return t(`Tags.${websiteTag.tag.name}` as ParseKeys)
}

export function compareWebsiteTags(websiteTag1: WebsiteTag, websiteTag2: WebsiteTag) {
  const tag1 = mapWebsiteTag(websiteTag1)
  const tag2 = mapWebsiteTag(websiteTag2)
  const tag1IsSpecial = isRegionTag(tag1)
  const tag2IsSpecial = isRegionTag(tag2)

  if (tag1IsSpecial && tag2IsSpecial)
    return tag1.localeCompare(tag2)

  if (tag1IsSpecial)
    return -1

  if (tag2IsSpecial)
    return 1

  return tag1.localeCompare(tag2)
}
