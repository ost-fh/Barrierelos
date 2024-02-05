import {ReportMessage, User, UserReport, WebpageReport, WebsiteReport} from "../../lib/api-client";
import {WebsiteReportData} from "../../model/WebsiteReportData.ts";
import {WebpageReportData} from "../../model/WebpageReportData.ts";
import {UserReportData} from "../../model/UserReportData.ts";
import {useTranslation} from "react-i18next";
import React, {useState} from "react";
import {convertReasonToString, convertStateToString, convertTimestampToLocalDate} from "../../util/converter.ts";
import {convertUserToLink, convertWebpageToLink, convertWebsiteToLink} from "../../util/formatter.tsx";
import {isUserReport, isWebpageReport, isWebsiteReport} from "../../util/typeguards.ts";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import IconButton from "@mui/material/IconButton";
import KeyboardArrowUpIcon from "@mui/icons-material/KeyboardArrowUp";
import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import Collapse from "@mui/material/Collapse";
import ReportConversation from "./ReportConversation.tsx";

export default function ReportRow(props: { reportId: number, report: WebsiteReport | WebpageReport | UserReport, data: WebsiteReportData | WebpageReportData | UserReportData, messages: ReportMessage[], users: Map<number, User>, onMessageSent: (reportMessage: ReportMessage) => void }) {
  const { reportId, report, data, messages, users, onMessageSent } = props;
  const {t} = useTranslation();
  const [open, setOpen] = useState(false);

  const initiator = users.get(report.report.userId);

  const dateText = convertTimestampToLocalDate(report.report.created);
  const initiatorText = convertUserToLink(initiator);
  const reasonText = convertReasonToString(report.report.reason, t);
  const stateText = convertStateToString(report.report.state, t);
  let contentText: React.JSX.Element | string = "unknown";

  if(data instanceof WebsiteReportData && isWebsiteReport(report)) {
    const website = data.websites.get(report.websiteId);
    contentText = convertWebsiteToLink(website);
  }
  else if(data instanceof WebpageReportData && isWebpageReport(report)) {
    const webpage = data.webpages.get(report.webpageId);
    contentText = convertWebpageToLink(webpage);
  }
  else if(data instanceof UserReportData && isUserReport(report)) {
    const user = users.get(report.userId);
    contentText = convertUserToLink(user);
  }

  const conversation = messages.filter((message) => message.reportId === reportId)

  return (
    <>
      <TableRow>
        <TableCell align="left" sx={{ whiteSpace: 'nowrap'}}>{dateText}</TableCell>
        <TableCell align="left" sx={{ width: "100%" }}>{contentText}</TableCell>
        <TableCell align="left">{initiatorText}</TableCell>
        <TableCell align="left">{reasonText}</TableCell>
        <TableCell align="left">{stateText}</TableCell>
        <TableCell>
          <IconButton
            aria-label="expand row"
            size="small"
            onClick={() => setOpen(!open)}
          >
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell sx={{ px: 1, py: 0 }} colSpan={6}>
          <Collapse in={open} timeout="auto" unmountOnExit>
            <ReportConversation
              key={reportId}
              reportId={reportId}
              conversation={conversation}
              users={users}
              onMessageSent={onMessageSent}
            />
          </Collapse>
        </TableCell>
      </TableRow>
    </>
  );
}
