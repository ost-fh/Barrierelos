import "./Reports.css"
import Collapse from '@mui/material/Collapse';
import IconButton from '@mui/material/IconButton';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import {
  Alert,
  Avatar,
  Box,
  Button,
  CircularProgress,
  TextField,
  Typography
} from "@mui/material";
import PersonIcon from "@mui/icons-material/Person";
import {useTranslation} from "react-i18next";
import React, {useContext, useEffect, useRef, useState} from "react";
import {
  ReportMessage,
  ReportMessageControllerService,
  User,
  UserReport,
  UserReportControllerService,
  UserReportsMessage, WebpageReport,
  WebpageReportControllerService,
  WebpageReportsMessage, WebsiteReport,
  WebsiteReportControllerService,
  WebsiteReportsMessage
} from "../lib/api-client";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";
import {Helmet} from "react-helmet-async";
import { UserReportData } from '../model/UserReportData.ts';
import {WebsiteReportData} from "../model/WebsiteReportData.ts";
import {WebpageReportData} from "../model/WebpageReportData.ts";
import {isUserReport, isWebpageReport, isWebsiteReport} from "../typeguards.ts";
import {POLLING_INTERVAL} from "../constants.ts";
import {
  convertDeletedToString, convertReasonToString,
  convertRolesToString, convertStateToString,
  convertTimestampToLocalDate,
  convertTimestampToLocalDatetime
} from "../util.ts";

function ReportMessage(props: { message: ReportMessage, user: User }) {
  const { message, user } = props;
  const {t} = useTranslation();

  return (
    <TableRow sx={{ height: '100%', "& td": { borderTop: '1px solid #e0e0e0', borderBottom: '1px solid #e0e0e0' }, "& td:first-of-type": { borderLeft: '1px solid #e0e0e0', borderTopLeftRadius: '8px', borderBottomLeftRadius: '8px' }, "& td:last-of-type": { borderRight: '1px solid #e0e0e0', borderTopRightRadius: '8px', borderBottomRightRadius: '8px' } }}>
      <TableCell padding="none" sx={{ whiteSpace: 'nowrap', backgroundColor: 'background.surface' }}>
        <Box sx={{ mr: 2, display: 'flex', flexFlow: 'column nowrap' }}>
          <Box sx={{ my: 1, ml: 1, display: 'flex', flexDirection: 'row', flexWrap: 'nowrap', justifyContent: 'left' }}>
            <Avatar sx={{ mr: 1, backgroundColor: 'secondary.light', width: 48, height: 48 }}>
              <PersonIcon sx={{ width: 32, height: 32 }} />
            </Avatar>
            <Box sx={{ display: 'flex', flexFlow: 'column nowrap', justifyContent: 'center' }}>
              <Typography component="strong" variant="subtitle2">
                {user.firstname} {user.lastname}
              </Typography>
              <Typography component="span" variant="body2">
                {convertRolesToString(user.roles, t)}
              </Typography>
            </Box>
          </Box>
          <Typography component="i" variant="caption" sx={{ ml: 1 }}>
            {t('ReportsPage.labelRegistered')}: {convertTimestampToLocalDate(user.created)}
          </Typography>
          <Typography component="i" variant="caption" sx={{ ml: 1 }}>
            {t('ReportsPage.labelStatus')}: {convertDeletedToString(user.deleted, t)}
          </Typography>
        </Box>
      </TableCell>
      <TableCell padding="none" className="heightWorkaround" sx={{ pt: 1, width: '100%' }}>
        <Box sx={{ ml: 2, mr: 1, display: 'flex', flexFlow: 'column nowrap', height: '100%' }}>
          <Typography component="p" variant="body2" sx={{ flexGrow: 1 }}>
            {message.message}
          </Typography>
          <Typography component="i" variant="caption" color={'text.hint'} sx={{ alignSelf: 'flex-end' }} >
            {convertTimestampToLocalDatetime(message.created)}
          </Typography>
        </Box>
      </TableCell>
    </TableRow>
  );
}

