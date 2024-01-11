import PersonIcon from "@mui/icons-material/Person";
import {Alert, Avatar, Box, CircularProgress, Typography} from "@mui/material";
import {useContext, useEffect, useState} from "react";
import {Helmet} from "react-helmet-async";
import {useTranslation} from "react-i18next";
import {User, UserControllerService} from "../lib/api-client";
import {useParams} from "react-router-dom";
import Table from "@mui/material/Table";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import {convertRolesToString, convertTimestampToLocalDatetime} from "../util/converter.ts";
import ReportComponent from "../components/ReportComponent.tsx";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";

interface UserPageParams extends Record<string, string> {
  userId: string
}

function UserProfile(props: { user: User }) {
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

function UserPage() {
  const {t} = useTranslation();
  const [user, setUser] = useState<User|undefined>();
  const [error, setError] = useState<string|undefined>();

  const params = useParams<UserPageParams>();

  if(params.userId === undefined) {
    throw Error('Path param userId is missing');
  }

  const userId = parseInt(params.userId);

  const loadUser = () => {
    UserControllerService.getUser(userId)
      .then((user) => {
        setError(undefined);
        setUser(user);
      })
      .catch(() => setError(t('UserPage.error')));
  }

  useEffect(() => { loadUser(); }, []);

  return (
    <>
      <Helmet>
        <title>{t('UserPage.title')} - {t("General.title")}</title>
      </Helmet>
      <h1>{t('UserPage.title')}</h1>
      {user ? (
        <UserProfile user={user}/>
      ) : (
        error ? (
          <>
            <Alert sx={{ my: 1 }} severity="error">{error}</Alert>
          </>
        ) : (
          <>
            <CircularProgress size="1.5rem" sx={{color: "background"}}/>
          </>
        )
      )}
    </>
  )
}

export default UserPage;
