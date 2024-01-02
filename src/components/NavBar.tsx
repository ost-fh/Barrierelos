import {NavLink, useNavigate} from "react-router-dom"
import "./NavBar.css"
import {useTranslation} from "react-i18next";
import {Button, ButtonGroup} from "@mui/material";
import {t} from "i18next";
import {useContext} from "react";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";

export default function NavBar() {
  const {i18n} = useTranslation()
  const {authentication} = useContext(AuthenticationContext);
  const navigate = useNavigate();

  const changeLanguage = async (lng: string) => {
    await i18n.changeLanguage(lng);
  }

  return (
    <>
      <nav className="navbar">
        <ul>
          <li>
            <NavLink to="/" onClick={resetFocus}>Home</NavLink>
          </li>
          <li>
            <NavLink to="/websites" end onClick={resetFocus}>Websites</NavLink>
          </li>
          <li>
            <NavLink to="/login" onClick={resetFocus}>Login</NavLink>
          </li>
          <li>
            <NavLink to="/signup" onClick={resetFocus}>Signup</NavLink>
          </li>
          <li>
            <NavLink to="/profile" onClick={resetFocus}>Profile</NavLink>
          </li>
          <li className="languageSelection">
            <ButtonGroup variant="text">
              {authentication.isAuthenticated ?
                <Button sx={{textTransform: 'lowercase'}}
                        onClick={() => navigate("/logout")}>Logout</Button>
                :
                <Button sx={{textTransform: 'lowercase'}}
                        onClick={() => navigate("/login")}>Login</Button>
              }
            </ButtonGroup>
            <ButtonGroup variant="text" aria-label={t("Navbar.languageSelectionLabel")}>
              <Button className="langBtn" disabled={i18n.resolvedLanguage === "de"}
                      onClick={() => changeLanguage("de")}>DE</Button>
              <Button className="langBtn" disabled={i18n.resolvedLanguage === "en"}
                      onClick={() => changeLanguage("en")}>EN</Button>
            </ButtonGroup>
          </li>
        </ul>
      </nav>
    </>
  )
}

function resetFocus() {
  const body = document.body;
  if (body) {
    body.focus();
  }
}
