import {useParams} from "react-router-dom";
import {StatisticControllerService, WebsiteDetails} from "../lib/api-client";
import useSWR from "swr"

function getWebsiteDetails(id: string) {
  return StatisticControllerService.getWebsiteDetails(parseInt(id));
}

function Website() {
  const params = useParams();
  if (params.websiteId === undefined) throw Error()
  const {data: websiteDetails, error, isLoading} = useSWR<WebsiteDetails, Error>(params.websiteId, getWebsiteDetails)
  if (isLoading) return "Loading..."
  if (error) return `Error occurred:\n${error}`
  if (websiteDetails === undefined) return "Couldn't load."

  const webpages = websiteDetails.webpages?.map(webpageDetails =>
    <li key={webpageDetails.webpage.id}>{webpageDetails.webpage.url}: {webpageDetails.statistics?.score ?? "N/A"}</li>
  )
  return (
    <>
      <h1>Website Details: {websiteDetails.website.url}</h1>
      Score: {websiteDetails.statistics?.score ?? "N/A"}

      <h2>Webpages</h2>
      <ul>{webpages}</ul>
    </>
  )
}

export default Website