import {User, UserReport, Webpage, WebpageReport, Website, WebsiteReport} from "./lib/api-client";

export function isWebsiteReport(report: WebsiteReport | WebpageReport | UserReport): report is WebsiteReport {
  return ("websiteId" in report);
}

export function isWebpageReport(report: WebsiteReport | WebpageReport | UserReport): report is WebpageReport {
  return ("webpageId" in report);
}

export function isUserReport(report: WebsiteReport | WebpageReport | UserReport): report is UserReport {
  return ("userId" in report);
}

export function isWebsite(subject: Website | Webpage | User): subject is Website {
  return ("domain" in subject);
}

export function isWebpage(subject: Website | Webpage | User): subject is Webpage {
  return ("displayUrl" in subject);
}

export function isUser(subject: Website | Webpage | User): subject is User {
  return ("username" in subject);
}
