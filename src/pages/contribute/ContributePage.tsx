import {Helmet} from "react-helmet-async";
import {useTranslation} from "react-i18next";
import ChooseWebsite from "./ChooseWebsite.tsx";

function ContributePage() {
  const {t} = useTranslation();

  return (
    <>
      <Helmet>
        <title>{t("ContributePage.title")} - {t("General.title")}</title>
      </Helmet>
      <h1>{t("ContributePage.title")}</h1>
      <p>{t("ContributePage.text")}</p>
      <ChooseWebsite/>
    </>
  )
}

export default ContributePage;
