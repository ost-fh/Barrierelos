import {UserReport, WebpageReport, WebsiteReport} from "./lib/api-client";

export function isWebsiteReport(report: WebsiteReport | WebpageReport | UserReport): report is WebsiteReport {
  return ("websiteId" in report);
}

export function isWebpageReport(report: WebsiteReport | WebpageReport | UserReport): report is WebpageReport {
  return ("webpageId" in report);
}

export function isUserReport(report: WebsiteReport | WebpageReport | UserReport): report is UserReport {
  return ("userId" in report);
}
