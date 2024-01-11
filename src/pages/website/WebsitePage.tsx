import {useLocation, useParams} from "react-router-dom";
import {WebpageScan, Website, WebsiteScanControllerService, WebsiteScanMessage} from "../../lib/api-client";
import useSWR from "swr"
import {Box, Chip, Stack, Tab, Tooltip} from "@mui/material";
import {TabContext, TabList, TabPanel} from "@mui/lab";
import {SyntheticEvent, useEffect, useState} from "react";
import OverviewTab from "./OverviewTab.tsx";
import IssuesTab from "./IssuesTab.tsx";
import "./WebsitePage.css"
import {useTranslation} from "react-i18next";
import {Helmet} from "react-helmet-async";
import LoadingIndicator from "../../components/LoadingIndicator.tsx";
import {formatScore, getScoreClass, getScoreColor} from "../../util/formatter.tsx";
import ScanInfosTab from "./ScanInfosTab.tsx";
import {ParseKeys} from "i18next";
import ReportComponent from "../../components/ReportComponent.tsx";
import status = Website.status;

function WebsitePage() {
  const {t} = useTranslation();

  const params = useParams<WebsitePageParams>();
  if (params.websiteId === undefined) throw Error("Path param websiteId is missing")
  const {data: websiteScan, error, isLoading} = useSWR<WebsiteScanMessage, Error>(params.websiteId, getWebsiteScan)

  const [currentTabIndex, setCurrentTabIndex] = useState(Tabs.OVERVIEW);
  const handleTabChange = (_e: SyntheticEvent, tabIndex: Tabs) => {
    setCurrentTabIndex(tabIndex);
  };

  const location = useLocation()
  useEffect(() => {
    if (currentTabIndex !== Tabs.OVERVIEW) setCurrentTabIndex(Tabs.OVERVIEW)
  }, [location.pathname])

  if (isLoading) return (<LoadingIndicator/>)
  if (error) return `Error occurred:\n${error}`
  if (websiteScan === undefined) return "Couldn't load website."

  const score = formatScore(websiteScan.websiteStatistic?.score)
  const scoreClass = getScoreClass(websiteScan.websiteStatistic?.score)
  const scoreColor = getScoreColor(websiteScan.websiteStatistic?.score)
  const scoreGradient = `conic-gradient(${scoreColor} ${score}%, transparent 0 100%)`;

  const notPendingInitialScan = websiteScan.website.status !== status.PENDING_INITIAL

  return (
    <>
      <Helmet>
        <title>{websiteScan.website.domain} - {t("General.title")}</title>
      </Helmet>

      <Stack direction="row" alignItems="center" justifyContent="flex-start" spacing={5} padding={2}>
        <h1>{websiteScan.website.domain}</h1>
        {websiteScan.website.status !== status.READY ? (
          <Chip
            label={`${t("WebsitePage.WebsiteStatus.label")}: ${t("WebsitePage.WebsiteStatus." + websiteScan.website.status as ParseKeys)}`}/>
        ) : null}
        {websiteScan.websiteStatistic ? (
          <Box>
            <Tooltip title={t("General.barrierelosScore")}>
              <div className={`score ${scoreClass}`} style={{background: scoreGradient}}>
                <span>{score}</span>
              </div>
            </Tooltip>
          </Box>
        ) : null}
        <ReportComponent
          subject={websiteScan.website}
          fullWidth={false}
        />
      </Stack>

      <TabContext value={currentTabIndex}>
        <Box sx={{borderBottom: 1, borderColor: "divider"}}>
          <TabList onChange={handleTabChange} aria-label={t("WebsitePage.tabsAriaLabel")}>
            <Tab label={t("WebsitePage.tabOverviewLabel")} value={Tabs.OVERVIEW}/>
            {websiteScan.website.status === status.READY ? (
              <Tab label={t("WebsitePage.tabRulesLabel")} value={Tabs.RULES}/>
            ) : null}
            {websiteScan.website.status === status.READY ? (
              <Tab label={t("WebsitePage.tabScanInfosLabel")} value={Tabs.SCAN_INFOS}/>
            ) : null}
          </TabList>
        </Box>
        <TabPanel value={Tabs.OVERVIEW}>
          <OverviewTab websiteScan={websiteScan}/>
        </TabPanel>
        {notPendingInitialScan ? (
          <TabPanel value={Tabs.RULES}>
            <IssuesTab websiteScan={websiteScan}/>
          </TabPanel>
        ) : null}
        {notPendingInitialScan ? (
          <TabPanel value={Tabs.SCAN_INFOS}>
            <ScanInfosTab webpageScans={websiteScan.webpageScans} scanJob={websiteScan.websiteResult?.scanJob}/>
          </TabPanel>
        ) : null}
      </TabContext>
    </>
  )
}

async function getWebsiteScan(id: string) {
  const websiteScan = await WebsiteScanControllerService.getWebsiteScanByWebsiteId(parseInt(id));
  websiteScan.webpageScans.sort(compareByWeightDescending)
  return websiteScan
}

function compareByWeightDescending(a: WebpageScan, b: WebpageScan) {
  return (b.webpageStatistic?.weight ?? 0) - (a.webpageStatistic?.weight ?? 0)
}

interface WebsitePageParams extends Record<string, string> {
  websiteId: string
}

enum Tabs {
  OVERVIEW = "OVERVIEW",
  RULES = "RULES",
  SCAN_INFOS = "SCAN_INFOS"
}

export default WebsitePage;
