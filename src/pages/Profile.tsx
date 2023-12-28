import {
  Alert,
  Avatar,
  Box,
  Button,
  CircularProgress,
  Container,
  CssBaseline,
  Grid,
  TextField,
  Typography
} from "@mui/material";
import PersonIcon from '@mui/icons-material/Person';
import {useTranslation} from "react-i18next";
import React, {useContext, useState} from "react";
import {ApiError, Credential, CredentialControllerService, UserControllerService} from "../lib/api-client";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";
import {AuthenticationService} from "../services/AuthenticationService.ts";
import {useNavigate} from "react-router-dom";
import {ERROR_CONFLICT, ERROR_UNAUTHORIZED} from "../constants.ts";
import ConfirmDialog from "../dialogs/ConfirmDialog.tsx";

function Profile() {
  const {t} = useTranslation();
  const [changeContactLoading, setChangeContactLoading] = useState(false);
  const [changePasswordLoading, setChangePasswordLoading] = useState(false);
  const [deleteAccountLoading, setDeleteAccountLoading] = useState(false);
  const [changeContactError, setChangeContactError] = useState<string|undefined>(undefined);
  const [changePasswordError, setChangePasswordError] = useState<string|undefined>(undefined);
  const [deleteAccountError, setDeleteAccountError] = useState<string|undefined>(undefined);
  const [deleteAccountConfirmOpen, setDeleteAccountConfirmOpen] = useState(false);
  const [deleteAccountPassword, setDeleteAccountPassword] = useState<string|null>(null);
  const {authentication, setAuthentication} = useContext(AuthenticationContext);
  const navigate = useNavigate();

  function onChangeContactError(error: string = t("Profile.changeContactError")) {
    setChangeContactError(error);
    setChangeContactLoading(false);
  }

  function onChangePasswordError(error: string = t("Profile.changePasswordError")) {
    setChangePasswordError(error);
    setChangePasswordLoading(false);
  }

  function onDeleteAccountError(error: string = t("Profile.deleteAccountError")) {
    setDeleteAccountError(error);
    setDeleteAccountLoading(false);
  }

  function onChangeContactSuccess() {
    setChangeContactError(undefined);
    setChangeContactLoading(false);
  }

  function onChangePasswordSuccess() {
    setChangePasswordError(undefined);
    setChangePasswordLoading(false);
  }

  function onDeleteAccountSuccess() {
    setDeleteAccountError(undefined);
    setDeleteAccountLoading(false);
  }

  const handleChangeContact = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    setChangeContactError(undefined);
    setChangeContactLoading(true);

    const data = new FormData(event.currentTarget);
    const firstname = data.get('firstname');
    const lastname = data.get('lastname');
    const email = data.get('email');
    const username = data.get('username');

    if(authentication.user !== undefined
      && username !== null
      && firstname !== null
      && lastname !== null
      && email !== null) {
      const user = Object.assign({}, authentication.user);
      user.username = username.toString();
      user.firstname = firstname.toString();
      user.lastname = lastname.toString();
      user.email = email.toString();

      UserControllerService.updateUser(user.id, user)
        .then((user) => {
          AuthenticationService.changeUser(user, onChangeContactSuccess, onChangeContactError, setAuthentication);
        })
        .catch((error) => {
          if(error instanceof ApiError) {
            switch(error.status) {
              case ERROR_CONFLICT:
                return onChangeContactError(t("Profile.changeUsernameFailed"));
              default:
                return onChangeContactError();
            }
          }
        })
    }
    else {
      return onChangeContactError();
    }
  };

  const handleChangePassword = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    setChangePasswordError(undefined);
    setChangePasswordLoading(true);

    const data = new FormData(event.currentTarget);
    const currentPassword = data.get('currentPassword');
    const newPassword = data.get('newPassword');
    const confirmNewPassword = data.get('confirmNewPassword');

    if(newPassword === confirmNewPassword) {
      if(authentication.user !== undefined
        && currentPassword !== null
        && newPassword !== null
        && confirmNewPassword !== null) {
        const credential: Credential = {
          id: 0,
          userId: authentication.user.id,
          password: newPassword.toString(),
          modified: 0,
          created: 0,
        }

        AuthenticationService.changePassword(currentPassword.toString());

        CredentialControllerService.updateCredential(authentication.user.id, credential)
          .then(() => {
            AuthenticationService.login(authentication?.user?.username ?? "", newPassword.toString(), onChangePasswordSuccess, onChangePasswordError, setAuthentication);
          })
          .catch((error) => {
            AuthenticationService.changePassword(AuthenticationService.getPassword());

            if(error instanceof ApiError) {
              switch(error.status) {
                case ERROR_UNAUTHORIZED:
                  return onChangePasswordError(t("Profile.wrongPasswordError"));
              }
            }

            return onChangePasswordError();
          })
      }
      else {
        return onChangePasswordError();
      }
    }
    else {
      return onChangePasswordError(t("Profile.passwordConfirmationFailed"));
    }
  };

  const handleDeleteAccount = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    setDeleteAccountError(undefined);
    setDeleteAccountLoading(true);

    const data = new FormData(event.currentTarget);
    const password = data.get('password');

    if(authentication.user !== undefined
      && password !== null) {
      setDeleteAccountPassword(password.toString());

      return setDeleteAccountConfirmOpen(true);
    }
    else {
      return onDeleteAccountError();
    }
  };

  function handleDeleteAccountNo() {
    setDeleteAccountLoading(false);
  }

  const handleDeleteAccountYes = () => {
    if(authentication.user !== undefined
      && deleteAccountPassword !== null) {
      AuthenticationService.changePassword(deleteAccountPassword);

      UserControllerService.deleteUser(authentication.user.id)
        .then(() => {
          onDeleteAccountSuccess();
          navigate("/logout");
        })
        .catch((error) => {
          AuthenticationService.changePassword(AuthenticationService.getPassword());
          if(error instanceof ApiError) {
            switch(error.status) {
              case ERROR_UNAUTHORIZED:
                return onDeleteAccountError(t("Profile.delete_wrongPasswordError"));
            }
          }

          return onDeleteAccountError();
        });
    }
    else {
      return onDeleteAccountError();
    }
  }

  return (
    <>
      <Container component="main">
        <CssBaseline />
        <Box sx={{ mt: 2, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <Avatar sx={{ m: 1, backgroundColor: 'secondary.main', width: 96, height: 96 }}>
            <PersonIcon sx={{ width: 72, height: 72 }} />
          </Avatar>
          <Typography component="h1" variant="h5">
            {t("Profile.profile")}
          </Typography>
          <Grid container sx={{ px: 3, mb: 3, width: '100vw' }} spacing={2} direction="row">
            <Grid item xs={12} sm={6}>
              <Box component="form" onSubmit={handleChangeContact} sx={{ mt: 3, alignItems: 'left' }}>
                <Grid container spacing={2} columns={{ xs: 2, sm: 4, md: 6 }} direction="column">
                  <Grid item>
                    <Typography component="h2" variant="h6">
                      {t("Profile.contact")}
                    </Typography>
                  </Grid>
                  <Grid item>
                    <Typography variant="body1">
                      {t("Profile.contactHint")}
                    </Typography>
                  </Grid>
                  <Grid item>
                    <TextField
                      required
                      fullWidth
                      id="firstname"
                      name="firstname"
                      label={t("Profile.firstname")}
                      autoComplete="given-name"
                      defaultValue={authentication?.user?.firstname}
                      autoFocus
                    />
                  </Grid>
                  <Grid item>
                    <TextField
                      required
                      fullWidth
                      id="lastname"
                      name="lastname"
                      label={t("Profile.lastname")}
                      autoComplete="family-name"
                      defaultValue={authentication?.user?.lastname}
                    />
                  </Grid>
                  <Grid item>
                    <TextField
                      required
                      fullWidth
                      id="email"
                      name="email"
                      label={t("Profile.email")}
                      autoComplete="email"
                      defaultValue={authentication?.user?.email}
                    />
                  </Grid>
                  <Grid item>
                    <TextField
                      required
                      fullWidth
                      id="username"
                      name="username"
                      label={t("Profile.username")}
                      defaultValue={authentication?.user?.username}
                      autoComplete="username"
                    />
                  </Grid>
                  {changeContactError && (
                    <Grid item>
                      <Alert sx={{ mt: 1 }} severity="error">{changeContactError}</Alert>
                    </Grid>
                  )}
                  <Grid item>
                    <Button type="submit" fullWidth variant="contained">
                      {changeContactLoading ? (
                        <CircularProgress size="1.5rem" color="background" />
                      ) : (
                        t("Profile.changeContact")
                      )}
                    </Button>
                  </Grid>
                </Grid>
              </Box>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Box component="form" onSubmit={handleChangePassword} sx={{ mt: 3, alignItems: 'left' }}>
                <Grid container spacing={2} columns={{ xs: 2, sm: 4, md: 6 }} direction="column">
                  <Grid item>
                    <Typography component="h2" variant="h6">
                      {t("Profile.changePassword")}
                    </Typography>
                  </Grid>
                  <Grid item>
                    <Typography variant="body1">
                      {t("Profile.changePasswordHint")}
                    </Typography>
                  </Grid>
                  <Grid item>
                    <TextField
                      required
                      fullWidth
                      id="currentPassword"
                      name="currentPassword"
                      type="password"
                      label={t("Profile.currentPassword")}
                      autoComplete="new-password"
                    />
                  </Grid>
                  <Grid item>
                    <TextField
                      required
                      fullWidth
                      id="newPassword"
                      name="newPassword"
                      type="password"
                      label={t("Profile.newPassword")}
                      autoComplete="new-password"
                    />
                  </Grid>
                  <Grid item>
                    <TextField
                      required
                      fullWidth
                      id="confirmNewPassword"
                      name="confirmNewPassword"
                      type="password"
                      label={t("Profile.confirmNewPassword")}
                      autoComplete="new-password"
                    />
                  </Grid>
                  {changePasswordError && (
                    <Grid item>
                      <Alert sx={{ mt: 1 }} severity="error">{changePasswordError}</Alert>
                    </Grid>
                  )}
                  <Grid item>
                    <Button type="submit" fullWidth variant="contained">
                      {changePasswordLoading ? (
                        <CircularProgress size="1.5rem" color="background" />
                      ) : (
                        t("Profile.changePassword")
                      )}
                    </Button>
                  </Grid>
                </Grid>
              </Box>
            </Grid>
            <Grid item xs={12} sm={6}>
              <Box component="form" onSubmit={handleDeleteAccount} sx={{ mt: 3, alignItems: 'left' }}>
                <Grid container spacing={2} columns={{ xs: 2, sm: 4, md: 6 }} direction="column">
                  <Grid item>
                    <Typography component="h2" variant="h6">
                      {t("Profile.deleteAccount")}
                    </Typography>
                  </Grid>
                  <Grid item>
                    <Typography variant="body1">
                      {t("Profile.deleteAccountHint")}
                    </Typography>
                  </Grid>
                  <Grid item>
                    <TextField
                      required
                      fullWidth
                      id="password"
                      name="password"
                      type="password"
                      label={t("Profile.password")}
                      autoComplete="new-password"
                    />
                  </Grid>
                  {deleteAccountError && (
                    <Grid item>
                      <Alert sx={{ mt: 1 }} severity="error">{deleteAccountError}</Alert>
                    </Grid>
                  )}
                  <Grid item>
                    <Button type="submit" fullWidth variant="contained">
                      {deleteAccountLoading ? (
                        <CircularProgress size="1.5rem" color="background" />
                      ) : (
                        t("Profile.deleteAccount")
                      )}
                    </Button>
                  </Grid>
                </Grid>
                <ConfirmDialog
                  title={t("Profile.confirmDialogTitle")}
                  no={t("Profile.confirmDialogNo")}
                  yes={t("Profile.confirmDialogYes")}
                  open={deleteAccountConfirmOpen}
                  setOpen={setDeleteAccountConfirmOpen}
                  onNo={handleDeleteAccountNo}
                  onYes={handleDeleteAccountYes}
                >
                  {t("Profile.confirmDialogText")}
                </ConfirmDialog>
              </Box>
            </Grid>
          </Grid>
        </Box>
      </Container>
    </>
  )
}

export default Profile;
