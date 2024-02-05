import i18n from "i18next";
import Backend from "i18next-http-backend";
import LanguageDetector from "i18next-browser-languagedetector";
import {initReactI18next} from "react-i18next";


void i18n
  .use(Backend)
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    fallbackLng: "en",
    nsSeparator: "::",
    interpolation: {
      // Not needed for react as it escapes by default
      escapeValue: false,
    },
  });

export default i18n;
