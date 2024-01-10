import {Report} from "../lib/api-client";
import {TFunction} from "i18next";

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

