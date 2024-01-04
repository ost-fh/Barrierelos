import {ScanJob, WebpageScan} from "../../lib/api-client";
import {useTranslation} from "react-i18next";
import {Box, Chip, Paper, Stack} from "@mui/material";
import {ParseKeys} from "i18next";


function ScanInfosTab(props: { webpageScans: WebpageScan[], scanJob: ScanJob | undefined }) {
  const {t, i18n} = useTranslation();

  const webpageScans = props.webpageScans
  const scanJob = props.scanJob

  const webpageScanInfos = webpageScans.map(webpageScan => (
    <WebpageScanInfo key={webpageScan.webpage.id} webpageScan={webpageScan}/>
  ))

  return (
    <>
      <h2>{t("WebsitePage.ScanInfosTab.scanInfosHeader")}</h2>
      <span>
        {t("WebsitePage.ScanInfosTab.lastScannedAtLabel")}:
        {scanJob !== undefined
          ? new Date(scanJob.jobTimestamp.toString()).toLocaleString(i18n.language)
          : t("WebsitePage.ScanInfosTab.ScanStatus.notYetScanned")
        }
      </span>

      <h3>{t("WebsitePage.ScanInfosTab.webpagesHeader")}</h3>
      <Stack>
        {webpageScanInfos}
      </Stack>
    </>
  )

  function WebpageScanInfo(props: { webpageScan: WebpageScan }) {
    const webpage = props.webpageScan.webpage
    const result = props.webpageScan.webpageResult
    const webpageScanStatus = result?.scanStatus !== undefined
      ? t(`WebsitePage.ScanInfosTab.ScanStatus.${result.scanStatus.toUpperCase()}` as ParseKeys)
      : t("WebsitePage.ScanInfosTab.ScanStatus.notYetScanned")

    return (
      <Box>
        <Paper key={webpage.id} sx={{padding: "0.74rem", width: "100%"}} variant="outlined">
          <span>{webpage.displayUrl}</span>
          <Chip label={`${t("WebsitePage.ScanInfosTab.ScanStatus.label")}: ${webpageScanStatus}`}/>
          {result?.errorMessage !== undefined ? (
            <div>
              <span>{t("WebsitePage.ScanInfosTab.technicalErrorLabel")}:</span>
              <Paper sx={{display: "table", padding: "0.75rem", width: "100%"}} lang="en" variant="outlined">
                {result?.errorMessage}
              </Paper>
            </div>
          ) : null}
        </Paper>
      </Box>
    )
  }
}


export default ScanInfosTab
