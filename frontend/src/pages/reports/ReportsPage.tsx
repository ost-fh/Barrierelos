import "./ReportsPage.css"
import {Alert, CircularProgress} from "@mui/material";
import {useTranslation} from "react-i18next";
import {useContext, useEffect, useRef, useState} from "react";
import {
  ReportMessage,
  UserReportControllerService,
  UserReportsMessage,
  WebpageReportControllerService,
  WebpageReportsMessage,
  WebsiteReportControllerService,
  WebsiteReportsMessage
} from "../../lib/api-client";
import {AuthenticationContext} from "../../context/AuthenticationContext.ts";
import {Helmet} from "react-helmet-async";
import {UserReportData} from "../../model/UserReportData.ts";
import {WebsiteReportData} from "../../model/WebsiteReportData.ts";
import {WebpageReportData} from "../../model/WebpageReportData.ts";
import {POLLING_INTERVAL} from "../../util/constants.ts";
import ReportTable from "./ReportTable.tsx";

function ReportsPage() {
  const {t} = useTranslation();
  const [websiteData, setWebsiteData] = useState<WebsiteReportData|undefined>();
  const [webpageData, setWebpageData] = useState<WebpageReportData|undefined>();
  const [userData, setUserData] = useState<UserReportData|undefined>();
  const [websiteError, setWebsiteError] = useState<string|undefined>();
  const [webpageError, setWebpageError] = useState<string|undefined>();
  const [userError, setUserError] = useState<string|undefined>();
  const [isPollingEnabled, setIsPollingEnabled] = useState(true);
  const {authentication} = useContext(AuthenticationContext);
  const timerIdRef = useRef<NodeJS.Timeout|null>(null);

  const loadWebsiteData = () => {
    WebsiteReportControllerService.getWebsiteReportsForUser(authentication.user ? authentication.user.id : 0)
      .then((data: WebsiteReportsMessage) => {
        setWebsiteData(new WebsiteReportData(data));
      })
      .catch(() => setWebsiteError(t('ReportsPage.loadError')));
  }

  const loadWebpageData = () => {
    WebpageReportControllerService.getWebpageReportsForUser(authentication.user ? authentication.user.id : 0)
      .then((data: WebpageReportsMessage) => {
        setWebpageData(new WebpageReportData(data));
      })
      .catch(() => setWebpageError(t('ReportsPage.loadError')));
  }

  const loadUserData = () => {
    UserReportControllerService.getUserReportsForUser(authentication.user ? authentication.user.id : 0)
      .then((data: UserReportsMessage) => {
        setUserData(new UserReportData(data));
      })
      .catch(() => setUserError(t('ReportsPage.loadError')));
  }

  const loadData = () => {
    loadWebsiteData();
    loadWebpageData();
    loadUserData();
  }

  useEffect(() => loadData(), []);

  useEffect(() => {
    const pollingCallback = () => loadData();

    const startPolling = () => {
      if(timerIdRef.current === null) {
        timerIdRef.current = setInterval(pollingCallback, POLLING_INTERVAL);
      }
    };

    const stopPolling = () => {
      if(timerIdRef.current) {
        clearInterval(timerIdRef.current);
        timerIdRef.current = null;
      }
    };

    if(isPollingEnabled) {
      startPolling();
    }
    else {
      stopPolling();
    }

    return () => {
      stopPolling();
    }
  }, [isPollingEnabled]);

  useEffect(() => {
    if(websiteError || webpageError || userError) {
      setIsPollingEnabled(false);
    }
  }, [websiteError, webpageError, userError])

  const onWebsiteMessageSent = (reportMessage: ReportMessage) => {
    if(websiteData) {
      websiteData.messages.push(reportMessage);
      setWebsiteData({...websiteData});  // to cause a state change
    }
  }

  const onWebpageMessageSent = (reportMessage: ReportMessage) => {
    if(webpageData) {
      webpageData.messages.push(reportMessage);
      setWebpageData({...webpageData});  // to cause a state change
    }
  }

  const onUserMessageSent = (reportMessage: ReportMessage) => {
    if(userData) {
      userData.messages.push(reportMessage);
      setUserData({...userData});  // to cause a state change
    }
  }

  return (
    <>
      <Helmet>
        <title>{t("ReportsPage.title")} - {t("General.title")}</title>
      </Helmet>
      <h1>{t("ReportsPage.title")}</h1>
      <p>{t("ReportsPage.text")}</p>

      <h2>{t("ReportsPage.titleWebsites")}</h2>
      {websiteData ? (
        websiteData.reports.length ? (
          <ReportTable
            contentHeader={t("ReportsPage.tableHeaderWebsite")}
            data={websiteData}
            onMessageSent={onWebsiteMessageSent}
          />
        ) : (
          <p>{t("ReportsPage.nothingFound")}</p>
        )
      ) : (
        websiteError ? (
          <Alert sx={{ my: 1 }} severity="error">{websiteError}</Alert>
        ) : (
          <CircularProgress size="1.5rem" sx={{color: "background"}}/>
        )
      )}

      <h2>{t("ReportsPage.titleWebpages")}</h2>
      {webpageData ? (
        webpageData.reports.length ? (
          <ReportTable
            contentHeader={t("ReportsPage.tableHeaderWebpage")}
            data={webpageData}
            onMessageSent={onWebpageMessageSent}
          />
        ) : (
          <p>{t("ReportsPage.nothingFound")}</p>
        )
      ) : (
        webpageError ? (
          <Alert sx={{ my: 1 }} severity="error">{webpageError}</Alert>
        ) : (
          <CircularProgress size="1.5rem" sx={{color: "background"}}/>
        )
      )}

      <h2>{t("ReportsPage.titleUsers")}</h2>
      {userData ? (
        userData.reports.length ? (
            <ReportTable
              contentHeader={t("ReportsPage.tableHeaderUser")}
              data={userData}
              onMessageSent={onUserMessageSent}
            />
        ) : (
          <p>{t("ReportsPage.nothingFound")}</p>
        )
      ) : (
        userError ? (
          <Alert sx={{ my: 1 }} severity="error">{userError}</Alert>
        ) : (
          <CircularProgress size="1.5rem" sx={{color: "background"}}/>
        )
      )}

      <br/>
    </>
  )
}

export default ReportsPage;
