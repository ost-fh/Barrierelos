import {Rule, WebsiteScan} from "../../lib/api-client";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Chip,
  Stack,
  Typography
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {useTranslation} from "react-i18next";
import "./IssuesTab.css"
import {compareStrings, formatViolationRatio} from "../../util/formatter.ts";


function IssuesTab(props: { websiteScan: WebsiteScan }) {
  const {t} = useTranslation();

  const websiteScan = props.websiteScan
  const webpages = websiteScan.webpageScans?.map(webpageScan => {
    const rules = webpageScan.webpageResult?.rules
      ?.filter(rule => rule.checks.some(check => check.violatedCount > 0))
      .sort((a, b) => a.description.localeCompare(b.description))
      .map(rule => (
        <Box key={rule.id}>
          <RuleElement rule={rule}/>
        </Box>
      ))
    if (rules === undefined || rules.length === 0) return null

    return (
      <Accordion key={webpageScan.webpage.id} variant="outlined">
        <AccordionSummary
          expandIcon={<ExpandMoreIcon/>}
          sx={{flexDirection: "row-reverse"}}
          aria-controls={`panel-${webpageScan.webpage.id}-content`}
          id={`panel-${webpageScan.webpage.id}-header`}
        >
          <Typography>{webpageScan.webpage.displayUrl}</Typography>
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
          <Stack direction="row" spacing={2} flexWrap="wrap" useFlexGap>
            {rules}
          </Stack>
        </AccordionDetails>
      </Accordion>
    )
  })
  return (
    <>
      <h2>{t("WebsitePage.IssuesTab.header")}</h2>
      {webpages}
    </>
  )

  function RuleElement(props: { rule: Rule }) {
    const rule = props.rule
    const violatedCount = rule.checks.map((check) => check.violatedCount).reduce((a, b) => a + b, 0)
    if (violatedCount === 0) return null
    const testedCount = rule.checks.map((check) => check.testedCount).reduce((a, b) => a + b, 0)
    const violationPercentage = testedCount !== 0 ? formatViolationRatio(violatedCount / testedCount * 100, 2) : "0"
    return (
      <Card sx={{maxWidth: 345}} variant="outlined">
        <CardHeader title={rule.description} lang="en"/>
        <CardContent>
          <div>{t("WebsitePage.IssuesTab.violationsLabel")}: {violatedCount}</div>
          <div>{violationPercentage}% {t("WebsitePage.IssuesTab.violationPercentageSuffix")}</div>

          <p>
            <span className="issueType">
              {t("WebsitePage.IssuesTab.issueTypeLabel")}:&nbsp;
              {rule.wcagReferences ? (
                <span>{t("WebsitePage.IssuesTab.issueTypeWcagLabel")}</span>
              ) : <span>{t("WebsitePage.IssuesTab.issueTypeBestPracticeLabel")}</span>}
            </span>
            {rule.wcagReferences ? (
              <>
                <span>{t("WebsitePage.IssuesTab.issueTypeWcagVersionLabel")}: {rule.wcagReferences.version}</span>
                <span>{t("WebsitePage.IssuesTab.issueTypeWcagLevelLabel")}: {rule.wcagReferences.level}</span>
                <span>{t("WebsitePage.IssuesTab.issueTypeWcagRelevantCriteriaLabel")}: {rule.wcagReferences.criteria.sort(compareStrings).join(", ")}
                </span>
              </>
            ) : null}
          </p>
        </CardContent>
        <CardActions disableSpacing>
          <a href={rule.axeUrl} target="_blank" rel="noreferrer">{t("WebsitePage.IssuesTab.learnMoreLinkLabel")}</a>
        </CardActions>
      </Card>
    )
  }
}

export default IssuesTab
