import {User} from "../../lib/api-client";
import {useTranslation} from "react-i18next";
import {useContext} from "react";
import {AuthenticationContext} from "../../context/AuthenticationContext.ts";
import {Avatar, Box, Typography} from "@mui/material";
import PersonIcon from "@mui/icons-material/Person";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import {convertRolesToString, convertTimestampToLocalDatetime} from "../../util/converter.ts";
import ReportComponent from "../../components/ReportComponent.tsx";

export default function UserProfile(props: { user: User }) {
  const { user } = props;
  const {t} = useTranslation();
  const {authentication} = useContext(AuthenticationContext);

  return (
    <>
      <Box sx={{ m: 2, display: 'flex', flexFlow: 'column nowrap' }}>
        <Avatar sx={{ mr: 1, backgroundColor: 'secondary.light', width: 72, height: 72 }}>
          <PersonIcon sx={{ width: 48, height: 48 }} />
        </Avatar>
        <h2>{user.firstname} {user.lastname}</h2>
        <Table>
          <TableBody>
            <TableRow>
              <TableCell>
                <Typography component="span" variant="body2" sx={{ whiteSpace: "nowrap" }}>
                  {t('UserPage.labelFirstname')}:
                </Typography>
              </TableCell>
              <TableCell sx={{ width: '100%' }}>
                {user.firstname}
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell>
                <Typography component="span" variant="body2" sx={{ whiteSpace: "nowrap" }}>
                  {t('UserPage.labelLastname')}:
                </Typography>
              </TableCell>
              <TableCell>
                {user.lastname}
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell>
                <Typography component="span" variant="body2" sx={{ whiteSpace: "nowrap" }}>
                  {t('UserPage.labelUsername')}:
                </Typography>
              </TableCell>
              <TableCell>
                {user.username}
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell>
                <Typography component="span" variant="body2" sx={{ whiteSpace: "nowrap" }}>
                  {t('UserPage.labelRole')}:
                </Typography>
              </TableCell>
              <TableCell>
                {convertRolesToString(user.roles, t)}
              </TableCell>
            </TableRow>
            <TableRow>
              <TableCell>
                <Typography component="span" variant="body2" sx={{ whiteSpace: "nowrap" }}>
                  {t('UserPage.labelRegistered')}:
                </Typography>
              </TableCell>
              <TableCell>
                {convertTimestampToLocalDatetime(user.created)}
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
        {authentication.user !== undefined && authentication.user.id === user.id ? null : (
          <Box sx={{my: 2}}>
            <ReportComponent
              subject={user}
              fullWidth={true}
            />
          </Box>
        )}
      </Box>
    </>
  )
}
