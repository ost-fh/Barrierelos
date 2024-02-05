import {Link, NavLink, useLocation} from "react-router-dom"
import "./NavBar.css"
import {useTranslation} from "react-i18next";
import {Button, ButtonGroup, Stack, useMediaQuery, useTheme} from "@mui/material";
import {useContext, useState} from "react";
import {AuthenticationContext} from "../../context/AuthenticationContext.ts";
import barrierelosLogo from "../../assets/logo.svg";
import OpenMenuIcon from "@mui/icons-material/Menu";
import CloseMenuIcon from "@mui/icons-material/Close";
import IconButton from "@mui/material/IconButton";

export default function NavBar() {
  const {t, i18n} = useTranslation()
  const {authentication} = useContext(AuthenticationContext);
  const location = useLocation();

  const theme = useTheme();
  const isMobileNav = useMediaQuery(theme.breakpoints.down("sm"))
  const [isNavExpanded, setIsNavExpanded] = useState(false);

  const changeLanguage = async (lng: string) => {
    await i18n.changeLanguage(lng);
  }

  return (
    <>
      <div>
        <Link className="skip-link" to="#main-content" onClick={focusMainContent}>{t("General.skipLinkLabel")}</Link>
      </div>
      <Stack direction="row" className="logo-bar" useFlexGap flexWrap="wrap" spacing={4}>
        <div>
          <a href="/">
            <img className="barrierelos-logo" src={barrierelosLogo} alt={t("General.barrierelosLogoAlt")}/>
          </a>
        </div>
        <Stack direction="row" spacing={4} className="header-actions">
          <div className="profile-area">
            <ButtonGroup variant="text">
              {authentication.isAuthenticated ? (
                <Button to="/logout" onClick={resetFocus} component={Link}>{t("NavBar.logout")}</Button>
              ) : (
                <Button to="/login" onClick={resetFocus} component={Link}>{t("NavBar.login")}</Button>
              )}
            </ButtonGroup>
          </div>
          <div className="language-selection">
            <ButtonGroup variant="text">
              <Button
                className="langBtn"
                disabled={i18n.resolvedLanguage === "de"}
                onClick={() => changeLanguage("de")}
                lang={"de"}
                aria-label={"Sprache auf Deutsch wechseln"}>
                DE
              </Button>
              <Button
                className="langBtn"
                disabled={i18n.resolvedLanguage === "en"}
                onClick={() => changeLanguage("en")}
                lang={"en"}
                aria-label={"Change language to English"}>
                EN
              </Button>
            </ButtonGroup>
          </div>
        </Stack>
      </Stack>
      <nav className={isNavExpanded ? "expanded" : undefined}>
        <IconButton
          aria-label={isNavExpanded ? t("NavBar.closeNavigationMenuBtnAriaLabel") : t("NavBar.openNavigationMenuBtnAriaLabel")}
          className="hamburger-btn"
          disableRipple
          onClick={toggleExpanded}
        >
          {isNavExpanded ? (
            <CloseMenuIcon sx={{color: "white"}}/>
          ) : (
            <OpenMenuIcon sx={{color: "white"}}/>
          )}
        </IconButton>
        <NavLink to="/" onClick={resetFocus} tabIndex={isMobileNav && !isNavExpanded ? -1 : undefined}>
          {t("NavLinks.home")}
        </NavLink>
        <NavLink to="/websites" onClick={resetFocus} tabIndex={isMobileNav && !isNavExpanded ? -1 : undefined}>
          {t("NavLinks.websites")}
        </NavLink>
        <NavLink to="/contribute" onClick={resetFocus} tabIndex={isMobileNav && !isNavExpanded ? -1 : undefined}>
          {t("NavLinks.contribute")}
        </NavLink>
        <NavLink to="/faq" onClick={resetFocus} tabIndex={isMobileNav && !isNavExpanded ? -1 : undefined}>
          {t("NavLinks.faq")}
        </NavLink>
        {authentication.isAuthenticated ? (
          <>
            <NavLink to="/contributions" onClick={resetFocus} tabIndex={isMobileNav && !isNavExpanded ? -1 : undefined}>
              {t("NavLinks.contributions")}
            </NavLink>
            <NavLink to="/reports" onClick={resetFocus} tabIndex={isMobileNav && !isNavExpanded ? -1 : undefined}>
              {t("NavLinks.reports")}
            </NavLink>
            <NavLink to="/profile" onClick={resetFocus} tabIndex={isMobileNav && !isNavExpanded ? -1 : undefined}>
              {t("NavLinks.profile")}
            </NavLink>
            {isCurrentLocation('/user') ? (
              <NavLink to={location.pathname} onClick={resetFocus} tabIndex={isMobileNav && !isNavExpanded ? -1 : undefined}>
                {t("NavLinks.user")}
              </NavLink>
            ) : null}
          </>
        ) : null}
      </nav>
    </>
  )

  function isCurrentLocation(path: string) {
    return location.pathname.toLowerCase().startsWith(path);
  }

  function toggleExpanded() {
    setIsNavExpanded(!isNavExpanded);
  }

  function resetFocus() {
    setIsNavExpanded(false);
    const body = document.body;
    if (body) {
      body.focus();
    }
  }
}

function focusMainContent() {
  const mainContent = document.querySelector(`#main-content`) as HTMLElement | null;
  if (mainContent) {
    mainContent.focus();
  }
}
