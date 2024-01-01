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
import "./Overview.css"
import {useTranslation} from "react-i18next"
import HelpIcon from "@mui/icons-material/Help"
import PerceivableIcon from "@mui/icons-material/Sensors"
import OperableIcon from "@mui/icons-material/Keyboard"
import UnderstandableIcon from "@mui/icons-material/Psychology"
import RobustIcon from "@mui/icons-material/FitnessCenter"
import {mapWebsiteTag} from "../../util/tags.ts"


function Overview(props: { websiteScan: WebsiteScan }) {
  const {t, i18n} = useTranslation();
  const websiteScan: WebsiteScan = props.websiteScan

  const tags = websiteScan.website.tags.map(websiteTag => (
    <Chip key={websiteTag.id} label={mapWebsiteTag(websiteTag)} variant="outlined"/>
  ))

  return (
    <>
      {websiteScan.websiteStatistic ? (
        <>
          <h2>{t("WebsitePage.Overview.websiteDetailsHeader")}</h2>
          <p>{t("WebsitePage.Overview.addedOnLabel")}: {new Date(websiteScan.website.created).toLocaleString(i18n.language)}</p>
          <p>{t("WebsitePage.Overview.tagsLabel")}: {tags}</p>

          <h2>{t("WebsitePage.Overview.principleViolationHeader")}</h2>
          <Grid container spacing={2}>
            <Grid item>
              <Card variant="outlined">
                <CardContent>
                  <Stack direction="row" spacing={2}>
                    <Typography align="center" sx={{fontSize: 80}}>
                      {getViolationsPerPrinciple("perceivable")}
                    </Typography>
                    <Grid direction="column" container alignItems="center" width="inherit">
                      <PerceivableIcon sx={{fontSize: 80}}/>
                      <Typography align="center">
                        {t("WebsitePage.Overview.principleViolationPerceivableLabel")}
                      </Typography>
                    </Grid>
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
            <Grid item>
              <Card variant="outlined">
                <CardContent>
                  <Stack direction="row" spacing={2}>
                    <Typography align="center" sx={{fontSize: 80}}>
                      {getViolationsPerPrinciple("operable")}
                    </Typography>
                    <Grid direction="column" container alignItems="center">
                      <OperableIcon sx={{fontSize: 80}}/>
                      <Typography align="center">
                        {t("WebsitePage.Overview.principleViolationOperableLabel")}
                      </Typography>
                    </Grid>
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
            <Grid item>
              <Card variant="outlined">
                <CardContent>
                  <Stack direction="row" spacing={2}>
                    <Typography align="center" sx={{fontSize: 80}}>
                      {getViolationsPerPrinciple("understandable")}
                    </Typography>
                    <Grid direction="column" container alignItems="center">
                      <UnderstandableIcon sx={{fontSize: 80}}/>
                      <Typography align="center">
                        {t("WebsitePage.Overview.principleViolationUnderstandableLabel")}
                      </Typography>
                    </Grid>
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
            <Grid item>
              <Card variant="outlined">
                <CardContent>
                  <Stack direction="row" spacing={2}>
                    <Typography align="center" sx={{fontSize: 80}}>
                      {getViolationsPerPrinciple("robust")}
                    </Typography>
                    <Grid direction="column" container alignItems="center">
                      <RobustIcon sx={{fontSize: 80}}/>
                      <Typography align="center">{t("WebsitePage.Overview.principleViolationRobustLabel")}</Typography>
                    </Grid>
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </>
      ) : null}

      <h2>{t("WebsitePage.Overview.webpagesHeader")}</h2>
      <TableContainer component={Paper}>
        <Table sx={{minWidth: 650}} aria-label={t("WebsitePage.Overview.webpagesTableAriaLabel")}>
          <TableHead>
            <TableRow>
              <TableCell>{t("WebsitePage.Overview.webpageUrl")}</TableCell>
              {websiteScan.websiteStatistic ? (
                <>
                  <TableCell>{t("General.barrierelosScore")}</TableCell>
                  <TableCell>
                    <Tooltip describeChild title={t("WebsitePage.Overview.webpageWeightTooltip")}>
                      <Stack direction="row" alignItems="center" gap={1}>
                        {t("General.webpageWeight")}
                        <HelpIcon/>
                      </Stack>
                    </Tooltip>
                  </TableCell>
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
                <TableCell component="th" scope="row">{webpageScan.webpage.displayUrl}</TableCell>
                {webpageScan.webpageStatistic ? (
                  <>
                    <TableCell>{Math.round(webpageScan.webpageStatistic.score ?? 0)}</TableCell>
                    <TableCell>{Math.round(webpageScan.webpageStatistic.weight * 100)}%</TableCell>
                  </>
                ) : null}
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </>
  )

  function getViolationsPerPrinciple(principle: string) {
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
      }, 0)
  }
}

export default Overview
