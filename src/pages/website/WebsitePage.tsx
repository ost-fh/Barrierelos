import {useLocation, useParams} from "react-router-dom";
import {WebpageScan, Website, WebsiteScan, WebsiteScanControllerService} from "../../lib/api-client";
import useSWR from "swr"
import {Box, Chip, Stack, Tab, Tooltip} from "@mui/material";
import {TabContext, TabList, TabPanel} from "@mui/lab";
import {SyntheticEvent, useEffect, useState} from "react";
import WebsiteOverview from "./WebsiteOverview.tsx";
import WebsiteRules from "./WebsiteRules.tsx";
import "./WebsitePage.css"
import {useTranslation} from "react-i18next";
import {Helmet} from "react-helmet-async";
import LoadingIndicator from "../../components/LoadingIndicator.tsx";
import {formatScore, getScoreClass, getScoreColor} from "../../util/formatter.ts";
import status = Website.status;

function WebsitePage() {
  const {t} = useTranslation();

  const params = useParams<WebsitePageParams>();
  if (params.websiteId === undefined) throw Error("Path param websiteId is missing")
  const {data: websiteScan, error, isLoading} = useSWR<WebsiteScan, Error>(params.websiteId, getWebsiteScan)

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

  return (
    <>
      <Helmet>
        <title>{websiteScan.website.domain} - {t("General.title")}</title>
      </Helmet>


      <Stack direction="row">
        <h1>{websiteScan.website.url}</h1>
        {websiteScan.website.status !== status.READY ? (
          <Chip label={`${t("WebsitePage.websiteStatusLabel")}: ${websiteScan.website.status}`}/>
        ) : null}
        {websiteScan.websiteStatistic ? (
          <Tooltip title={t("General.barrierelosScore")}>
            <div className={`score ${scoreClass}`} style={{background: scoreGradient}}>
              <span>{score}</span>
            </div>
          </Tooltip>
        ) : null}
      </Stack>

      <TabContext value={currentTabIndex}>
        <Box sx={{borderBottom: 1, borderColor: "divider"}}>
          <TabList onChange={handleTabChange} aria-label={t("WebsitePage.tabsAriaLabel")}>
            <Tab label={t("WebsitePage.tabOverviewLabel")} value={Tabs.OVERVIEW}/>
            {websiteScan.website.status === status.READY ? (
              <Tab label={t("WebsitePage.tabRulesLabel")} value={Tabs.RULES}/>
            ) : null}
            <Tab label={t("WebsitePage.tabScanInfosLabel")} value={Tabs.SCAN_INFOS}/>
          </TabList>
        </Box>
        <TabPanel value={Tabs.OVERVIEW}><WebsiteOverview websiteScan={websiteScan}/></TabPanel>
        {websiteScan.website.status === status.READY ? (
          <TabPanel value={Tabs.RULES}><WebsiteRules websiteScan={websiteScan}/></TabPanel>
        ) : null}
        <TabPanel value={Tabs.SCAN_INFOS}>Scan Infos</TabPanel>
      </TabContext>
    </>
  )
}

async function getWebsiteScan(id: string) {
  const websiteScan = await WebsiteScanControllerService.getWebsiteScan(parseInt(id));
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

export default WebsitePage
