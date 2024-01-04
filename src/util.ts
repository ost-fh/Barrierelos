import {ReactNode} from "react";
import {MIN_LENGTH_PASSWORD, MIN_LENGTH_USERNAME} from "./constants.ts";
import {Report} from "./lib/api-client";
import {TFunction} from "i18next";

export interface Props { children: ReactNode; }

export function isValidEmail(email: string) {
  return /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z0-9.-]+/.test(email);
}

export function isValidUsername(username: string) {
  return username.length >= MIN_LENGTH_USERNAME && !username.includes('@');
}

export function isValidPassword(password: string) {
  return password.length >= MIN_LENGTH_PASSWORD;
}

export function convertReasonToString(reason: Report.reason, t: TFunction<"translation", undefined>) {
  return t(`ReasonEnum.${reason}`);
}

export function convertStateToString(state: Report.state, t: TFunction<"translation", undefined>) {
  return t(`StateEnum.${state ?? "OPEN"}`);
}

export function convertRolesToString(roles: Array<'ADMIN' | 'MODERATOR' | 'CONTRIBUTOR' | 'VIEWER'>, t: TFunction<"translation", undefined>) {
  if(roles.includes('ADMIN')) {
    return t('RoleEnum.ADMIN');
  }
  else if(roles.includes('MODERATOR')) {
    return t('RoleEnum.MODERATOR');
  }
  else if(roles.includes('CONTRIBUTOR')) {
    return t('RoleEnum.CONTRIBUTOR');
  }
  else if(roles.includes('VIEWER')) {
    return t('RoleEnum.VIEWER');
  }
  else {
    return t('RoleEnum.GUEST');
  }
}

export function convertDeletedToString(deleted: boolean, t: TFunction<"translation", undefined>) {
  return t(`DeleteEnum.${deleted ? 'INACTIVE' : 'ACTIVE'}`);
}

export function convertTimestampToLocalDatetime(timestamp: number) {
  const date = new Date(timestamp);

  return date.toLocaleString('sv').replace(' ', ', ');
}

export function convertTimestampToLocalDate(timestamp: number) {
  const date = new Date(timestamp);

  return date.toLocaleString('sv').split(' ')[0];
}

export function convertTimestampToLocalTime(timestamp: number) {
  const date = new Date(timestamp);

  return date.toLocaleString('sv').split(' ')[1];
}
