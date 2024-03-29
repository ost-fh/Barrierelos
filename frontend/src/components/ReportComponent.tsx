import ReportDialog from "../components/ReportDialog.tsx";
import {useContext, useState} from "react";
import {Alert, Box, Button, Typography} from "@mui/material";
import {
  Report,
  ReportMessage,
  ReportMessageControllerService,
  User,
  UserReport,
  UserReportControllerService,
  Webpage,
  WebpageReport,
  WebpageReportControllerService,
  Website,
  WebsiteReport,
  WebsiteReportControllerService
} from "../lib/api-client";
import {useTranslation} from "react-i18next";
import {isUser, isWebpage, isWebsite} from "../util/typeguards.ts";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";
import {useNavigate} from "react-router-dom";
import reasonEnum = Report.reason;
import state = Report.state;

function ReportComponent(props: { subject: Website|Webpage|User, fullWidth: boolean }) {
  const { subject, fullWidth } = props;
  const {t} = useTranslation();
  const [alreadyReported, setAlreadyReported] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [success, setSuccess] = useState(false);
  const [error, setError] = useState(false);
  const [loaded, setLoaded] = useState(false);
  const {authentication} = useContext(AuthenticationContext);
  const navigate = useNavigate();

  const handleReport = () => {
    if(!authentication.isAuthenticated) {
      navigate("/login");
    }
    else {
      setDialogOpen(true);
    }
  }

  const onReportYes = (reason: reasonEnum, explanation: string) => {
    if(authentication.user) {
      const report: Report = {
        id: 0,
        userId: authentication.user.id,
        reason: reason,
        state: state.OPEN,
        modified: 0,
        created: 0
      }

      const reportMessage: ReportMessage = {
        id: 0,
        reportId: 0,
        userId: authentication.user.id,
        message: explanation,
        modified: 0,
        created: 0
      }

      if(isWebsite(subject)) {
        const websiteReport: WebsiteReport = {
          id: 0,
          websiteId: subject.id,
          report: report
        };
        WebsiteReportControllerService.addWebsiteReport(websiteReport)
          .then((addedWebsiteReport) => {
            reportMessage.reportId = addedWebsiteReport.report.id;
            ReportMessageControllerService.addReportMessage(reportMessage)
              .catch(() => setError(true));
          })
          .catch(() => setError(true));
      }
      else if(isWebpage(subject)) {
        const webpageReport: WebpageReport = {
          id: 0,
          webpageId: subject.id,
          report: report
        };
        WebpageReportControllerService.addWebpageReport(webpageReport)
          .then((addedWebpageReport) => {
            reportMessage.reportId = addedWebpageReport.report.id;
            ReportMessageControllerService.addReportMessage(reportMessage)
              .catch(() => setError(true));
          })
          .catch(() => setError(true));
      }
      else if(isUser(subject)) {
        const userReport: UserReport = {
          id: 0,
          userId: subject.id,
          report: report
        };
        UserReportControllerService.addUserReport(userReport)
          .then((addedUserReport) => {
            reportMessage.reportId = addedUserReport.report.id;
            ReportMessageControllerService.addReportMessage(reportMessage)
              .catch(() => setError(true));
          })
          .catch(() => setError(true));
      }

      setSuccess(true);
    }
    else
    {
      setError(true);
    }
  }

  if(authentication.isAuthenticated) {
    if (isWebsite(subject)) {
      WebsiteReportControllerService.getWebsiteReports1(subject.id)
        .then((reports: WebsiteReport[]) => {
          if (reports.find(report => report.websiteId === subject.id && authentication.user && report.report.userId === authentication.user.id)) {
            setAlreadyReported(true);
          }
          setLoaded(true);
        });
    } else if (isWebpage(subject)) {
      WebpageReportControllerService.getWebpageReports1(subject.id)
        .then((reports: WebpageReport[]) => {
          if (reports.find(report => report.webpageId === subject.id && authentication.user && report.report.userId === authentication.user.id)) {
            setAlreadyReported(true);
          }
          setLoaded(true);
        });
    } else if (isUser(subject)) {
      UserReportControllerService.getUserReports1(subject.id)
        .then((reports: UserReport[]) => {
          if (reports.find(report => report.userId === subject.id && authentication.user && report.report.userId === authentication.user.id)) {
            setAlreadyReported(true);
          }
          setLoaded(true);
        });
    }
  }

  return (
    <>
      {(!authentication.isAuthenticated || loaded) && (
        error ? (
          <Alert sx={{ width: fullWidth ? "100%" : "fit-content" }} severity="error">
            <Typography component="span" variant="body2">
              {t('ReportComponent.error')}
            </Typography>
          </Alert>
        ) : (
          success ? (
            <Alert sx={{ width: fullWidth ? "100%" : "fit-content" }} severity="success">
              <Typography component="span" variant="body2">
                {t('ReportComponent.success')}
              </Typography>
            </Alert>
          ) : (
            <>
              {alreadyReported ? (
                <Box sx={{ display: fullWidth ? "flex" : "inline-flex", flexFlow: "row", justifyContent: "flex-end" }}>
                  <Button onClick={handleReport} variant="contained" color="error" disabled={true} sx={{ width: "fit-content", alignSelf: "end" }}>
                    {t('ReportComponent.alreadyReported')}
                  </Button>
                </Box>
              ) : (
                <Box sx={{ display: fullWidth ? "flex" : "inline-flex", flexFlow: "row", justifyContent: "flex-end" }}>
                  <Button onClick={handleReport} variant="contained" color="error" disabled={dialogOpen} sx={{ width: "fit-content", alignSelf: "end" }}>
                    {t('ReportComponent.buttonReport')}
                  </Button>
                </Box>
              )}
              {dialogOpen && (
                <ReportDialog
                  subject={subject}
                  open={dialogOpen}
                  setOpen={setDialogOpen}
                  onReportNo={() => {}}
                  onReportYes={onReportYes}
                />
              )}
            </>
          )
        )
      )}
    </>
  )
}

export default ReportComponent;
