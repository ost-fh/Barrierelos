import {ReportMessage, User, UserReport, UserReportsMessage} from "../lib/api-client";

export class UserReportData {
  public reports: Array<UserReport> = [];
  public messages: Array<ReportMessage> = [];
  public users: Map<number, User> = new Map();

  constructor(data: UserReportsMessage) {
    this.reports = data.reports;
    this.messages = data.messages;
    this.users = new Map(data.users.map(it => [it.id, it]));
  }
}
