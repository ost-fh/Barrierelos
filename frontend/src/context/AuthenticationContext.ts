import {createContext, Dispatch, SetStateAction} from "react";
import {Authentication} from "../model/Authentication.ts";

export interface AuthenticationType {
  authentication: Authentication;
  setAuthentication?: Dispatch<SetStateAction<Authentication>>;
}

export const AuthenticationContext = createContext<AuthenticationType>({
  authentication: new Authentication(),
  setAuthentication: undefined
});
