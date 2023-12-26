import {useTranslation} from "react-i18next";
import {Helmet} from "react-helmet-async";

function HomePage() {
  const {t} = useTranslation();
  return (
    <>
      <Helmet>
        <title>{t("HomePage.title")} - {t("General.title")}</title>
      </Helmet>
      <h1>Barrierelos</h1>
    </>
  )
}

export default HomePage
