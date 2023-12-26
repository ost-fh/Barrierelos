import "./App.css"
import {Route, Routes} from "react-router-dom";
import WebsitePage from "./pages/website/WebsitePage.tsx";
import HomePage from "./pages/HomePage.tsx";
import {NotFound} from "./pages/NotFound.tsx";
import NavBar from "./components/NavBar.tsx";
import {useTranslation} from "react-i18next";
import * as locales from "@mui/material/locale";
import {createTheme, ThemeProvider, useTheme} from "@mui/material";
import {useMemo, useState} from "react";
import WebsitesPage from "./pages/websites/WebsitesPage.tsx";
import {Helmet, HelmetProvider} from "react-helmet-async";

type MuiLocales = "enUS" | "deDE";

function App() {
  const {i18n} = useTranslation()

  const [muiLocale, setMuiLocale] = useState<MuiLocales>(mapToMuiLocale(i18n.resolvedLanguage));
  const theme = useTheme();
  theme.palette.contrastThreshold = 4.5
  const themeWithLocale = useMemo(
    () => createTheme(theme, locales[muiLocale]),
    [muiLocale, theme],
  );

  i18n.on("languageChanged", language => {
    setMuiLocale(mapToMuiLocale(language))
  })

  return (
    <>
      <ThemeProvider theme={themeWithLocale}>
        <HelmetProvider>
          <Helmet>
            <html lang={i18n.resolvedLanguage}/>
            <meta charSet="utf-8"/>
            <title>Barrierelos</title>
          </Helmet>
          <header>
            <NavBar/>
          </header>
          <main>
            <Routes>
              <Route path="/" element={<HomePage/>}/>
              <Route path="/websites" element={<WebsitesPage/>}/>
              <Route path="/websites/:websiteId" element={<WebsitePage/>}/>
              <Route path="*" element={<NotFound/>}/>
            </Routes>
          </main>
        </HelmetProvider>
      </ThemeProvider>
    </>
  )

  function mapToMuiLocale(locale?: string): MuiLocales {
    if (locale === undefined) return "enUS"
    switch (locale) {
      case "en":
        return "enUS"
      case "de":
        return "deDE"
      default:
        throw Error(`Locale '${locale}' is not configured`)
    }
  }
}

export default App
