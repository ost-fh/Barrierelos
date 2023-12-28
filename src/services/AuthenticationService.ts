import {OpenAPI, User, UserControllerService} from "../lib/api-client";
import {Authentication} from "../model/Authentication.ts";
import {Dispatch, SetStateAction} from "react";

export class AuthenticationService {
  private static readonly KEY_USERNAME = "username";
  private static readonly KEY_PASSWORD = "password";
  private static readonly KEY_AUTHENTICATION = "authentication";

  public static login(username: string, password: string, onSuccess: (authentication: Authentication) => void, onError: (error: string) => void, setAuthentication: Dispatch<SetStateAction<Authentication>> | undefined): void {
    OpenAPI.USERNAME = username;
    OpenAPI.PASSWORD = password;

    UserControllerService.login(username)
      .then((user) => {
        sessionStorage.setItem(AuthenticationService.KEY_PASSWORD, password);

        AuthenticationService.changeUser(user, onSuccess, onError, setAuthentication);
      })
      .catch((error) => onError(error));
  }

  public static changeUser(user: User, onSuccess: (authentication: Authentication) => void, onError: (error: string) => void, setAuthentication: Dispatch<SetStateAction<Authentication>> | undefined): void {
    OpenAPI.USERNAME = user.username;

    const authentication = new Authentication();
    authentication.isAuthenticated = true;
    authentication.user = user;

    sessionStorage.setItem(AuthenticationService.KEY_USERNAME, user.username);
    sessionStorage.setItem(AuthenticationService.KEY_AUTHENTICATION, JSON.stringify(authentication));

    if(setAuthentication) {
      setAuthentication(authentication);

      onSuccess(authentication);
    }
    else {
      onError("No authentication.");
    }
  }

  public static logout(setAuthentication: Dispatch<SetStateAction<Authentication>> | undefined) {
    sessionStorage.removeItem(AuthenticationService.KEY_USERNAME);
    sessionStorage.removeItem(AuthenticationService.KEY_PASSWORD);
    sessionStorage.removeItem(AuthenticationService.KEY_AUTHENTICATION);

    if(setAuthentication) {
      setAuthentication(new Authentication());
    }
  }

  public static load(setAuthentication: Dispatch<SetStateAction<Authentication>> | undefined) {
    const username = sessionStorage.getItem(AuthenticationService.KEY_USERNAME);
    const password = sessionStorage.getItem(AuthenticationService.KEY_PASSWORD);
    const authentication = sessionStorage.getItem(AuthenticationService.KEY_AUTHENTICATION);

    if(setAuthentication && authentication) {
      setAuthentication(JSON.parse(authentication));
    }

    if(username !== null && password != null) {
      this.login(username, password, () => {}, () => {
        this.logout(setAuthentication);
      }, setAuthentication);
    }
  }

  public static changePassword(password: string | undefined) {
    OpenAPI.PASSWORD = password;
  }

  public static getPassword(): string | undefined {
    return sessionStorage.getItem(AuthenticationService.KEY_PASSWORD)?.toString();
  }
}
