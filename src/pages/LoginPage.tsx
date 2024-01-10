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
import React, {Dispatch, SetStateAction, useContext, useLayoutEffect, useState} from "react";
import {AuthenticationService} from "../services/AuthenticationService.ts";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";
import {Link as RouterLink, useNavigate} from "react-router-dom";
import {CredentialResponse, GoogleLogin} from "@react-oauth/google";
import {jwtDecode} from "jwt-decode";
import {Token} from "../model/Token.ts";
import {ApiError, Credential, RegistrationMessage, User, UserControllerService} from "../lib/api-client";
import ConfirmDialog from "../dialogs/ConfirmDialog.tsx";
import {isValidUsername} from "../util.ts";
import {ERROR_CONFLICT} from "../constants.ts";
import {Authentication} from "../model/Authentication.ts";

function GoogleLoginComponent(props: { onLoginSuccess: (authentication: Authentication) => void, onLoginError: (errorCode?: number) => void, setError: Dispatch<SetStateAction<string | undefined>>, setLoading: Dispatch<SetStateAction<boolean>> }) {
  const { onLoginSuccess, onLoginError, setError, setLoading } = props;
  const {t, i18n} = useTranslation();
  const [dialogUsernameOpen, setDialogUsernameOpen] = useState(false);
  const [dialogError, setDialogError] = useState<string | undefined>(undefined);
  const [token, setToken] = useState("");
  const [dialogUsername, setDialogUsername] = useState("");
  const [googleLoginWidth, setGoogleLoginWidth] = useState(0);
  const {setAuthentication} = useContext(AuthenticationContext);

  useLayoutEffect(() => {
    function updateSize() {
      setGoogleLoginWidth(document.querySelector("#login-box")?.clientWidth ?? 0)
    }

    window.addEventListener("resize", updateSize)
    updateSize()
    return () => window.removeEventListener("resize", updateSize)
  }, []);

  function createUser() {
    const userObject: Token = jwtDecode(token);

    const user: User = {
      id: 0,
      username: dialogUsername,
      firstname: userObject.given_name,
      lastname: userObject.family_name,
      email: userObject.email,
      roles: ["CONTRIBUTOR"],
      deleted: false,
      modified: 0,
      created: 0,
    }

    const credential: Credential = {
      id: 0,
      userId: 0,
      issuer: userObject.iss,
      subject: userObject.sub,
      modified: 0,
      created: 0,
    }

    const registrationMessage: RegistrationMessage = {
      user: user,
      credential: credential,
    }

    UserControllerService.addUser(registrationMessage)
      .then(() => {
        if (setAuthentication !== undefined) {
          AuthenticationService.loginWithTokenAuthentication(token, onLoginSuccess, onLoginError, setAuthentication);
        } else {
          onLoginError()
        }
      })
      .catch((error) => {
        if(error instanceof ApiError) {
          switch(error.status) {
            case ERROR_CONFLICT:
              return onDialogError(t("SignupPage.changeUsernameFailed"));
            default:
              return onLoginError();
          }
        }
      });
  }

  const handleGoogleLogin = async (response: CredentialResponse) => {
    setError(undefined);
    setLoading(true);

    const identityToken = response.credential ?? "";

    setToken(identityToken);

    if (setAuthentication !== undefined) {
      AuthenticationService.loginWithTokenAuthentication(identityToken, onLoginSuccess, () => setDialogUsernameOpen(true), setAuthentication);
    } else {
      onLoginError()
    }
  };

  const handleLoginDialogUsernameYes = () => {
    setDialogError(undefined);

    if (!isValidUsername(dialogUsername.toString())) {
      return onDialogError(t("SignupPage.invalidUsername"));
    }

    createUser();
  };

  const handleLoginDialogUsernameNo = () => {
    setDialogError(undefined);
    setDialogUsername("");
    setLoading(false);
  };

  function onDialogError(error: string = t("LoginPage.loginFailed")): void {
    setDialogError(error);
    setDialogUsernameOpen(true)
  }

  return (
    <>
      <GoogleLogin
        width={googleLoginWidth}
        onSuccess={(credentialResponse) => handleGoogleLogin(credentialResponse)}
        onError={() => onLoginError()}
        locale={i18n.resolvedLanguage}
      />
      <ConfirmDialog
        title={t("LoginPage.loginDialogUsernameTitle")}
        no={t("LoginPage.loginDialogUsernameNo")}
        yes={t("LoginPage.loginDialogUsernameYes")}
        open={dialogUsernameOpen}
        setOpen={setDialogUsernameOpen}
        onNo={handleLoginDialogUsernameNo}
        onYes={handleLoginDialogUsernameYes}
      >
        <Box sx={{display: "flex", flexDirection: "column", alignItems: "center"}}>
          <TextField
            onChange={(event) => setDialogUsername(event.target.value)}
            margin="normal"
            required
            id="dialogUsername"
            name="dialogUsername"
            label={t("LoginPage.username")}
            autoComplete="username"
            autoFocus
            value={dialogUsername}
            sx={{width: "min(550px, 80vw)"}}
          />
          {dialogError && (
            <Alert sx={{mt: 2}} severity="error">{dialogError}</Alert>
          )}
        </Box>
      </ConfirmDialog>
    </>
  );
}

