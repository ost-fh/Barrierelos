import {ReportMessage, User, Webpage, WebpageReport, WebpageReportsMessage} from "../lib/api-client";

export class WebpageReportData {
  public reports: Array<WebpageReport> = [];
  public messages: Array<ReportMessage> = [];
  public users: Map<number, User> = new Map();
  public webpages: Map<number, Webpage> = new Map();

  constructor(data: WebpageReportsMessage) {
    this.reports = data.reports;
    this.messages = data.messages;
    this.users = new Map(data.users.map(it => [it.id, it]));
    this.webpages = new Map(data.webpages.map(it => [it.id, it]));
  }
}
