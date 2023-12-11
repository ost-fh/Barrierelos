import {NavLink} from "react-router-dom"
import "./NavBar.css"
import {useTranslation} from "react-i18next";
import {Button, ButtonGroup} from "@mui/material";
import {t} from "i18next";

function NavBar() {
  const {i18n} = useTranslation()

  const changeLanguage = async (lng: string) => {
    await i18n.changeLanguage(lng);
  }
  return (
    <>
      <nav className="navbar">
        <ul>
          <li>
            <NavLink to="/">Home</NavLink>
          </li>
          <li>
            <NavLink to="/websites/1">Website 1</NavLink>
          </li>
          <li>
            <NavLink to="/websites/2">Website 2</NavLink>
          </li>
          <li className="languageSelection">
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

export default NavBar