function ReportConversation(props: { reportId: number, conversation: ReportMessage[], users: Map<number, User>, onMessageSent: (reportMessage: ReportMessage) => void }) {
  const { reportId, conversation, users, onMessageSent } = props;
  const {t} = useTranslation();
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string|undefined>(undefined);
  const {authentication} = useContext(AuthenticationContext);

  const handleSendMessage = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    setError(undefined);
    setLoading(true);

    const reportMessage: ReportMessage = {
      id: 0,
      reportId: reportId,
      userId: authentication.user ? authentication.user.id : 0,
      message: message,
      modified: 0,
      created: 0
    }

    ReportMessageControllerService.addReportMessage(reportMessage)
      .then((reportMessage) => {
        onMessageSent(reportMessage);
        setMessage("")
      })
      .catch(() => {
        setError(t('ReportsPage.sendError'))
      })
      .finally(() => {
        setLoading(false);
      });
  }

  return (
    <Table size='small' sx={{ borderCollapse: 'separate', borderSpacing: '0 16px' }}>
      <TableBody>
        {conversation.map((message) => (
          <ReportMessage
            key={message.id}
            message={message}
            user={users.get(message.userId) as User}
          />
        ))}
        <TableRow sx={{ "& td": { border: 0 } }}>
          <TableCell padding="none" colSpan={2} sx={{ width: '100%' }}>
            <Box component="form" onSubmit={handleSendMessage}>
              <TextField
                id="message"
                name="message"
                label={t('ReportsPage.labelMessage')}
                multiline
                rows={4}
                required
                fullWidth
                onChange={(event) => setMessage(event.target.value)}
                value={message}
              />
              {error && (
                <Alert sx={{ mt: 1 }} severity="error">{error}</Alert>
              )}
              <Button type="submit" fullWidth variant="contained" sx={{ mt: 1 }}>
                {loading ? (
                  <CircularProgress size="1.5rem" sx={{color: "white"}}/>
                ) : (
                  t('ReportsPage.buttonSend')
                )}
              </Button>
            </Box>
          </TableCell>
        </TableRow>
      </TableBody>
    </Table>
  );
}

function ReportRow(props: { reportId: number, report: WebsiteReport | WebpageReport | UserReport, data: WebsiteReportData | WebpageReportData | UserReportData, messages: ReportMessage[], users: Map<number, User>, onMessageSent: (reportMessage: ReportMessage) => void }) {
  const { reportId, report, data, messages, users, onMessageSent } = props;
  const {t} = useTranslation();
  const [open, setOpen] = useState(false);

  const initiator = users.get(report.report.userId);

  const dateText = convertTimestampToLocalDate(report.report.created);
  const initiatorText = initiator ? `${initiator.firstname} ${initiator.lastname} (${initiator.username})` : "unknown";
  const reasonText = convertReasonToString(report.report.reason, t);
  const stateText = convertStateToString(report.report.state, t);
  let contentText = "unknown";

  if(data instanceof WebsiteReportData && isWebsiteReport(report)) {
    const website = data.websites.get(report.websiteId);
    contentText = website ? `${website.domain}` : contentText;
  }
  else if(data instanceof WebpageReportData && isWebpageReport(report)) {
    const webpage = data.webpages.get(report.webpageId);
    contentText = webpage ? `${webpage.displayUrl}` : contentText;
  }
  else if(data instanceof UserReportData && isUserReport(report)) {
    const user = users.get(report.userId);
    contentText = user ? `${user.firstname} ${user.lastname} (${user.username})` : "unknown";
  }

  const conversation = messages.filter((message) => message.reportId === reportId)

  return (
    <>
      <TableRow sx={{ '& > *': { borderBottom: 'unset' } }}>
        <TableCell align="left">{dateText}</TableCell>
        <TableCell align="left">{contentText}</TableCell>
        <TableCell align="left">{initiatorText}</TableCell>
        <TableCell align="left">{reasonText}</TableCell>
        <TableCell align="left">{stateText}</TableCell>
        <TableCell>
          <IconButton
            aria-label="expand row"
            size="small"
            onClick={() => setOpen(!open)}
          >
            {open ? <KeyboardArrowUpIcon /> : <KeyboardArrowDownIcon />}
          </IconButton>
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell sx={{ px: 1, py: 0 }} colSpan={6}>
          <Collapse in={open} timeout="auto" unmountOnExit>
            <ReportConversation
              key={reportId}
              reportId={reportId}
              conversation={conversation}
              users={users}
              onMessageSent={onMessageSent}
            />
          </Collapse>
        </TableCell>
      </TableRow>
    </>
  );
}

