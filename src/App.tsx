import "./App.css"
import {Route, Routes} from "react-router-dom";
import LoginPage from "./pages/LoginPage.tsx";
import WebsitePage from "./pages/website/WebsitePage.tsx";
import HomePage from "./pages/HomePage.tsx";
import {NotFoundPage} from "./pages/NotFoundPage.tsx";
import NavBar from "./components/nav_bar/NavBar.tsx";
import {useTranslation} from "react-i18next";
import * as locales from "@mui/material/locale";
import {createTheme, ThemeProvider, useTheme} from "@mui/material";
import {useMemo, useState} from "react";
import {Helmet, HelmetProvider} from "react-helmet-async";
import {AuthenticationContext} from "./context/AuthenticationContext.ts";
import {Authentication} from "./model/Authentication.ts";
import {AuthenticationService} from "./services/AuthenticationService.ts";
import Private from "./components/Private.tsx";
import LogoutPage from "./pages/LogoutPage.tsx";
import ProfilePage from "./pages/ProfilePage.tsx";
import SignupPage from "./pages/SignupPage.tsx";
import WebsitesPage from "./pages/websites/WebsitesPage.tsx";
import PrivacyPolicyPage from "./pages/PrivacyPolicyPage.tsx";
import ImpressumPage from "./pages/ImpressumPage.tsx";
import {GoogleOAuthProvider} from "@react-oauth/google";
import {OAUTH_GOOGLE_CLIENT_ID} from "./util/constants.ts";
import FaqPage from "./pages/faq/FaqPage.tsx";
import ReportsPage from "./pages/reports/ReportsPage.tsx";
import UserPage from "./pages/UserPage.tsx";
import FooterBar from "./components/footer_bar/FooterBar.tsx";
import {Props} from "./util/login.ts";
import ContributePage from "./pages/contribute/ContributePage.tsx";
import ContributionsPage from "./pages/contributions/ContributionsPage.tsx";

type MuiLocales = "enUS" | "deDE";

function App() {
  const {i18n} = useTranslation()

  const [muiLocale, setMuiLocale] = useState<MuiLocales>(mapToMuiLocale(i18n.resolvedLanguage));
  const theme = useTheme();
  theme.palette.contrastThreshold = 4.5
  const themeWithLocale = useMemo(
    () => createTheme(theme, locales[muiLocale], {
      palette: {
        common: {
          black: "#000000",
          white: "#ffffff",
        },
        background: {
          paper: "#ffffff",
          surface: "#fafafa",
          default: "#ffffff",
        },
        primary: {
          main: "#006064",
          dark: "#004346",
          light: "#337f83",
          contrastText: "#ffffff",
        },
        secondary: {
          main: "#008080",
          dark: "#005959",
          light: "#339999",
          contrastText: "#ffffff",
        },
        error: {
          main: "#ba1a1a",
          dark: "#821212",
          light: "#c74747",
          contrastText: "#ffffff",
        },
        warning: {
          main: "#c57d20",
          dark: "#895716",
          light: "#d0974c",
          contrastText: "#ffffff",
        },
        info: {
          main: "#00668a",
          dark: "#004760",
          light: "#3384a1",
          contrastText: "#ffffff",
        },
        success: {
          main: "#0e8e3b",
          dark: "#096329",
          light: "#3ea462",
          contrastText: "#ffffff",
        },
        text: {
          primary: "#000000",
          secondary: "#6B6B6B",
          disabled: "#9E9E9E",
          hint: "#929292",
        }
      },
    }),
    [muiLocale, theme]
  );

  i18n.on("languageChanged", language => {
    setMuiLocale(mapToMuiLocale(language))
  })

  const [authentication, setAuthentication] = useState<Authentication>(new Authentication());

  if (!authentication.isAuthenticated) {
    AuthenticationService.load(setAuthentication);
  }

  function AuthenticationProvider({children}: Props) {
    return (
      <AuthenticationContext.Provider value={{authentication, setAuthentication}}>
        <GoogleOAuthProvider clientId={OAUTH_GOOGLE_CLIENT_ID}>
          {children}
        </GoogleOAuthProvider>
      </AuthenticationContext.Provider>
    );
  }

  return (
    <>
      <AuthenticationProvider>
        <ThemeProvider theme={themeWithLocale}>
          <HelmetProvider>
            <Helmet>
              <html lang={i18n.resolvedLanguage}/>
              <meta charSet="utf-8"/>
              <title>Barrierelos</title>
            </Helmet>
            <div className="content-container">
              <header>
                <NavBar/>
              </header>
              <main id="main-content" tabIndex={-1}>
                <Routes>
                  <Route path="/" element={<HomePage/>}/>
                  <Route path="/websites" element={<WebsitesPage/>}/>
                  <Route path="/websites/:websiteId" element={<WebsitePage/>}/>
                  <Route path="/contribute" element={<ContributePage/>}/>
                  <Route path="/faq" element={<FaqPage/>}/>
                  <Route path="/contributions" element={<Private Component={ContributionsPage}/>}/>
                  <Route path="/reports" element={<Private Component={ReportsPage}/>}/>
                  <Route path="/profile" element={<Private Component={ProfilePage}/>}/>
                  <Route path="/user/:userId" element={<Private Component={UserPage} />}/>
                  <Route path="/login" element={<LoginPage/>}/>
                  <Route path="/logout" element={<LogoutPage/>}/>
                  <Route path="/signup" element={<SignupPage/>}/>
                  <Route path="/impressum" element={<ImpressumPage/>}/>
                  <Route path="/privacy-policy" element={<PrivacyPolicyPage/>}/>
                  <Route path="*" element={<NotFoundPage/>}/>
                </Routes>
              </main>
            </div>
            <footer>
              <FooterBar/>
            </footer>
          </HelmetProvider>
        </ThemeProvider>
      </AuthenticationProvider>
    </>
  )
}

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

export default App
