import {MIN_LENGTH_PASSWORD, MIN_LENGTH_USERNAME} from "./constants.ts";

export function isValidEmail(email: string) {
  return /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z0-9.-]+/.test(email);
}

export function isValidUsername(username: string) {
  return username.length >= MIN_LENGTH_USERNAME && !username.includes("@");
}

export function isValidPassword(password: string) {
  return password.length >= MIN_LENGTH_PASSWORD;
}

export function isValidWebsiteUrl(url: string) {
  return /^https?:\/\/(([^/?]+\.)+[^/?]+)(\/.*)?$/.test(url);
}