function LoginPage() {
  const {t} = useTranslation();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | undefined>(undefined);
  const {authentication, setAuthentication} = useContext(AuthenticationContext);
  const navigate = useNavigate();

  if(authentication.isAuthenticated) {
    navigate("/profile");
  }

  function onLoginSuccess() {
    setError(undefined);
    setLoading(false);

    navigate("/profile");
  }

  function onLoginError(): void
  function onLoginError(error: string): void
  function onLoginError(error: number): void
  function onLoginError(error: unknown = t("LoginPage.loginFailed")): void {
    if (typeof error === "string") {
      setError(error);
    } else if (typeof error === "number") {
      switch (error) {
        default:
          onLoginError();
          break;
      }
    }

    setLoading(false);
  }

  const handleBasicLogin = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    setError(undefined);
    setLoading(true);

    if (setAuthentication !== undefined) {
      AuthenticationService.loginWithBasicAuthentication(username, password, onLoginSuccess, onLoginError, setAuthentication);
    } else {
      onLoginError()
    }
  };

  return (
    <>
      <Container maxWidth="xs">
        <Box id="login-box" sx={{marginTop: 8, display: "flex", flexDirection: "column", alignItems: "center"}}>
          <Avatar sx={{m: 1, backgroundColor: "secondary.main"}}>
            <LockOutlinedIcon/>
          </Avatar>
          <Typography component="h1" variant="h5" sx={{mb: 3}}>
            {t("LoginPage.logIn")}
          </Typography>
          <GoogleLoginComponent
            onLoginSuccess={() => onLoginSuccess}
            onLoginError={() => onLoginError}
            setError={() => setError}
            setLoading={() => setLoading}
          />
          <Divider sx={{mt: 4, mb: 1}} flexItem>{t("LoginPage.dividerOr")}</Divider>
          <Box component="form" onSubmit={handleBasicLogin} sx={{mt: 1}}>
            <TextField
              onChange={(event) => setUsername(event.target.value)}
              margin="normal"
              required
              fullWidth
              id="username"
              name="username"
              label={t("LoginPage.username")}
              autoComplete="username"
              value={username}
            />
            <TextField
              onChange={(event) => setPassword(event.target.value)}
              margin="normal"
              required
              fullWidth
              id="password"
              name="password"
              type="password"
              label={t("LoginPage.password")}
              autoComplete="current-password"
              value={password}
            />
            {error && (
              <Alert sx={{mt: 2}} severity="error">{error}</Alert>
            )}
            <Button type="submit" fullWidth variant="contained" sx={{mt: 3, mb: 2}}>
              {loading ? (
                <CircularProgress size="1.5rem" sx={{color: "white"}}/>
              ) : (
                t("LoginPage.logIn")
              )}
            </Button>
            <Grid container justifyContent="flex-end">
              <Grid item>
                <Link to="/signup" component={RouterLink} variant="body2" sx={{ textDecoration: "none" }}>
                  {t("LoginPage.noAccountSignUp")}
                </Link>
              </Grid>
            </Grid>
          </Box>
        </Box>
      </Container>
    </>
  );
}

export default LoginPage;
