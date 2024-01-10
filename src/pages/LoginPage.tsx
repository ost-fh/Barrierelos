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
import {AuthenticationService} from "../services/AuthenticationService.ts";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";
import {Link as RouterLink, useNavigate} from "react-router-dom";
import GoogleLoginComponent from "../components/GoogleLoginComponent.tsx";

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
