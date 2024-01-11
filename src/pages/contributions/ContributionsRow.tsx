import {Webpage, Website} from "../../lib/api-client";
import {useTranslation} from "react-i18next";
import {convertStatusToString, convertTimestampToLocalDate} from "../../util/converter.ts";
import React from "react";
import {isWebpage, isWebsite} from "../../util/typeguards.ts";
import {convertWebpageToLink, convertWebsiteToLink} from "../../util/formatter.tsx";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";

export default function ContributionsRow(props: { data: Website | Webpage }) {
  const { data } = props;
  const {t} = useTranslation();

  const dateText = convertTimestampToLocalDate(data.created);
  const statusText = convertStatusToString("" + data.status, t);
  let contentText: React.JSX.Element | string = "unknown";

  if(isWebsite(data)) {
    contentText = convertWebsiteToLink(data);
  }
  else if(isWebpage(data)) {
    contentText = convertWebpageToLink(data);
  }

  return (
    <>
      <TableRow>
        <TableCell align="left" sx={{ whiteSpace: 'nowrap'}}>{dateText}</TableCell>
        <TableCell align="left" sx={{ width: "100%" }}>{contentText}</TableCell>
        <TableCell align="left" sx={{ minWidth: "200px" }}>{statusText}</TableCell>
      </TableRow>
    </>
  );
}
