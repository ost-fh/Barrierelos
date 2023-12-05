import {WebsiteDetails} from "../../lib/api-client";


interface WebsiteRulesProps {
  websiteDetails: WebsiteDetails
}

function WebsiteRules(params: WebsiteRulesProps) {
  const websiteDetails: WebsiteDetails = params.websiteDetails
  const webpages = websiteDetails.webpages?.map(webpageDetails => {
    const rules = webpageDetails.scanResult?.rules?.map(rule =>
      <li key={rule.id}>{rule.description}</li>
    )
    return (
      <>
        <h2>{webpageDetails.webpage.url}</h2>
        <ul>
          {rules}
        </ul>
      </>
    )
  })
  return (
    <>
      {webpages}
    </>
  )
}

export default WebsiteRules