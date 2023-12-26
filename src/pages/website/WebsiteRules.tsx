import {Rule, WebsiteScanMessage} from "../../lib/api-client";
import {Accordion, AccordionDetails, AccordionSummary, Chip, Typography} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {useTranslation} from "react-i18next";
import "./WebsiteRules.css"


function WebsiteRules(props: { websiteScan: WebsiteScanMessage }) {
  const {t} = useTranslation();

  const websiteScan = props.websiteScan
  const webpages = websiteScan.webpageScans?.map(webpageScan => {
    const rules = webpageScan.webpageResult?.rules
      ?.filter(rule => rule.checks.some(check => check.violatedCount > 0))
      ?.map(rule => (
          <RuleElement rule={rule} key={rule.id}/>
        )
      )
    if (rules === undefined || rules.length === 0) return null

    return (
      <>
        <Accordion>
          <AccordionSummary
            expandIcon={<ExpandMoreIcon/>}
            aria-controls="panel1a-content"
            id="panel1a-header"
          >
            <Typography>Webpage: {webpageScan.webpage.displayUrl}</Typography>
            {webpageScan.webpageStatistic ? (
              <>
                <Chip
                  label={`${t("General.barrierelosScore")}: ${Math.round(webpageScan.webpageStatistic?.score ?? 0)}`}/>
                <Chip
                  label={`${t("General.webpageWeight")}: ${Math.round(webpageScan.webpageStatistic.weight * 100)}%`}/>
              </>
            ) : null}
          </AccordionSummary>
          <AccordionDetails>
            <ul>
              {rules}
            </ul>
          </AccordionDetails>
        </Accordion>
      </>
    )
  })
  return (
    <>
      {webpages}
    </>
  )
}

function RuleElement(props: { rule: Rule }) {
  const rule = props.rule
  const violatedCount = rule.checks.map((check) => check.violatedCount).reduce((a, b) => a + b, 0)
  if (violatedCount === 0) return null
  const testedCount = rule.checks.map((check) => check.testedCount).reduce((a, b) => a + b, 0)
  const violationPercentage = testedCount !== 0 ? roundTo(violatedCount / testedCount * 100, 2) : "0"
  return (
    <li key={rule.id}>
      <p>
        {rule.description}
      </p>
      <p>
        <a href={rule.axeUrl}>Learn more</a>
      </p>
      <p>
        Violations: {violatedCount} ({violationPercentage}% of elements tested for this rule)
      </p>
      {rule.wcagReferences ? (
        <>
          <p>
            WCAG - Version: {rule.wcagReferences.version} - Level: {rule.wcagReferences.level}
            - Relevant criteria: {rule.wcagReferences.criteria.join(", ")}
          </p>
        </>
      ) : null}
    </li>
  )
}

function roundTo(n: number, digits: number) {
  if (digits === undefined) {
    digits = 0;
  }

  const multiplicator = Math.pow(10, digits);
  n = parseFloat((n * multiplicator).toFixed(11));
  return Math.round(n) / multiplicator;
}

export default WebsiteRules