import {WebsiteDetails} from "../../lib/api-client"
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


interface WebsiteOverviewProps {
  websiteDetails: WebsiteDetails
}

function WebsiteOverview(params: WebsiteOverviewProps) {
  const {t} = useTranslation();
  const websiteDetails: WebsiteDetails = params.websiteDetails

  return (
    <>
      {websiteDetails.scanResult ? (
        <>
          <h2>{t("WebsitePage.Overview.PrincipleViolationHeader")}</h2>
          <Stack
            direction={{xs: "column", md: "row"}}
            spacing={2}
          >
            <Card variant="outlined" sx={{minWidth: 275}}>
              <CardContent>
                <Stack direction="row" spacing={2}>
                  <Typography align="center"
                              sx={{fontSize: 80, minWidth: 100}}>{getViolationsPerPrinciple("perceivable")}</Typography>
                  <Grid direction="column" container maxWidth="sm" justifyContent="space-evenly" alignItems="center">
                    <PerceivableIcon sx={{fontSize: 80}}/>
                    <Typography
                      align="center">{t("WebsitePage.Overview.PrincipleViolationPerceivableLabel")}</Typography>
                  </Grid>
                </Stack>
              </CardContent>
            </Card>
            <Card variant="outlined" sx={{minWidth: 275}}>
              <CardContent>
                <Stack direction="row" spacing={2}>
                  <Typography align="center"
                              sx={{fontSize: 80, minWidth: 100}}>{getViolationsPerPrinciple("operable")}</Typography>
                  <Grid direction="column" container maxWidth="sm" justifyContent="space-evenly" alignItems="center">
                    <OperableIcon sx={{fontSize: 80}}/>
                    <Typography align="center">{t("WebsitePage.Overview.PrincipleViolationOperableLabel")}</Typography>
                  </Grid>
                </Stack>
              </CardContent>
            </Card>
            <Card variant="outlined" sx={{minWidth: 275}}>
              <CardContent>
                <Stack direction="row" spacing={2}>
                  <Typography align="center"
                              sx={{fontSize: 80}}>{getViolationsPerPrinciple("understandable")}</Typography>
                  <Grid direction="column" container maxWidth="sm" justifyContent="space-evenly" alignItems="center">
                    <UnderstandableIcon sx={{fontSize: 80, minWidth: 100}}/>
                    <Typography
                      align="center">{t("WebsitePage.Overview.PrincipleViolationUnderstandableLabel")}</Typography>
                  </Grid>
                </Stack>
              </CardContent>
            </Card>
            <Card variant="outlined" sx={{minWidth: 275}}>
              <CardContent>
                <Stack direction="row" spacing={2}>
                  <Typography align="center"
                              sx={{fontSize: 80, minWidth: 100}}>{getViolationsPerPrinciple("robust")}</Typography>
                  <Grid direction="column" container maxWidth="sm" justifyContent="space-evenly" alignItems="center">
                    <RobustIcon sx={{fontSize: 80}}/>
                    <Typography align="center">{t("WebsitePage.Overview.PrincipleViolationRobustLabel")}</Typography>
                  </Grid>
                </Stack>
              </CardContent>
            </Card>
          </Stack>
        </>
      ) : null}
      <h2>{t("WebsitePage.Overview.WebpagesHeader")}</h2>
      <TableContainer component={Paper}>
        <Table sx={{minWidth: 650}} aria-label={t("WebsitePage.Overview.WebpagesTableAriaLabel")}>
          <TableHead>
            <TableRow>
              <TableCell>{t("WebsitePage.Overview.WebpageUrl")}</TableCell>
              {websiteDetails.statistics ? (
                <>
                  <TableCell>{t("General.barrierelosScore")}</TableCell>
                  <TableCell>
                    <Tooltip title={t("WebsitePage.Overview.WebpageWeightTooltip")}>
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
            {websiteDetails.webpages.map((webpageDetails) => (
              <TableRow
                key={webpageDetails.id}
                sx={{"&:last-child td, &:last-child th": {border: 0}}}
              >
                <TableCell component="th" scope="row">{webpageDetails.webpage.url}</TableCell>
                {websiteDetails.statistics ? (
                  <>
                    <TableCell>{webpageDetails.statistics?.score}</TableCell>
                    <TableCell>10</TableCell>
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

    return websiteDetails.webpages
      .flatMap(webpageDetails => webpageDetails.scanResult?.rules
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