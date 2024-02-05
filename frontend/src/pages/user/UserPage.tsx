import {Alert, CircularProgress} from "@mui/material";
import {useEffect, useState} from "react";
import {Helmet} from "react-helmet-async";
import {useTranslation} from "react-i18next";
import {User, UserControllerService} from "../../lib/api-client";
import {useParams} from "react-router-dom";
import UserProfile from "./UserProfile.tsx";

interface UserPageParams extends Record<string, string> {
  userId: string
}

function UserPage() {
  const {t} = useTranslation();
  const [user, setUser] = useState<User|undefined>();
  const [error, setError] = useState<string|undefined>();

  const params = useParams<UserPageParams>();

  if(params.userId === undefined) {
    throw Error('Path param userId is missing');
  }

  const userId = parseInt(params.userId);

  const loadUser = () => {
    UserControllerService.getUser(userId)
      .then((user) => {
        setError(undefined);
        setUser(user);
      })
      .catch(() => setError(t('UserPage.error')));
  }

  useEffect(() => { loadUser(); }, []);

  return (
    <>
      <Helmet>
        <title>{t('UserPage.title')} - {t("General.title")}</title>
      </Helmet>
      <h1>{t('UserPage.title')}</h1>
      {user ? (
        <UserProfile user={user}/>
      ) : (
        error ? (
          <>
            <Alert sx={{ my: 1 }} severity="error">{error}</Alert>
          </>
        ) : (
          <>
            <CircularProgress size="1.5rem" sx={{color: "background"}}/>
          </>
        )
      )}
    </>
  )
}

export default UserPage;
