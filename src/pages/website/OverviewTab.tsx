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
  useMediaQuery,
  useTheme,
} from "@mui/material"
import "./OverviewTab.css"
import {useTranslation} from "react-i18next"
import HelpIcon from "@mui/icons-material/Help"
import PerceivableIcon from "@mui/icons-material/Sensors"
import OperableIcon from "@mui/icons-material/Keyboard"
import UnderstandableIcon from "@mui/icons-material/Psychology"
import RobustIcon from "@mui/icons-material/FitnessCenter"
import {mapWebsiteTag} from "../../util/tags.ts"


function OverviewTab(props: { websiteScan: WebsiteScan }) {
  const {t, i18n} = useTranslation();
  const websiteScan: WebsiteScan = props.websiteScan

  const theme = useTheme();
  const isSmallScreen = useMediaQuery(theme.breakpoints.down("sm"));

  const tags = websiteScan.website.tags.map(websiteTag => (
    <Chip key={websiteTag.id} label={mapWebsiteTag(websiteTag)} variant="outlined"/>
  ))

  return (
    <>
      {websiteScan.websiteStatistic ? (
        <>
          <h2>{t("WebsitePage.OverviewTab.websiteDetailsHeader")}</h2>
          <Stack spacing={2}>
            <div>{t("WebsitePage.OverviewTab.addedOnLabel")}: {new Date(websiteScan.website.created).toLocaleString(i18n.language)}</div>
            {tags.length !== 0 ? (
              <div>{t("WebsitePage.OverviewTab.tagsLabel")}: {tags}</div>
            ) : null}
          </Stack>

          <h2>{t("WebsitePage.OverviewTab.violationsPerWcagPrincipleHeader")}</h2>
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
                        {t("WebsitePage.OverviewTab.violationsPerWcagPrinciplePerceivableLabel")}
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
                        {t("WebsitePage.OverviewTab.violationsPerWcagPrincipleOperableLabel")}
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
                        {t("WebsitePage.OverviewTab.violationsPerWcagPrincipleUnderstandableLabel")}
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
                      <Typography
                        align="center">{t("WebsitePage.OverviewTab.violationsPerWcagPrincipleRobustLabel")}</Typography>
                    </Grid>
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </>
      ) : null}

      <h2>{t("WebsitePage.OverviewTab.webpagesHeader")}</h2>
      <TableContainer component={Paper} sx={{maxWidth: "1200px"}}>
        <Table
          aria-label={t("WebsitePage.OverviewTab.webpagesTableAriaLabel")}
          size={isSmallScreen ? "small" : "medium"}
        >
          <TableHead>
            <TableRow>
              <TableCell sx={{minWidth: "80px"}}>{t("WebsitePage.OverviewTab.webpageUrl")}</TableCell>
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
                <TableCell>{webpageScan.webpage.displayUrl}</TableCell>
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

export default OverviewTab
