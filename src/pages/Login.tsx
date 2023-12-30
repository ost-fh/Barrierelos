import {
  Alert,
  Avatar,
  Box,
  Button,
  Checkbox,
  CircularProgress,
  Container,
  Divider,
  FormControlLabel,
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

function Login() {
  const {t} = useTranslation();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string|undefined>(undefined);
  const {setAuthentication} = useContext(AuthenticationContext);
  const navigate = useNavigate();

  function onLoginSuccess() {
    setError(undefined);
    setLoading(false);

    navigate("/profile");
  }

  function onLoginError(error: string) {
    setError(error);
    setLoading(false);
  }

  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    setError(undefined);
    setLoading(true);

    AuthenticationService.login(username, password, onLoginSuccess, onLoginError, setAuthentication);
  };

  return (
    <>
      <Container component="main" maxWidth="xs">
        <Box sx={{ marginTop: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <Avatar sx={{ m: 1, backgroundColor: 'secondary.main' }}>
            <LockOutlinedIcon />
          </Avatar>
          <Typography component="h1" variant="h5">
            {t("LoginPage.logIn")}
          </Typography>
          <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
            {t("LoginPage.continueWithGoogle")}
          </Button>
          <Divider sx={{ my: 2 }} flexItem>{t("LoginPage.dividerOr")}</Divider>
          <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1 }}>
            <TextField
              onChange={(event) => setUsername(event.target.value)}
              margin="normal"
              required
              fullWidth
              id="username"
              name="username"
              label={t("LoginPage.username")}
              autoComplete="username"
              autoFocus
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
            <FormControlLabel
              control={<Checkbox value="remember" color="primary" />}
              label={t("LoginPage.rememberMe")}
            />
            {error && (
              <Alert sx={{ mt: 2 }} severity="error">{t("LoginPage.loginFailed")}</Alert>
            )}
            <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
              {loading ? (
                <CircularProgress size="1.5rem" sx={{color: "background"}}/>
              ) : (
                t("LoginPage.logIn")
              )}
            </Button>
            <Grid container>
              <Grid item xs>
                <Link to="#" component={RouterLink} variant="body2">
                  {t("LoginPage.forgotPassword")}
                </Link>
              </Grid>
              <Grid item>
                <Link to="/signup" component={RouterLink} variant="body2">
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

export default Login;
