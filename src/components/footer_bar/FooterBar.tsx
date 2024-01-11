import {Link as RouterLink} from "react-router-dom";
import {Link, Stack} from "@mui/material";
import "./FooterBar.css"
import {useTranslation} from "react-i18next";

function FooterBar() {
  const {t} = useTranslation()

  return (
    <Stack direction="row" spacing={4} width="100%" justifyContent="space-between">
      <span>© 2024 Barrierelos</span>
      <Stack direction={"row"} spacing={4} className="footer-links">
        <Link to="/privacy-policy" underline="always" component={RouterLink} sx={{ color: "white"}}>{t("NavLinks.privacyPolicy")}</Link>
        <Link to="/impressum" underline="always" component={RouterLink} sx={{ color: "white"}}>{t("NavLinks.impressum")}</Link>
      </Stack>
    </Stack>
  )
}

export default FooterBar
