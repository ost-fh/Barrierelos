import {User} from "../lib/api-client";

export class Authentication {
  public isAuthenticated: boolean = false;
  public isBasicAuthentication: boolean = true;
  public user?: User = undefined;
}
