import {WebsiteReportData} from "../../model/WebsiteReportData.ts";
import {WebpageReportData} from "../../model/WebpageReportData.ts";
import {UserReportData} from "../../model/UserReportData.ts";
import {ReportMessage} from "../../lib/api-client";
import {useTranslation} from "react-i18next";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import ReportRow from "./ReportRow.tsx";

export default function ReportTable(props: { contentHeader: string, data: WebsiteReportData | WebpageReportData | UserReportData, onMessageSent: (reportMessage: ReportMessage) => void }) {
  const { contentHeader, data, onMessageSent } = props;
  const {t} = useTranslation();

  return (
    <TableContainer component={Paper}>
      <Table aria-label="collapsible table">
        <TableHead>
          <TableRow>
            <TableCell align="left">{t("ReportsPage.tableHeaderDate")}</TableCell>
            <TableCell align="left">{contentHeader}</TableCell>
            <TableCell align="left">{t("ReportsPage.tableHeaderInitiator")}</TableCell>
            <TableCell align="left">{t("ReportsPage.tableHeaderReason")}</TableCell>
            <TableCell align="left">{t("ReportsPage.tableHeaderStatus")}</TableCell>
            <TableCell />
          </TableRow>
        </TableHead>
        <TableBody>
          {data.reports.map((report) => (
            <ReportRow
              key={report.report.id}
              reportId={report.report.id}
              report={report}
              data={data}
              messages={data.messages}
              users={data.users}
              onMessageSent={onMessageSent}
            />
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
