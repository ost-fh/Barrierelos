import {ReactNode} from "react";

export interface Props { children: ReactNode; }

export function isValidEmail(email: string) {
  return /\S+@\S+\.\S+/.test(email);
}
