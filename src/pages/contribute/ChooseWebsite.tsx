import {useTranslation} from "react-i18next";
import React, {useContext, useState} from "react";
import {AuthenticationContext} from "../../context/AuthenticationContext.ts";
import {useNavigate} from "react-router-dom";
import {isValidWebsiteUrl} from "../../util/validation.ts";
import {Alert, Box, TextField, Typography} from "@mui/material";
import {ContributeButton} from "../../util/formatter.tsx";
import ChooseType from "./ChooseType.tsx";

export default function ChooseWebsite() {
  const {t} = useTranslation();
  const [url, setUrl] = useState("");
  const [done, setDone] = useState(false);
  const [error, setError] = useState<string|undefined>(undefined);
  const {authentication} = useContext(AuthenticationContext);
  const navigate = useNavigate();

  const onChooseWebsite = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    setError(undefined);
    setDone(false);

    if(!authentication.isAuthenticated) {
      navigate('/login');
    }
    else {
      const data = new FormData(event.currentTarget);
      const url = data.get('url')?.toString() ?? "";

      setUrl(url);

      if(isValidWebsiteUrl(url)) {
        setDone(true);
      }
      else {
        setError(t("Validation.invalidUrl"))
      }
    }
  }

  return (
    <>
      <Box component="form" onSubmit={onChooseWebsite} display="flex" flexDirection="column" justifyContent="center" alignItems="center">
        <Typography component="h2" variant="h5" sx={{mt: 2}}>
          {t("ContributePage.ChooseWebsite.title")}
        </Typography>
        <Typography component="p" variant="body1" sx={{my: 2}}>
          {t("ContributePage.ChooseWebsite.text")}
        </Typography>
        <Box display="flex" justifyContent="center" alignItems="center" gap="32px" sx={{flexGrow: 1, width: "100%", maxWidth: "640px"}}>
          <TextField
            required
            id="url"
            name="url"
            label={t("ContributePage.url")}
            type="url"
            sx={{flexGrow: 1}}
            onChange={() => setDone(false)}
          />
          <ContributeButton type="submit" variant="contained">
            {t("ContributePage.addWebsiteButton")}
          </ContributeButton>
        </Box>
        {error && (
          <Alert sx={{ mt: 2 }} severity="error">{error}</Alert>
        )}
      </Box>
      {done && (
        <ChooseType
          url={url}
        />
      )}
    </>
  )
}
