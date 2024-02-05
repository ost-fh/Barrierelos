import React, {useContext} from "react"
import {Navigate} from "react-router-dom";
import {AuthenticationContext} from "../context/AuthenticationContext.ts";

export interface PrivateProps {
  Component: React.FunctionComponent
}

function Private({Component}: PrivateProps) {
  const {authentication} = useContext(AuthenticationContext);

  return authentication.isAuthenticated ? <Component /> : <Navigate to="/login" />
}

export default Private;
