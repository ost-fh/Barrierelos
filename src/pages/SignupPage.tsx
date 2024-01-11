import {
  Alert,
  Avatar,
  Box,
  Button,
  CircularProgress,
  Container,
  Divider,
  Grid,
  Link,
  TextField,
  Typography
} from "@mui/material";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import {useTranslation} from "react-i18next";
import React, {useContext, useState} from "react";
import {ApiError, Credential, RegistrationMessage, User, UserControllerService} from "../lib/api-client";
import {AuthenticationService} from "../services/AuthenticationService.ts";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";
import {Link as RouterLink, useNavigate} from "react-router-dom";
import {ERROR_CONFLICT, PAGE_AFTER_SIGNUP} from "../util/constants.ts";
import GoogleLoginComponent from "../components/GoogleLoginComponent.tsx";
import {Helmet} from "react-helmet-async";
import {isValidEmail, isValidPassword, isValidUsername} from "../util/validation.ts";

function SignupPage() {
  const {t} = useTranslation();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string|undefined>(undefined);
  const {authentication, setAuthentication} = useContext(AuthenticationContext);
  const navigate = useNavigate();

  if(authentication.isAuthenticated) {
    navigate(PAGE_AFTER_SIGNUP);
  }

  function onSignupSuccess() {
    setError(undefined);
    setLoading(false);
    navigate(PAGE_AFTER_SIGNUP);
  }

  function onSignupError(): void
  function onSignupError(error: string): void
  function onSignupError(error: number): void
  function onSignupError(error: unknown = t("SignupPage.signupFailed")): void {
    if(typeof error === "string") {
      setError(error);
    }
    else if(typeof error === "number") {
      switch(error) {
        default:
          onSignupError();
          break;
      }
    }

    setLoading(false);
  }

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    setError(undefined);
    setLoading(true);

    const data = new FormData(event.currentTarget);
    const firstname = data.get('firstname');
    const lastname = data.get('lastname');
    const email = data.get('email');
    const username = data.get('username');
    const password = data.get('password');
    const confirmPassword = data.get('confirmPassword');

    if(password === confirmPassword) {
      if(username !== null
        && firstname !== null
        && lastname !== null
        && email !== null
        && password !== null) {

        if(!isValidEmail(email.toString())) {
          return onSignupError(t("SignupPage.wrongEmail"));
        }

        if(!isValidUsername(username.toString())) {
          return onSignupError(t("SignupPage.invalidUsername"));
        }

        if(!isValidPassword(password.toString())) {
          return onSignupError(t("SignupPage.invalidPassword"));
        }

        const user: User = {
          id: 0,
          username: username.toString(),
          firstname: firstname.toString(),
          lastname: lastname.toString(),
          email: email.toString(),
          roles: ['CONTRIBUTOR'],
          deleted: false,
          modified: 0,
          created: 0,
        }

        const credential: Credential = {
          id: 0,
          userId: 0,
          password: password.toString(),
          modified: 0,
          created: 0,
        }

        const registrationMessage: RegistrationMessage = {
          user: user,
          credential: credential,
        }

        UserControllerService.addUser(registrationMessage)
          .then(() => {
            if(setAuthentication !== undefined) {
              AuthenticationService.loginWithBasicAuthentication(username.toString(), password.toString(), onSignupSuccess, onSignupError, setAuthentication);
            }
            else {
              onSignupError()
            }
          })
          .catch((error) => {
            if(error instanceof ApiError) {
              switch(error.status) {
                case ERROR_CONFLICT:
                  return onSignupError(t("SignupPage.changeUsernameFailed"));
              }
            }

            return onSignupError();
          });
      }
      else {
        return onSignupError();
      }
    }
    else {
      return onSignupError(t("SignupPage.passwordConfirmationFailed"));
    }
  };

  return (
    <Container maxWidth="xs">
      <Helmet>
        <title>{t("SignupPage.signUp")} - {t("General.title")}</title>
      </Helmet>
      <Box id="login-box" sx={{ marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Avatar sx={{ m: 1, backgroundColor: 'secondary.main' }}>
          <LockOutlinedIcon />
        </Avatar>
        <Typography component="h1" variant="h5" sx={{mb: 3}}>
          {t("SignupPage.signUp")}
        </Typography>
        <GoogleLoginComponent
          onLoginSuccess={() => onSignupSuccess}
          onLoginError={() => onSignupError}
          setError={() => setError}
          setLoading={() => setLoading}
        />
        <Divider sx={{mt: 4, mb: 1}} flexItem>{t("LoginPage.dividerOr")}</Divider>
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                id="firstname"
                name="firstname"
                label={t("SignupPage.firstname")}
                autoComplete="given-name"
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                required
                fullWidth
                id="lastname"
                name="lastname"
                label={t("SignupPage.lastname")}
                autoComplete="family-name"
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                id="email"
                name="email"
                label={t("SignupPage.email")}
                autoComplete="email"
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                id="username"
                name="username"
                label={t("SignupPage.username")}
                autoComplete="username"
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                id="password"
                name="password"
                type="password"
                label={t("SignupPage.password")}
                autoComplete="new-password"
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                required
                fullWidth
                id="confirmPassword"
                name="confirmPassword"
                type="password"
                label={t("SignupPage.confirmPassword")}
                autoComplete="new-password"
              />
            </Grid>
          </Grid>
          {error && (
            <Alert sx={{ mt: 2 }} severity="error">{error}</Alert>
          )}
          <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
            {loading ? (
              <CircularProgress size="1.5rem" sx={{color: "white"}}/>
            ) : (
              t("SignupPage.signUp")
            )}
          </Button>
          <Grid container justifyContent="flex-end">
            <Grid item>
              <Link to="/login" component={RouterLink} variant="body2">
                {t("SignupPage.alreadyAccountLogIn")}
              </Link>
            </Grid>
          </Grid>
        </Box>
      </Box>
    </Container>
  )
}

export default SignupPage;
