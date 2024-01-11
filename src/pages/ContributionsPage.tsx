import {
  Webpage,
  WebpageControllerService,
  Website,
  WebsiteControllerService
} from "../lib/api-client";
import {Helmet} from "react-helmet-async";
import React, {useContext, useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";
import {Alert, CircularProgress} from "@mui/material";
import TableContainer from "@mui/material/TableContainer";
import Paper from "@mui/material/Paper";
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import {convertStatusToString, convertTimestampToLocalDate} from "../util/converter.ts";
import {convertWebpageToLink, convertWebsiteToLink} from "../util/formatter.tsx";
import {isWebpage, isWebsite} from "../util/typeguards.ts";

function ContributionsRow(props: { data: Website | Webpage }) {
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

function ContributionsTable(props: { contentHeader: string, data: Website[] | Webpage[] }) {
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

function ContributionsPage() {
  const {t} = useTranslation();
  const [websites, setWebsites] = useState<Website[]|undefined>();
  const [webpages, setWebpages] = useState<Webpage[]|undefined>();
  const [websiteError, setWebsiteError] = useState<string|undefined>();
  const [webpageError, setWebpageError] = useState<string|undefined>();
  const {authentication} = useContext(AuthenticationContext);

  const loadWebsites = () => {
    WebsiteControllerService.getWebsites(false, false)
      .then((websites) => {
        setWebsites(websites.content.filter((website) => website.user.id === authentication.user.id));
      })
      .catch(() => setWebsiteError(t('ContributionsPage.loadError')));
  }

  const loadWebspages = () => {
    WebpageControllerService.getWebpages(false, false)
      .then((webpages) => {
        setWebpages(webpages.filter((webpage) => webpage.user.id === authentication.user.id));
      })
      .catch(() => setWebpageError(t('ContributionsPage.loadError')));
  }

  useEffect(() => {
    loadWebsites();
    loadWebspages();
  }, []);

  return (
    <>
      <Helmet>
        <title>{t("ContributionsPage.title")} - {t("General.title")}</title>
      </Helmet>
      <h1>{t("ContributionsPage.title")}</h1>
      <p>{t("ContributionsPage.text")}</p>

      <h2>{t("ContributionsPage.titleWebsites")}</h2>
      {websites ? (
        websites.length ? (
          <ContributionsTable
            contentHeader={t("ContributionsPage.tableHeaderWebsite")}
            data={websites}
          />
        ) : (
          <p>{t("ContributionsPage.nothingFound")}</p>
        )
      ) : (
        websiteError ? (
          <Alert sx={{ my: 1 }} severity="error">{websiteError}</Alert>
        ) : (
          <CircularProgress size="1.5rem" sx={{color: "background"}}/>
        )
      )}

      <h2>{t("ContributionsPage.titleWebpages")}</h2>
      {webpages ? (
        webpages.length ? (
          <ContributionsTable
            contentHeader={t("ContributionsPage.tableHeaderWebpage")}
            data={webpages}
          />
        ) : (
          <p>{t("ContributionsPage.nothingFound")}</p>
        )
      ) : (
        webpageError ? (
          <Alert sx={{ my: 1 }} severity="error">{webpageError}</Alert>
        ) : (
          <CircularProgress size="1.5rem" sx={{color: "background"}}/>
        )
      )}
    </>
  )
}

export default ContributionsPage;
