import {useTranslation} from "react-i18next";
import {useEffect, useRef, useState} from "react";
import {useNavigate} from "react-router-dom";
import {WebpageControllerService, WebpageMessage, WebsiteControllerService, WebsiteMessage} from "../../lib/api-client";
import {ERROR_CONFLICT} from "../../util/constants.ts";
import {Alert, Box, CircularProgress, Typography} from "@mui/material";
import ArrowCircleDownIcon from "@mui/icons-material/ArrowCircleDown";
import {ContributeButton, scrollToBottom} from "../../util/formatter.tsx";
import category = WebsiteMessage.category;

export default function Done(props: {url: string, category: category, tags: Array<string> }) {
  const { url, category, tags } = props;
  const {t} = useTranslation();
  const endRef = useRef<null | HTMLDivElement>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | undefined>(undefined);
  const navigate = useNavigate();

  useEffect(() => {
    setLoading(false);
    setError(undefined);
    scrollToBottom(endRef);
  }, [url, category, tags]);

  const onCancel = () => {
    window.location.reload();
  }

  const onAdd = () => {
    setLoading(true);

    const websiteMessage: WebsiteMessage = {
      url: url,
      category: category,
      tags: tags
    }

    WebsiteControllerService.addWebsite(websiteMessage)
      .then((website) => {
        const webpageMessage: WebpageMessage = {
          websiteId: website.id,
          url: url
        }
        WebpageControllerService.addWebpage(webpageMessage)
          .then(() => {
            WebsiteControllerService.scanWebsite(website.id)
              .finally(() => navigate(`/websites/${website.id}`));
          });
      })
      .catch((error) => {
        switch(error.status) {
          case ERROR_CONFLICT:
            return setError(t("ContributePage.alreadyAddedError"));
          default:
            return setError(t("ContributePage.defaultError"));
        }
      })
      .finally(() => setLoading(false));
  }

  return (
    <>
      <Box ref={endRef} component="form" display="flex" flexDirection="column" justifyContent="center" alignItems="center">
        <ArrowCircleDownIcon color={"disabled"} sx={{mt: 4, fontSize: 80}}/>
        <Typography component="h2" variant="h5" sx={{mt: 2}}>
          {t("ContributePage.Done.title")}
        </Typography>
        <Typography component="p" variant="body1" sx={{my: 2}}>
          {t("ContributePage.Done.text")}
        </Typography>
        {error && (
          <Alert sx={{ mb: 2 }} severity="error">{error}</Alert>
        )}
        <Box display="flex" gap="16px">
          <ContributeButton onClick={onCancel} variant="contained">{t("ContributePage.Done.cancelButton")}</ContributeButton>
          {!error && (
            <ContributeButton onClick={onAdd} variant="contained">
              {loading ? (
                <CircularProgress size="1.5rem" sx={{color: "white"}}/>
              ) : (
                t("ContributePage.addWebsiteButton")
              )}
            </ContributeButton>
          )}
        </Box>
      </Box>
    </>
  )
}
