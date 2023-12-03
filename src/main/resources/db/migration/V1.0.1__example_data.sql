---------------------------------------------------------------------------
-- User and Credential Tables                                            --
---------------------------------------------------------------------------

TRUNCATE "user" CASCADE;
TRUNCATE "credential" CASCADE;

INSERT INTO "user"("user_id", "username", "firstname", "lastname", "email", "roles", "deleted")
VALUES
  (1, 'admin', 'Hans', 'Root', 'admin@localhost', '{ADMIN}'::role_enum[], FALSE),
  (2, 'moderator', 'Hans', 'Admin', 'moderator@localhost', '{MODERATOR}'::role_enum[], FALSE),
  (3, 'contributor', 'Hans', 'Moderator', 'contributor@localhost', '{CONTRIBUTOR}'::role_enum[], FALSE),
  (4, 'viewer', 'Hans', 'Creator', 'viewer@localhost', '{VIEWER}'::role_enum[], FALSE),
  (5, 'google', 'Hans', 'Google', 'google@localhost', '{CONTRIBUTOR,VIEWER}'::role_enum[], FALSE);

INSERT INTO "credential"("credential_id", "user_fk", "password", "issuer", "subject")
VALUES
  (1, 1, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (2, 2, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (3, 3, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (4, 4, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (5, 5, NULL, 'accounts.google.com', '117148868352469216810');

SELECT SETVAL('user_user_id_seq', (SELECT MAX("user_id") FROM "user"));
SELECT SETVAL('credential_credential_id_seq', (SELECT MAX("credential_id") FROM "credential"));


---------------------------------------------------------------------------
-- Reports                                                               --
---------------------------------------------------------------------------

TRUNCATE "website_details" CASCADE;
TRUNCATE "webpage_details" CASCADE;

INSERT INTO "website_details"
  ("website_details_id", "score")
VALUES
  (1, 0)
;

INSERT INTO "webpage_details"
  ("webpage_details_id", "website_details_fk", "path", "score")
VALUES
  (1, 1, '/', 30),
  (2, 1, '/test', 50),
  (3, 1, '/about', 80)
;
