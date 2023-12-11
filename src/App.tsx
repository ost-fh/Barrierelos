import "./App.css"
import {Route, Routes} from "react-router-dom";
import WebsitePage from "./pages/website/WebsitePage.tsx";
import Home from "./pages/Home.tsx";
import {NotFound} from "./pages/NotFound.tsx";
import {Helmet} from "react-helmet";
import NavBar from "./components/NavBar.tsx";
import {useTranslation} from "react-i18next";
import * as locales from "@mui/material/locale";
import {createTheme, ThemeProvider, useTheme} from "@mui/material";
import {useMemo, useState} from "react";

type MuiLocales = "enUS" | "deDE";

function App() {
  const {i18n} = useTranslation()

  const [muiLocale, setMuiLocale] = useState<MuiLocales>(mapToMuiLocale(i18n.resolvedLanguage));
  const theme = useTheme();
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
            <Route path="/" element={<Home/>}/>
            <Route path="/websites/:websiteId" element={<WebsitePage/>}/>
            <Route path="*" element={<NotFound/>}/>
          </Routes>
        </main>
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
