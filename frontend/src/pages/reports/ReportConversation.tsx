import {ReportMessage, ReportMessageControllerService, User} from "../../lib/api-client";
import {useTranslation} from "react-i18next";
import React, {useContext, useState} from "react";
import {AuthenticationContext} from "../../context/AuthenticationContext.ts";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import {Alert, Box, Button, CircularProgress, TextField} from "@mui/material";
import ReportConversationMessage from "./ReportConversationMessage.tsx";

export default function ReportConversation(props: { reportId: number, conversation: ReportMessage[], users: Map<number, User>, onMessageSent: (reportMessage: ReportMessage) => void }) {
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
          <ReportConversationMessage
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
