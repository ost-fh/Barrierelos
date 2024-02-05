import {Webpage, Website} from "../../lib/api-client";
import {useTranslation} from "react-i18next";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import ContributionsRow from "./ContributionsRow.tsx";

export default function ContributionsTable(props: { contentHeader: string, data: Website[] | Webpage[] }) {
  const { contentHeader, data } = props;
  const {t} = useTranslation();

  return (
    <TableContainer component={Paper}>
      <Table aria-label="collapsible table">
        <TableHead>
          <TableRow>
            <TableCell align="left">{t("ContributionsPage.tableHeaderDate")}</TableCell>
            <TableCell align="left">{contentHeader}</TableCell>
            <TableCell align="left">{t("ContributionsPage.tableHeaderStatus")}</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {data.map((element) => (
            <ContributionsRow
              key={element.id}
              data={element}
            />
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
