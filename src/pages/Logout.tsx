import {AuthenticationService} from "../services/AuthenticationService.ts";
import {useContext, useEffect} from "react";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";
import {Navigate} from "react-router-dom";
import {googleLogout} from "@react-oauth/google";

function Logout() {
  const {authentication, setAuthentication} = useContext(AuthenticationContext);

  useEffect(() => {
    if(authentication.isAuthenticated) {
      if(setAuthentication) {
        AuthenticationService.logout(setAuthentication)
      }
    }
  });

  googleLogout();

  return <Navigate to="/" />
}

export default Logout;
