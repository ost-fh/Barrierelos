import {WebsiteTag} from "../lib/api-client";
import {t} from "i18next";

const SPECIAL_TAG_PREFIXES = [
  "Canton: ",
  "Country: ",
]

export function isSpecialTag(tag: string) {
  return SPECIAL_TAG_PREFIXES.some(prefix => tag.startsWith(prefix))
}

export function mapWebsiteTag(websiteTag: WebsiteTag | undefined): string {
  if (websiteTag === undefined)
    return ""

  const tag = websiteTag.tag.name
  if (!isSpecialTag(tag))
    return websiteTag.tag.name

  // @ts-expect-error: Interface is automatically generated from the translation file and consists of many union types
  return t(`WebsiteTags.${websiteTag.tag.name}`)
}

export function compareWebsiteTags(websiteTag1: WebsiteTag, websiteTag2: WebsiteTag) {
  const tag1 = mapWebsiteTag(websiteTag1)
  const tag2 = mapWebsiteTag(websiteTag2)
  const tag1IsSpecial = isSpecialTag(tag1)
  const tag2IsSpecial = isSpecialTag(tag2)

  if (tag1IsSpecial && tag2IsSpecial)
    return tag1.localeCompare(tag2)

  if (tag1IsSpecial)
    return -1

  if (tag2IsSpecial)
    return 1

  return tag1.localeCompare(tag2)
}