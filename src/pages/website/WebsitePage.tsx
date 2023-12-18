import {useLocation, useParams} from "react-router-dom";
import {StatisticControllerService, Website, WebsiteScanMessage} from "../../lib/api-client";
import useSWR from "swr"
import {Box, Chip, Tab} from "@mui/material";
import {TabContext, TabList, TabPanel} from "@mui/lab";
import {SyntheticEvent, useEffect, useState} from "react";
import WebsiteOverview from "./WebsiteOverview.tsx";
import WebsiteRules from "./WebsiteRules.tsx";
import "./WebsitePage.css"
import {useTranslation} from "react-i18next";
import status = Website.status;

interface WebsitePageParams extends Record<string, string> {
  websiteId: string
}

function WebsitePage() {
  const {t} = useTranslation();

  const params = useParams<WebsitePageParams>();
  if (params.websiteId === undefined) throw Error("Path param websiteId is missing")
  const {data: websiteScan, error, isLoading} = useSWR<WebsiteScanMessage, Error>(params.websiteId, getWebsiteScan)

  const [currentTabIndex, setCurrentTabIndex] = useState("1");
  const handleTabChange = (_e: SyntheticEvent, tabIndex: string) => {
    setCurrentTabIndex(tabIndex);
  };

  // Reset currentTabIndex on navigation
  const location = useLocation()
  useEffect(() => {
    if (currentTabIndex !== "1") setCurrentTabIndex("1")
  }, [location.pathname])

  if (isLoading) return "Loading..."
  if (error) return `Error occurred:\n${error}`
  if (websiteScan === undefined) return "Couldn't load."

  return (
    <>
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
            <Tab label={t("WebsitePage.tabOverviewLabel")} value="1"/>
            {websiteScan.website.status === status.READY ? (
              <Tab label={t("WebsitePage.tabRulesLabel")} value="2"/>
            ) : null}
            <Tab label={t("WebsitePage.tabScanInfosLabel")} value="3"/>
          </TabList>
        </Box>
        <TabPanel value="1"><WebsiteOverview websiteScan={websiteScan}/></TabPanel>
        {websiteScan.website.status === status.READY ? (
          <TabPanel value="2"><WebsiteRules websiteScan={websiteScan}/></TabPanel>
        ) : null}
        <TabPanel value="3">Scan Infos</TabPanel>
      </TabContext>
    </>
  )
}

function getWebsiteScan(id: string) {
  return StatisticControllerService.getWebsiteScan(parseInt(id));
}

export default WebsitePage