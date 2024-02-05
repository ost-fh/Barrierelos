import {
  Webpage,
  WebpageControllerService,
  Website,
  WebsiteControllerService
} from "../../lib/api-client";
import {Helmet} from "react-helmet-async";
import {useContext, useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {AuthenticationContext} from "../../context/AuthenticationContext.ts";
import {Alert, CircularProgress} from "@mui/material";
import ContributionsTable from "./ContributionsTable.tsx";

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
        if(authentication.user) {
          const userId = authentication.user.id
          setWebsites(websites.content.filter((website) => website.user.id === userId));
        }
      })
      .catch(() => setWebsiteError(t('ContributionsPage.loadError')));
  }

  const loadWebspages = () => {
    WebpageControllerService.getWebpages(false, false)
      .then((webpages) => {
        if(authentication.user) {
          const userId = authentication.user.id
          setWebpages(webpages.filter((webpage) => webpage.user.id === userId));
        }
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
