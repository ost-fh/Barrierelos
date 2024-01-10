import {AuthenticationService} from "../services/AuthenticationService.ts";
import {useContext, useEffect} from "react";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";
import {Navigate} from "react-router-dom";
import {googleLogout} from "@react-oauth/google";
import {PAGE_AFTER_LOGOUT} from "../constants.ts";

function LogoutPage() {
  const {authentication, setAuthentication} = useContext(AuthenticationContext);

  useEffect(() => {
    if(authentication.isAuthenticated) {
      if(setAuthentication) {
        AuthenticationService.logout(setAuthentication)
      }
    }
  });

  googleLogout();

  return <Navigate to={PAGE_AFTER_LOGOUT} />
}

export default LogoutPage;
