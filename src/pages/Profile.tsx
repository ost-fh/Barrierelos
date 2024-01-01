import {Alert, Avatar, Box, Button, CircularProgress, Container, Grid, TextField, Typography} from "@mui/material";
import PersonIcon from "@mui/icons-material/Person";
import {useTranslation} from "react-i18next";
import React, {useContext, useState} from "react";
import {Credential, CredentialControllerService, UserControllerService} from "../lib/api-client";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";
import {AuthenticationService} from "../services/AuthenticationService.ts";
import {useNavigate} from "react-router-dom";
import {ERROR_CONFLICT, ERROR_UNAUTHORIZED} from "../constants.ts";
import ConfirmDialog from "../dialogs/ConfirmDialog.tsx";
import {isValidEmail, isValidUsername} from "../util.ts";

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

  function onChangeContactError(): void
  function onChangeContactError(error: string): void
  function onChangeContactError(error: number): void
  function onChangeContactError(error: unknown = t("ProfilePage.changeContactError")): void {
    if(typeof error === "string") {
      setChangeContactError(error);
    }
    else if(typeof error === "number") {
      switch(error) {
        case ERROR_CONFLICT:
          setChangeContactError(t("ProfilePage.changeUsernameFailed"));
          break;
        default:
          onChangeContactError();
          break;
      }
    }

    setChangeContactLoading(false);
  }

  function onChangePasswordError(): void
  function onChangePasswordError(error: string): void
  function onChangePasswordError(error: number): void
  function onChangePasswordError(error: unknown = t("ProfilePage.changePasswordError")): void {
    if(typeof error === "string") {
      setChangePasswordError(error);
    }
    else if(typeof error === "number") {
      switch(error) {
        case ERROR_UNAUTHORIZED:
          setChangePasswordError(t("ProfilePage.wrongPasswordError"));
          break;
        default:
          onChangePasswordError();
          break;
      }
    }

    setChangePasswordLoading(false);
  }

  function onDeleteAccountError(): void
  function onDeleteAccountError(error: number): void
  function onDeleteAccountError(error: unknown = t("ProfilePage.deleteAccountError")): void {
    if(typeof error === "string") {
      setDeleteAccountError(error);
    }
    else if(typeof error === "number") {
      switch(error) {
        case ERROR_UNAUTHORIZED:
          setDeleteAccountError(t("ProfilePage.delete_wrongPasswordError"));
          break;
        default:
          onDeleteAccountError();
          break;
      }
    }

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

      if(!isValidEmail(email.toString())) {
        return onChangeContactError(t("ProfilePage.wrongEmail"));
      }

      if(!isValidUsername(username.toString())) {
        return onChangeContactError(t("ProfilePage.invalidUsername"));
      }

      const user = Object.assign({}, authentication.user);
      user.username = username.toString();
      user.firstname = firstname.toString();
      user.lastname = lastname.toString();
      user.email = email.toString();

      UserControllerService.updateUser(user.id, user)
        .then((user) => {
          if(setAuthentication !== undefined) {
            AuthenticationService.changeUser(user, authentication.isBasicAuthentication, onChangeContactSuccess, setAuthentication);
          }
          else {
            onChangeContactError()
          }
        })
        .catch((error) => onChangeContactError(error.status as number));
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
            if(setAuthentication !== undefined) {
              AuthenticationService.loginWithBasicAuthentication(authentication?.user?.username ?? "", newPassword.toString(), onChangePasswordSuccess, onChangePasswordError, setAuthentication);
            }
            else {
              onChangePasswordError()
            }
          })
          .catch((error) => {
            AuthenticationService.changePassword(AuthenticationService.getPassword());

            return onChangePasswordError(error.status as number);
          })
      }
      else {
        return onChangePasswordError();
      }
    }
    else {
      return onChangePasswordError(t("ProfilePage.passwordConfirmationFailed"));
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

          return onDeleteAccountError(error.status as number);
        });
    }
    else {
      return onDeleteAccountError();
    }
  }

  return (
    <>
      <Container component="main">
        <Box sx={{ mt: 2, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <Avatar sx={{ m: 1, backgroundColor: 'secondary.main', width: 96, height: 96 }}>
            <PersonIcon sx={{ width: 72, height: 72 }} />
          </Avatar>
          <Typography component="h1" variant="h5">
            {t("ProfilePage.profile")}
          </Typography>
          <Grid container sx={{ px: 3, mb: 3, width: '100vw' }} spacing={2} direction="row">
            <Grid item xs={12} sm={6}>
              <Box component="form" onSubmit={handleChangeContact} sx={{ mt: 3, alignItems: 'left' }}>
                <Grid container spacing={2} columns={{ xs: 2, sm: 4, md: 6 }} direction="column">
                  <Grid item>
                    <Typography component="h2" variant="h6">
                      {t("ProfilePage.contact")}
                    </Typography>
                  </Grid>
                  <Grid item>
                    <Typography variant="body1">
                      {t("ProfilePage.contactHint")}
                    </Typography>
                  </Grid>
                  <Grid item>
                    <TextField
                      required
                      fullWidth
                      id="firstname"
                      name="firstname"
                      label={t("ProfilePage.firstname")}
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
                      label={t("ProfilePage.lastname")}
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
                      label={t("ProfilePage.email")}
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
                      label={t("ProfilePage.username")}
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
                        <CircularProgress size="1.5rem" sx={{color: "background"}}/>
                      ) : (
                        t("ProfilePage.changeContact")
                      )}
                    </Button>
                  </Grid>
                </Grid>
              </Box>
            </Grid>
            {authentication.isBasicAuthentication && (
              <Grid item xs={12} sm={6}>
                <Box component="form" onSubmit={handleChangePassword} sx={{ mt: 3, alignItems: 'left' }}>
                  <Grid container spacing={2} columns={{ xs: 2, sm: 4, md: 6 }} direction="column">
                    <Grid item>
                      <Typography component="h2" variant="h6">
                        {t("ProfilePage.changePassword")}
                      </Typography>
                    </Grid>
                    <Grid item>
                      <Typography variant="body1">
                        {t("ProfilePage.changePasswordHint")}
                      </Typography>
                    </Grid>
                    <Grid item>
                      <TextField
                        required
                        fullWidth
                        id="currentPassword"
                        name="currentPassword"
                        type="password"
                        label={t("ProfilePage.currentPassword")}
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
                        label={t("ProfilePage.newPassword")}
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
                        label={t("ProfilePage.confirmNewPassword")}
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
                          <CircularProgress size="1.5rem" sx={{color: "background"}}/>
                        ) : (
                          t("ProfilePage.changePassword")
                        )}
                      </Button>
                    </Grid>
                  </Grid>
                </Box>
              </Grid>
            )}
            <Grid item xs={12} sm={6}>
              <Box component="form" onSubmit={handleDeleteAccount} sx={{ mt: 3, alignItems: 'left' }}>
                <Grid container spacing={2} columns={{ xs: 2, sm: 4, md: 6 }} direction="column">
                  <Grid item>
                    <Typography component="h2" variant="h6">
                      {t("ProfilePage.deleteAccount")}
                    </Typography>
                  </Grid>
                  <Grid item>
                    <Typography variant="body1">
                      {t("ProfilePage.deleteAccountHint")}
                    </Typography>
                  </Grid>
                  <Grid item>
                    <TextField
                      required
                      fullWidth
                      id="password"
                      name="password"
                      type="password"
                      label={t("ProfilePage.password")}
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
                        <CircularProgress size="1.5rem" sx={{color: "background"}}/>
                      ) : (
                        t("ProfilePage.deleteAccount")
                      )}
                    </Button>
                  </Grid>
                </Grid>
                <ConfirmDialog
                  title={t("ProfilePage.confirmDialogTitle")}
                  no={t("ProfilePage.confirmDialogNo")}
                  yes={t("ProfilePage.confirmDialogYes")}
                  open={deleteAccountConfirmOpen}
                  setOpen={setDeleteAccountConfirmOpen}
                  onNo={handleDeleteAccountNo}
                  onYes={handleDeleteAccountYes}
                >
                  {t("ProfilePage.confirmDialogText")}
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
