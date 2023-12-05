import {WebsiteDetails} from "../../lib/api-client";
import {Paper, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tooltip} from "@mui/material";
import "./WebsiteOverview.css"
import {useTranslation} from "react-i18next";
import HelpIcon from '@mui/icons-material/Help';


interface WebsiteOverviewProps {
  websiteDetails: WebsiteDetails
}

function WebsiteOverview(params: WebsiteOverviewProps) {
  const {t} = useTranslation();
  const websiteDetails: WebsiteDetails = params.websiteDetails

  return (
    <>
      <h2>{t("WebsitePage.Overview.WepbagesHeader")}</h2>
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
                        {t("WebsitePage.Overview.WebpageWeight")}
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
                sx={{'&:last-child td, &:last-child th': {border: 0}}}
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
}

export default WebsiteOverview