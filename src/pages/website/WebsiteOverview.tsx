import {WebsiteScan} from "../../lib/api-client"
import {
  Card,
  CardContent,
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
import "./WebsiteOverview.css"
import {useTranslation} from "react-i18next"
import HelpIcon from "@mui/icons-material/Help"
import PerceivableIcon from "@mui/icons-material/Sensors"
import OperableIcon from "@mui/icons-material/Keyboard"
import UnderstandableIcon from "@mui/icons-material/Psychology"
import RobustIcon from "@mui/icons-material/FitnessCenter"


function WebsiteOverview(props: { websiteScan: WebsiteScan }) {
  const {t} = useTranslation();
  const websiteScan: WebsiteScan = props.websiteScan

  return (
    <>
      {websiteScan.websiteStatistic ? (
        <>
          <h2>{t("WebsitePage.Overview.PrincipleViolationHeader")}</h2>
          <Grid container spacing={2}>
            <Grid item>
              <Card variant="outlined">
                <CardContent>
                  <Stack direction="row" spacing={2}>
                    <Typography align="center"
                                sx={{fontSize: 80}}>{getViolationsPerPrinciple("perceivable")}</Typography>
                    <Grid direction="column" container alignItems="center" width="inherit">
                      <PerceivableIcon sx={{fontSize: 80}}/>
                      <Typography
                        align="center">{t("WebsitePage.Overview.PrincipleViolationPerceivableLabel")}</Typography>
                    </Grid>
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
            <Grid item>
              <Card variant="outlined">
                <CardContent>
                  <Stack direction="row" spacing={2}>
                    <Typography align="center"
                                sx={{fontSize: 80}}>{getViolationsPerPrinciple("operable")}</Typography>
                    <Grid direction="column" container alignItems="center">
                      <OperableIcon sx={{fontSize: 80}}/>
                      <Typography
                        align="center">{t("WebsitePage.Overview.PrincipleViolationOperableLabel")}</Typography>
                    </Grid>
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
            <Grid item>
              <Card variant="outlined">
                <CardContent>
                  <Stack direction="row" spacing={2}>
                    <Typography align="center"
                                sx={{fontSize: 80}}>{getViolationsPerPrinciple("understandable")}</Typography>
                    <Grid direction="column" container alignItems="center">
                      <UnderstandableIcon sx={{fontSize: 80}}/>
                      <Typography
                        align="center">{t("WebsitePage.Overview.PrincipleViolationUnderstandableLabel")}</Typography>
                    </Grid>
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
            <Grid item>
              <Card variant="outlined">
                <CardContent>
                  <Stack direction="row" spacing={2}>
                    <Typography align="center"
                                sx={{fontSize: 80}}>{getViolationsPerPrinciple("robust")}</Typography>
                    <Grid direction="column" container alignItems="center">
                      <RobustIcon sx={{fontSize: 80}}/>
                      <Typography align="center">{t("WebsitePage.Overview.PrincipleViolationRobustLabel")}</Typography>
                    </Grid>
                  </Stack>
                </CardContent>
              </Card>
            </Grid>
          </Grid>
        </>
      ) : null}
      <h2>{t("WebsitePage.Overview.WebpagesHeader")}</h2>
      <TableContainer component={Paper}>
        <Table sx={{minWidth: 650}} aria-label={t("WebsitePage.Overview.WebpagesTableAriaLabel")}>
          <TableHead>
            <TableRow>
              <TableCell>{t("WebsitePage.Overview.WebpageUrl")}</TableCell>
              {websiteScan.websiteStatistic ? (
                <>
                  <TableCell>{t("General.barrierelosScore")}</TableCell>
                  <TableCell>
                    <Tooltip describeChild title={t("WebsitePage.Overview.WebpageWeightTooltip")}>
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


export default WebsiteOverview