function ReportTable(props: { contentHeader: string, data: WebsiteReportData | WebpageReportData | UserReportData, onMessageSent: (reportMessage: ReportMessage) => void }) {
  const { contentHeader, data, onMessageSent } = props;
  const {t} = useTranslation();

  return (
    <TableContainer component={Paper}>
      <Table aria-label="collapsible table">
        <TableHead>
          <TableRow>
            <TableCell align="left">{t("ReportsPage.tableHeaderDate")}</TableCell>
            <TableCell align="left">{contentHeader}</TableCell>
            <TableCell align="left">{t("ReportsPage.tableHeaderInitiator")}</TableCell>
            <TableCell align="left">{t("ReportsPage.tableHeaderReason")}</TableCell>
            <TableCell align="left">{t("ReportsPage.tableHeaderStatus")}</TableCell>
            <TableCell />
          </TableRow>
        </TableHead>
        <TableBody>
          {data.reports.map((report) => (
            <ReportRow
              key={report.report.id}
              reportId={report.report.id}
              report={report}
              data={data}
              messages={data.messages}
              users={data.users}
              onMessageSent={onMessageSent}
            />
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}

function Reports() {
  const {t} = useTranslation();
  const [websiteData, setWebsiteData] = useState<WebsiteReportData|undefined>();
  const [webpageData, setWebpageData] = useState<WebpageReportData|undefined>();
  const [userData, setUserData] = useState<UserReportData|undefined>();
  const [websiteError, setWebsiteError] = useState<string|undefined>();
  const [webpageError, setWebpageError] = useState<string|undefined>();
  const [userError, setUserError] = useState<string|undefined>();
  const {authentication} = useContext(AuthenticationContext);
  const timerIdRef = useRef<NodeJS.Timeout|null>(null);
  const [isPollingEnabled, setIsPollingEnabled] = useState(true);

  const loadWebsiteData = () => {
    WebsiteReportControllerService.getWebsiteReportsForUser(authentication.user ? authentication.user.id : 0)
      .then((data: WebsiteReportsMessage) => {
        setWebsiteData(new WebsiteReportData(data));
      })
      .catch(() => setWebsiteError(t('ReportsPage.loadError')));
  }

  const loadWebpageData = () => {
    WebpageReportControllerService.getWebpageReportsForUser(authentication.user ? authentication.user.id : 0)
      .then((data: WebpageReportsMessage) => {
        setWebpageData(new WebpageReportData(data));
      })
      .catch(() => setWebpageError(t('ReportsPage.loadError')));
  }

  const loadUserData = () => {
    UserReportControllerService.getUserReportsForUser(authentication.user ? authentication.user.id : 0)
      .then((data: UserReportsMessage) => {
        setUserData(new UserReportData(data));
      })
      .catch(() => setUserError(t('ReportsPage.loadError')));
  }

  const loadData = () => {
    loadWebsiteData();
    loadWebpageData();
    loadUserData();
  }

  useEffect(() => loadData(), []);

  useEffect(() => {
    const pollingCallback = () => loadData();

    const startPolling = () => {
      if(timerIdRef.current === null) {
        timerIdRef.current = setInterval(pollingCallback, POLLING_INTERVAL);
      }
    };

    const stopPolling = () => {
      if(timerIdRef.current) {
        clearInterval(timerIdRef.current);
        timerIdRef.current = null;
      }
    };

    if(isPollingEnabled) {
      startPolling();
    }
    else {
      stopPolling();
    }

    return () => {
      stopPolling();
    }
  }, [isPollingEnabled]);

  useEffect(() => {
    if(websiteError || webpageError || userError) {
      setIsPollingEnabled(false);
    }
  }, [websiteError, webpageError, userError])

  const onWebsiteMessageSent = (reportMessage: ReportMessage) => {
    if(websiteData) {
      websiteData.messages.push(reportMessage);
      setWebsiteData({...websiteData});  // to cause a state change
    }
  }

  const onWebpageMessageSent = (reportMessage: ReportMessage) => {
    if(webpageData) {
      webpageData.messages.push(reportMessage);
      setWebpageData({...webpageData});  // to cause a state change
    }
  }

  const onUserMessageSent = (reportMessage: ReportMessage) => {
    if(userData) {
      userData.messages.push(reportMessage);
      setUserData({...userData});  // to cause a state change
    }
  }

  return (
    <>
      <Helmet>
        <title>{t("ReportsPage.title")}</title>
      </Helmet>
      <h1>{t("ReportsPage.title")}</h1>
      <p>{t("ReportsPage.text")}</p>

      <h2>{t("ReportsPage.titleWebsites")}</h2>
      {websiteData ? (
        <ReportTable
          contentHeader={t("ReportsPage.tableHeaderWebsite")}
          data={websiteData}
          onMessageSent={onWebsiteMessageSent}
        />
      ) : (
        websiteError ? (
          <p>{websiteError}</p>
        ) : (
          <CircularProgress size="1.5rem" sx={{color: "background"}}/>
        )
      )}

      <h2>{t("ReportsPage.titleWebpages")}</h2>
      {webpageData ? (
        <ReportTable
          contentHeader={t("ReportsPage.tableHeaderWebpage")}
          data={webpageData}
          onMessageSent={onWebpageMessageSent}
        />
      ) : (
        webpageError ? (
          <p>{webpageError}</p>
        ) : (
          <CircularProgress size="1.5rem" sx={{color: "background"}}/>
        )
      )}

      <h2>{t("ReportsPage.titleUsers")}</h2>
      {userData ? (
          <ReportTable
            contentHeader={t("ReportsPage.tableHeaderUser")}
            data={userData}
            onMessageSent={onUserMessageSent}
          />
      ) : (
        userError ? (
          <p>{userError}</p>
        ) : (
          <CircularProgress size="1.5rem" sx={{color: "background"}}/>
        )
      )}

      <br/>
    </>
  )
}

export default Reports;
