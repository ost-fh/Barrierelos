import {WebsiteScan} from "../../lib/api-client"
import {
  Card,
  CardContent,
  Chip,
  Grid,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip,
  Typography,
} from "@mui/material"
import "./OverviewTab.css"
import {useTranslation} from "react-i18next"
import HelpIcon from "@mui/icons-material/Help"
import PerceivableIcon from "@mui/icons-material/Sensors"
import {mapWebsiteTag} from "../../util/tags.ts"
import {ParseKeys} from "i18next";
import OperableIcon from "@mui/icons-material/Keyboard";
import UnderstandableIcon from "@mui/icons-material/Psychology";
import RobustIcon from "@mui/icons-material/FitnessCenter";
import {ReactElement} from "react";
import ReportComponent from "../../components/ReportComponent.tsx";
import {Link} from "react-router-dom";

function OverviewTab(props: { websiteScan: WebsiteScan }) {
  const {t, i18n} = useTranslation();
  const websiteScan: WebsiteScan = props.websiteScan

  const tags = websiteScan.website.tags.map(websiteTag => (
    <Chip key={websiteTag.id} label={mapWebsiteTag(websiteTag)} variant="outlined"/>
  ))

  return (
    <>
      {websiteScan.websiteStatistic ? (
        <>
          <Link to="/faq#wcag-explanation">{t("WebsitePage.OverviewTab.whatIsTheBarrierelosScoreLabel")}</Link>

          <h2>{t("WebsitePage.OverviewTab.websiteDetailsHeader")}</h2>
          <Stack spacing={2}>
            <div>{t("WebsitePage.OverviewTab.addedOnLabel")}: {new Date(websiteScan.website.created).toLocaleString(i18n.language)}</div>
            <div>{t("WebsitePage.OverviewTab.categoryLabel")}: {t(`WebsiteCategories.${websiteScan.website.category}` as ParseKeys)}</div>
            {tags.length !== 0 ? (
              <div>{t("WebsitePage.OverviewTab.tagsLabel")}: {tags}</div>
            ) : null}
          </Stack>

          <h2>{t("WebsitePage.OverviewTab.violationsPerWcagPrincipleHeader")}</h2>
          <Grid container spacing={2} mb={2}>
            <WcagPrincipleGridCard
              principle={t("WebsitePage.OverviewTab.violationsPerWcagPrinciplePerceivableLabel")}
              icon={(<PerceivableIcon sx={{fontSize: 80}}/>)}
              count={getViolationsPerPrinciple("perceivable", websiteScan)}
            />
            <WcagPrincipleGridCard
              principle={t("WebsitePage.OverviewTab.violationsPerWcagPrincipleOperableLabel")}
              icon={(<OperableIcon sx={{fontSize: 80}}/>)}
              count={getViolationsPerPrinciple("operable", websiteScan)}
            />
            <WcagPrincipleGridCard
              principle={t("WebsitePage.OverviewTab.violationsPerWcagPrincipleUnderstandableLabel")}
              icon={(<UnderstandableIcon sx={{fontSize: 80}}/>)}
              count={getViolationsPerPrinciple("understandable", websiteScan)}
            />
            <WcagPrincipleGridCard
              principle={t("WebsitePage.OverviewTab.violationsPerWcagPrincipleRobustLabel")}
              icon={(<RobustIcon sx={{fontSize: 80}}/>)}
              count={getViolationsPerPrinciple("robust", websiteScan)}
            />
          </Grid>
          <Link to="/faq#wcag-explanation">{t("WebsitePage.OverviewTab.whatIsWcagLabel")}</Link>
        </>
      ) : null}

      <h2>{t("WebsitePage.OverviewTab.webpagesHeader")}</h2>
      <TableContainer component={Paper} variant="outlined" sx={{maxWidth: "1200px"}}>
        <Table
          aria-label={t("WebsitePage.OverviewTab.webpagesTableAriaLabel")}
        >
          <TableHead>
            <TableRow>
              <TableCell sx={{minWidth: "150px"}}>{t("WebsitePage.OverviewTab.webpageUrl")}</TableCell>
              {websiteScan.websiteStatistic ? (
                <>
                  <TableCell sx={{width: "15%", minWidth: "105px"}}>{t("General.barrierelosScore")}</TableCell>
                  <TableCell sx={{width: "15%", minWidth: "105px"}}>
                    <Tooltip describeChild title={t("WebsitePage.OverviewTab.webpageWeightTooltip")}>
                      <Stack direction="row" alignItems="center" gap={1} useFlexGap flexWrap="wrap">
                        {t("General.webpageWeight")}
                        <HelpIcon/>
                      </Stack>
                    </Tooltip>
                  </TableCell>
                  <TableCell sx={{width: "15%", minWidth: "105px", justifyContent: "flex-start"}}>{t("WebsitePage.report")}</TableCell>
                </>
              ) : null}
            </TableRow>
          </TableHead>
          <TableBody>
            {websiteScan.webpageScans.map((webpageScan) => (
              <TableRow
                key={webpageScan.id}
                sx={{"&:last-child td, &:last-child th": {border: 0}}}
              >
                <TableCell sx={{overflowWrap: "anywhere"}}>{webpageScan.webpage.displayUrl}</TableCell>
                {webpageScan.webpageStatistic ? (
                  <>
                    <TableCell>{Math.round(webpageScan.webpageStatistic.score ?? 0)}</TableCell>
                    <TableCell>{Math.round(webpageScan.webpageStatistic.weight * 100)}%</TableCell>
                    <TableCell>
                      <ReportComponent
                        subject={webpageScan.webpage}
                        fullWidth={false}
                      />
                    </TableCell>
                  </>
                ) : null}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </>
  )
}

function WcagPrincipleGridCard(props: {
  principle: string,
  icon: ReactElement
  count: number
}) {
  return (
    <Grid item xs={12} sm={"auto"}>
      <Card variant="outlined">
        <CardContent>
          <Stack direction="row" spacing={2} justifyContent="center">
            <Typography align="center" sx={{fontSize: 80}} width={"auto"}>
              {props.count}
            </Typography>
            <Grid direction="column" container alignItems="center" width="fit-content">
              {props.icon}
              <Typography align="center">
                {props.principle}
              </Typography>
            </Grid>
          </Stack>
        </CardContent>
      </Card>
    </Grid>
  )
}

function getViolationsPerPrinciple(principle: string, websiteScan: WebsiteScan): number {
  let id: string
  switch (principle) {
    case "perceivable":
      id = "1"
      break
    case "operable":
      id = "2"
      break
    case "understandable":
      id = "3"
      break
    case "robust":
      id = "4"
      break
    default:
      throw Error(`{principle} is not a valid WCAG principle`)
  }

  return websiteScan.webpageScans
    .flatMap(webpageScan => webpageScan.webpageResult?.rules
      ?.filter(rule => rule.wcagReferences && rule.wcagReferences.criteria
        .some(criterion => criterion.startsWith(id))
      )
      ?.map(rule => rule.checks)
      .flatMap(checks => checks
        .map(check => check.violatedCount)
      )
      .reduce((a, b) => a + b, 0)
    ).reduce((a, b) => {
      return (a ?? 0) + (b ?? 0)
    }, 0) ?? 0
}

export default OverviewTab
