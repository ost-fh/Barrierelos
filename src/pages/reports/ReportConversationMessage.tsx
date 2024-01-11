import {ReportMessage, User} from "../../lib/api-client";
import {useTranslation} from "react-i18next";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import {Avatar, Box, Link, Typography} from "@mui/material";
import PersonIcon from "@mui/icons-material/Person";
import {
  convertDeletedToString,
  convertRolesToString,
  convertTimestampToLocalDate,
  convertTimestampToLocalDatetime
} from "../../util/converter.ts";

export default function ReportConversationMessage(props: { message: ReportMessage, user: User }) {
  const { message, user } = props;
  const {t} = useTranslation();

  return (
    <TableRow sx={{ height: '100%', "& td": { borderTop: '1px solid #e0e0e0', borderBottom: '1px solid #e0e0e0' }, "& td:first-of-type": { borderLeft: '1px solid #e0e0e0', borderTopLeftRadius: '8px', borderBottomLeftRadius: '8px' }, "& td:last-of-type": { borderRight: '1px solid #e0e0e0', borderTopRightRadius: '8px', borderBottomRightRadius: '8px' } }}>
      <TableCell padding="none" sx={{ whiteSpace: 'nowrap', backgroundColor: 'background.surface' }}>
        <Link href={`/user/${user.id}`} sx={{ color: 'black', textDecoration: 'none' }}>
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
        </Link>
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
