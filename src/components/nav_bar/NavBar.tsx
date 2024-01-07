import {Link, NavLink} from "react-router-dom"
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
          <img className="barrierelos-logo" src={barrierelosLogo} alt={t("General.barrierelosLogoAlt")}/>
        </div>
        <Stack direction="row" spacing={4} className="header-actions">
          <div className="profile-area">
            <ButtonGroup variant="text">
              {authentication.isAuthenticated ? (
                <Button to="/logout" onClick={resetFocus} component={Link}>Logout</Button>
              ) : (
                <Button to="/login" onClick={resetFocus} component={Link}>Login</Button>
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
          Home
        </NavLink>
        <NavLink to="/websites" onClick={resetFocus} tabIndex={isMobileNav && !isNavExpanded ? -1 : undefined}>
          Websites
        </NavLink>
        <NavLink to="/faq" onClick={resetFocus} tabIndex={isMobileNav && !isNavExpanded ? -1 : undefined}>
          FAQ
        </NavLink>
        {authentication.isAuthenticated ? (
          <>
            <NavLink to="/profile" onClick={resetFocus} tabIndex={isMobileNav && !isNavExpanded ? -1 : undefined}>
              Profile
            </NavLink>
            <NavLink to="/reports" onClick={resetFocus} tabIndex={isMobileNav && !isNavExpanded ? -1 : undefined}>
              Reports
            </NavLink>
          </>
        ) : null}
      </nav>
    </>
  )

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
