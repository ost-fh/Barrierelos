export interface Token {
  name: string | null;
  email: string;
  iss: string;
  sub: string;
  email_verified?: boolean;
  picture?: string;
  family_name: string;
  given_name: string;
  exp?: number;
  iat?: number;
  locale: string;
}
