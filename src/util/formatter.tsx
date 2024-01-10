import {User, Webpage, Website} from "../lib/api-client";
import {Link} from "@mui/material";

export function formatScore(score: number | undefined): string {
  if (score == null) {
    return "N/A";
  }
  return Math.round(score).toString();
}

export function getScoreClass(score: number | undefined): string {
  if (score === undefined) {
    return "";
  }
  if (score >= 80) {
    return "good"
  }
  if (score >= 50) {
    return "medium"
  }
  return "bad"
}

export function getScoreColor(score: number | undefined): string {
  const rating = getScoreClass(score);
  switch (rating) {
    case "good":
      return "#27ae60";
    case "medium":
      return "#f1c40f";
    case "bad":
      return "#e74c3c";
    default:
      return "#2980b9";
  }
}

export function formatViolationRatio(n: number, digits: number): string {
  const decimalFactor = Math.pow(10, digits);
  n = parseFloat((n * decimalFactor).toFixed(11));
  return String(Math.round(n) / decimalFactor);
}

export function compareStrings(a: string, b: string): number {
  return a.localeCompare(b);
}

export function convertWebsiteToLink(website: Website | undefined) {
  if (website) {
    return <Link href={`/websites/${website.id}`}>{website.domain}</Link>;
  } else {
    return "unknown";
  }
}

export function convertWebpageToLink(webpage: Webpage | undefined) {
  if (webpage) {
    return <Link href={`/websites/${webpage.website.id}`}>{webpage.displayUrl}</Link>;
  } else {
    return "unknown";
  }
}

export function convertUserToLink(user: User | undefined) {
  if (user) {
    return <Link href={`/user/${user.id}`}>{user.firstname} {user.lastname} ({user.username})</Link>;
  } else {
    return "unknown";
  }
}