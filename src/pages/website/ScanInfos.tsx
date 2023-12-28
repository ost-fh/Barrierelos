import {ScanJob, WebpageResult, WebpageScan} from "../../lib/api-client";
import {useTranslation} from "react-i18next";
import {Chip, Paper, Stack} from "@mui/material";


function ScanInfos(props: { webpageScans: WebpageScan[], scanJob: ScanJob | undefined }) {
  const {t, i18n} = useTranslation();

  const webpageScans = props.webpageScans
  const scanJob = props.scanJob

  const webpageScanInfos = webpageScans.map(webpageResult => {
    const webpage = webpageResult.webpage
    const result = webpageResult.webpageResult
    const scanStatus = result?.scanStatus !== undefined
      ? translateScanStatus(result.scanStatus)
      : t("WebsitePage.ScanInfos.ScanStatus.notYetScanned")

    return (
      <div key={webpage.id}>
        <span>{webpage.displayUrl}</span>
        <Chip label={`${t("WebsitePage.ScanInfos.scanStatusLabel")}: ${scanStatus}`}/>
        {result?.errorMessage !== undefined ? (
          <div>
            <span>{t("WebsitePage.ScanInfos.technicalErrorLabel")}:</span>
            <Paper sx={{display: "table", padding: "1rem"}}>{result?.errorMessage}</Paper>
          </div>
        ) : null}
      </div>
    )
  })
  return (
    <>
      <h2>{t("WebsitePage.ScanInfos.scanInfosHeader")}</h2>
      <span>
        {t("WebsitePage.ScanInfos.lastScannedAtLabel")}:
        {scanJob !== undefined
          ? new Date(scanJob.jobTimestamp.toString()).toLocaleString(i18n.language)
          : t("WebsitePage.ScanInfos.ScanStatus.notYetScanned")
        }
      </span>

      <h3>{t("WebsitePage.ScanInfos.webpagesHeader")}</h3>
      <Stack spacing={2}>
        {webpageScanInfos}
      </Stack>
    </>
  )

  function translateScanStatus(scanStatus: WebpageResult.scanStatus): string {
    // @ts-expect-error: Interface is automatically generated from the translation file and consists of many union types
    return t(`WebsitePage.ScanInfos.ScanStatus.${scanStatus.toUpperCase()}`)
  }
}


export default ScanInfos
