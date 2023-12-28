import {User} from "../lib/api-client";

export class Authentication {
  public isAuthenticated: boolean = false;
  public user?: User = undefined;
}
