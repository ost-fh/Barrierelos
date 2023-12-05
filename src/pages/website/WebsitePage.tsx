import {useLocation, useParams} from "react-router-dom";
import {StatisticControllerService, Website, WebsiteDetails} from "../../lib/api-client";
import useSWR from "swr"
import {Box, Chip, Tab} from "@mui/material";
import {TabContext, TabList, TabPanel} from "@mui/lab";
import {SyntheticEvent, useEffect, useState} from "react";
import WebsiteOverview from "./WebsiteOverview.tsx";
import WebsiteRules from "./WebsiteRules.tsx";
import "./Website.css"
import status = Website.status;

async function getWebsiteDetails(id: string) {
  return StatisticControllerService.getWebsiteDetails(parseInt(id));
}

function WebsitePage() {
  const params = useParams();
  if (params.websiteId === undefined) throw Error()
  const {data: websiteDetails, error, isLoading} = useSWR<WebsiteDetails, Error>(params.websiteId, getWebsiteDetails)

  const [currentTabIndex, setCurrentTabIndex] = useState("1");
  const handleTabChange = (_e: SyntheticEvent, tabIndex: string) => {
    setCurrentTabIndex(tabIndex);
  };

  const location = useLocation()
  useEffect(() => {
    if (currentTabIndex !== "1") setCurrentTabIndex("1")
  }, [location])

  if (isLoading) return "Loading..."
  if (error) return `Error occurred:\n${error}`
  if (websiteDetails === undefined) return "Couldn't load."

  return (
    <>
      <h1>
        {websiteDetails.website.url}
        {websiteDetails.website.status !== status.READY ? (
          <Chip label={`Status: ${websiteDetails.website.status}`}/>
        ) : null}
        {websiteDetails.statistics ? (
          <Chip label={`Barrierelos-Score: ${Math.round(websiteDetails.statistics.score)}`}/>
        ) : null}
      </h1>
      <TabContext value={currentTabIndex}>
        <Box sx={{borderBottom: 1, borderColor: 'divider'}}>
          <TabList onChange={handleTabChange} aria-label="lab API tabs example">
            <Tab label="Overview" value="1"/>
            <Tab label="Rules" value="2"/>
            <Tab label="Item Three" value="3"/>
          </TabList>
        </Box>
        <TabPanel value="1"><WebsiteOverview websiteDetails={websiteDetails}/></TabPanel>
        <TabPanel value="2"><WebsiteRules websiteDetails={websiteDetails}/></TabPanel>
        <TabPanel value="3">Item Three</TabPanel>
      </TabContext>
    </>
  )
}

export default WebsitePage