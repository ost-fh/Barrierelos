import {OpenAPI, User, UserControllerService} from "../lib/api-client";
import {Authentication} from "../model/Authentication.ts";
import {Dispatch, SetStateAction} from "react";

export class AuthenticationService {
  private static readonly KEY_USERNAME = "username";
  private static readonly KEY_PASSWORD = "password";
  private static readonly KEY_TOKEN = "token";
  private static readonly KEY_AUTHENTICATION = "authentication";

  public static loginWithBasicAuthentication(username: string, password: string, onSuccess: (authentication: Authentication) => void, onError: (errorCode?: number) => void, setAuthentication: Dispatch<SetStateAction<Authentication>>): void {
    this.login(username, password, undefined, onSuccess, onError, setAuthentication);
  }

  public static loginWithTokenAuthentication(token: string, onSuccess: (authentication: Authentication) => void, onError: (errorCode?: number) => void, setAuthentication: Dispatch<SetStateAction<Authentication>>): void {
    this.login(undefined, undefined, token, onSuccess, onError, setAuthentication);
  }

  public static login(username: string | undefined, password: string | undefined, token: string | undefined, onSuccess: (authentication: Authentication) => void, onError: (errorCode?: number) => void, setAuthentication: Dispatch<SetStateAction<Authentication>>): void {
    if(((username === undefined || password === undefined) && token === undefined) || ((username !== undefined || password !== undefined) && token !== undefined)) {
      return onError();
    }
    else {
      OpenAPI.USERNAME = username;
      OpenAPI.PASSWORD = password;
      OpenAPI.TOKEN = token;

      UserControllerService.login()
        .then((user) => {
          if(password !== undefined) {
            sessionStorage.removeItem(AuthenticationService.KEY_TOKEN);
            sessionStorage.setItem(AuthenticationService.KEY_PASSWORD, password);
          }
          else if(token !== undefined) {
            sessionStorage.removeItem(AuthenticationService.KEY_PASSWORD);
            sessionStorage.setItem(AuthenticationService.KEY_TOKEN, token);
          }

          AuthenticationService.changeUser(user, onSuccess, setAuthentication);
        })
        .catch((error) => {
          AuthenticationService.clear();

          onError(error.status);
        });
    }
  }

  public static changeUser(user: User, onSuccess: (authentication: Authentication) => void, setAuthentication: Dispatch<SetStateAction<Authentication>>): void {
    OpenAPI.USERNAME = user.username;

    const authentication = new Authentication();
    authentication.isAuthenticated = true;
    authentication.user = user;

    sessionStorage.setItem(AuthenticationService.KEY_USERNAME, user.username);
    sessionStorage.setItem(AuthenticationService.KEY_AUTHENTICATION, JSON.stringify(authentication));

    setAuthentication(authentication);

    onSuccess(authentication);
  }

  public static logout(setAuthentication: Dispatch<SetStateAction<Authentication>>) {
    AuthenticationService.clear();

    setAuthentication(new Authentication());
  }

  public static load(setAuthentication: Dispatch<SetStateAction<Authentication>>) {
    const username = sessionStorage.getItem(AuthenticationService.KEY_USERNAME);
    const password = sessionStorage.getItem(AuthenticationService.KEY_PASSWORD);
    const token = sessionStorage.getItem(AuthenticationService.KEY_TOKEN);
    const authentication = sessionStorage.getItem(AuthenticationService.KEY_AUTHENTICATION);

    if(authentication) {
      setAuthentication(JSON.parse(authentication));
    }

    if(username !== null) {
      this.login(username, password ?? undefined, token ?? undefined, () => {}, () => {
        AuthenticationService.logout(setAuthentication);
      }, setAuthentication);
    }
  }

  private static clear() {
    sessionStorage.removeItem(AuthenticationService.KEY_USERNAME);
    sessionStorage.removeItem(AuthenticationService.KEY_PASSWORD);
    sessionStorage.removeItem(AuthenticationService.KEY_TOKEN);
    sessionStorage.removeItem(AuthenticationService.KEY_AUTHENTICATION);

    OpenAPI.USERNAME = undefined;
    OpenAPI.PASSWORD = undefined;
    OpenAPI.TOKEN = undefined;
  }

  public static changePassword(password: string | undefined) {
    OpenAPI.PASSWORD = password;
  }

  public static getPassword(): string | undefined {
    return sessionStorage.getItem(AuthenticationService.KEY_PASSWORD)?.toString();
  }
}
