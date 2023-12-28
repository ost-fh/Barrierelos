import {AuthenticationService} from "../services/AuthenticationService.ts";
import {useContext, useEffect} from "react";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";
import {Navigate} from "react-router-dom";

function Logout() {
  const {authentication, setAuthentication} = useContext(AuthenticationContext);

  useEffect(() => {
    if (authentication.isAuthenticated) {
      AuthenticationService.logout(setAuthentication)
    }
  });

  return <Navigate to="/" />
}

export default Logout;
