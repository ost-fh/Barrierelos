import {Link as RouterLink} from "react-router-dom";
import {Link, Stack} from "@mui/material";
import "./FooterBar.css"

function FooterBar() {
  return (
    <Stack direction="row" spacing={4} width="100%" justifyContent="space-between">
      <span>© 2024 Barrierelos</span>
      <Stack direction={"row"} spacing={4} className="footer-links">
        <Link to="/privacy-policy" underline="always" component={RouterLink}>Privacy Policy</Link>
        <Link to="/impressum" underline="always" component={RouterLink}>Impressum</Link>
      </Stack>
    </Stack>
  )
}

export default FooterBar
