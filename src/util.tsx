import {User, Webpage, Website} from "./lib/api-client";
import {Link} from "@mui/material";

export function convertWebsiteToLink(website: Website | undefined) {
  if(website) {
    return <Link href={`/websites/${website.id}`}>{website.domain}</Link>;
  }
  else {
    return "unknown";
  }
}

export function convertWebpageToLink(webpage: Webpage | undefined) {
  if(webpage) {
    return <Link href={`/websites/${webpage.website.id}`}>{webpage.displayUrl}</Link>;
  }
  else {
    return "unknown";
  }
}

export function convertUserToLink(user: User | undefined) {
  if(user) {
    return <Link href={`/user/${user.id}`}>{user.firstname} {user.lastname} ({user.username})</Link>;
  }
  else {
    return "unknown";
  }
}
