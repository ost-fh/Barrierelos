import {ReportMessage, User, Website, WebsiteReport, WebsiteReportsMessage} from "../lib/api-client";

export class WebsiteReportData {
  public reports: Array<WebsiteReport> = [];
  public messages: Array<ReportMessage> = [];
  public users: Map<number, User> = new Map();
  public websites: Map<number, Website> = new Map();

  constructor(data: WebsiteReportsMessage) {
    this.reports = data.reports;
    this.messages = data.messages;
    this.users = new Map(data.users.map(it => [it.id, it]));
    this.websites = new Map(data.websites.map(it => [it.id, it]));
  }
}
