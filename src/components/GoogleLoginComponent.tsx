import {Authentication} from "../model/Authentication.ts";
import {Dispatch, SetStateAction, useContext, useLayoutEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";
import {Token} from "../model/Token.ts";
import {jwtDecode} from "jwt-decode";
import {ApiError, Credential, RegistrationMessage, User, UserControllerService} from "../lib/api-client";
import {AuthenticationService} from "../services/AuthenticationService.ts";
import {ERROR_CONFLICT} from "../constants.ts";
import {CredentialResponse, GoogleLogin} from "@react-oauth/google";
import {isValidUsername} from "../util.ts";
import ConfirmDialog from "../dialogs/ConfirmDialog.tsx";
import {Alert, Box, TextField} from "@mui/material";

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

export default GoogleLoginComponent;
