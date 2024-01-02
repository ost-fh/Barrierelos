import {ScanJob, WebpageScan} from "../../lib/api-client";
import {useTranslation} from "react-i18next";
import {Chip, Paper, Stack} from "@mui/material";
import {translate} from "../../util/formatter.ts";


function ScanInfosTab(props: { webpageScans: WebpageScan[], scanJob: ScanJob | undefined }) {
  const {t, i18n} = useTranslation();

  const webpageScans = props.webpageScans
  const scanJob = props.scanJob

  const webpageScanInfos = webpageScans.map(webpageResult => {
    const webpage = webpageResult.webpage
    const result = webpageResult.webpageResult
    const scanStatus = result?.scanStatus !== undefined
      ? translate(t, result.scanStatus.toUpperCase())
      : t("WebsitePage.ScanInfosTab.ScanStatus.notYetScanned")

    return (
      <div key={webpage.id}>
        <span>{webpage.displayUrl}</span>
        <Chip label={`${t("WebsitePage.ScanInfosTab.scanStatusLabel")}: ${scanStatus}`}/>
        {result?.errorMessage !== undefined ? (
          <div>
            <span>{t("WebsitePage.ScanInfosTab.technicalErrorLabel")}:</span>
            <Paper sx={{display: "table", padding: "1rem"}}>{result?.errorMessage}</Paper>
          </div>
        ) : null}
      </div>
    )
  })
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
      <Stack spacing={2}>
        {webpageScanInfos}
      </Stack>
    </>
  )
}


export default ScanInfosTab
