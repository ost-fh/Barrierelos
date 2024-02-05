import {Helmet} from "react-helmet-async";
import {useTranslation} from "react-i18next";
import ChooseWebsite from "./ChooseWebsite.tsx";
import {Box} from "@mui/material";
import {useContext} from "react";
import {AuthenticationContext} from "../../context/AuthenticationContext.ts";

function ContributePage() {
  const {t} = useTranslation();
  const {authentication} = useContext(AuthenticationContext);

  return (
    <>
      <Helmet>
        <title>{t("ContributePage.title")} - {t("General.title")}</title>
      </Helmet>
      <Box display="flex" flexDirection="column" justifyContent="center" alignItems="center">
        <h1>{t("ContributePage.title")}</h1>
        <p>{t("ContributePage.text")}</p>
        {!authentication.isAuthenticated && (
          <p>{t("ContributePage.loginRequired")}</p>
        )}
      </Box>

      {authentication.isAuthenticated && (
        <ChooseWebsite/>
      )}

    </>
  )
}

export default ContributePage;
