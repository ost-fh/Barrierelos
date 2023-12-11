import {useLocation, useParams} from "react-router-dom";
import {StatisticControllerService, Website, WebsiteDetails} from "../../lib/api-client";
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
  const {data: websiteDetails, error, isLoading} = useSWR<WebsiteDetails, Error>(params.websiteId, getWebsiteDetails)

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
  if (websiteDetails === undefined) return "Couldn't load."

  return (
    <>
      <h1>
        {websiteDetails.website.url}
        {websiteDetails.website.status !== status.READY ? (
          <Chip label={`${t("WebsitePage.websiteStatusLabel")}: ${websiteDetails.website.status}`}/>
        ) : null}
        {websiteDetails.statistics ? (
          <Chip label={`${t("General.barrierelosScore")}: ${Math.round(websiteDetails.statistics.score)}`}/>
        ) : null}
      </h1>
      <TabContext value={currentTabIndex}>
        <Box sx={{borderBottom: 1, borderColor: 'divider'}}>
          <TabList onChange={handleTabChange} aria-label={t("WebsitePage.tabsAriaLabel")}>
            <Tab label={t("WebsitePage.tabOverviewLabel")} value="1"/>
            {websiteDetails.website.status === status.READY ? (
              <Tab label={t("WebsitePage.tabRulesLabel")} value="2"/>
            ) : null}
            <Tab label={t("WebsitePage.tabScanInfosLabel")} value="3"/>
          </TabList>
        </Box>
        <TabPanel value="1"><WebsiteOverview websiteDetails={websiteDetails}/></TabPanel>
        {websiteDetails.website.status === status.READY ? (
          <TabPanel value="2"><WebsiteRules websiteDetails={websiteDetails}/></TabPanel>
        ) : null}
        <TabPanel value="3">Scan Infos</TabPanel>
      </TabContext>
    </>
  )
}

function getWebsiteDetails(id: string) {
  return StatisticControllerService.getWebsiteDetails(parseInt(id));
}

export default WebsitePage