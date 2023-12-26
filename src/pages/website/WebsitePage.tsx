import {useLocation, useParams} from "react-router-dom";
import {WebpageScanMessage, Website, WebsiteScanControllerService, WebsiteScanMessage} from "../../lib/api-client";
import useSWR from "swr"
import {Box, Chip, Tab} from "@mui/material";
import {TabContext, TabList, TabPanel} from "@mui/lab";
import {SyntheticEvent, useEffect, useState} from "react";
import WebsiteOverview from "./WebsiteOverview.tsx";
import WebsiteRules from "./WebsiteRules.tsx";
import "./WebsitePage.css"
import {useTranslation} from "react-i18next";
import {Helmet} from "react-helmet-async";
import LoadingIndicator from "../../components/LoadingIndicator.tsx";
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

  return (
    <>
      <Helmet>
        <title>{websiteScan.website.domain} - {t("General.title")}</title>
      </Helmet>
      <h1>
        {websiteScan.website.url}
        {websiteScan.website.status !== status.READY ? (
          <Chip label={`${t("WebsitePage.websiteStatusLabel")}: ${websiteScan.website.status}`}/>
        ) : null}
        {websiteScan.websiteStatistic ? (
          <Chip label={`${t("General.barrierelosScore")}: ${Math.round(websiteScan.websiteStatistic.score)}`}/>
        ) : null}
      </h1>
      <TabContext value={currentTabIndex}>
        <Box sx={{borderBottom: 1, borderColor: 'divider'}}>
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

function compareByWeightDescending(a: WebpageScanMessage, b: WebpageScanMessage) {
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
