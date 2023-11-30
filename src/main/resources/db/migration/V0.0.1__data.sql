TRUNCATE "user" CASCADE;
TRUNCATE "credential" CASCADE;

INSERT INTO "user"("user_id", "username", "firstname", "lastname", "email", "roles", "deleted") VALUES
    (1, 'admin', 'Hans', 'Root', 'admin@localhost', '{ADMIN}'::role_enum[], false),
    (2, 'moderator', 'Hans', 'Admin', 'moderator@localhost', '{MODERATOR}'::role_enum[], false),
    (3, 'contributor', 'Hans', 'Moderator', 'contributor@localhost', '{CONTRIBUTOR}'::role_enum[], false),
    (4, 'viewer', 'Hans', 'Creator', 'viewer@localhost', '{VIEWER}'::role_enum[], false),
    (5, 'google', 'Hans', 'Google', 'google@localhost', '{CONTRIBUTOR,VIEWER}'::role_enum[], false);

INSERT INTO "credential"("credential_id", "user_fk", "password", "issuer", "subject") VALUES
    (1, 1, crypt('password', gen_salt('bf')), NULL, NULL),
    (2, 2, crypt('password', gen_salt('bf')), NULL, NULL),
    (3, 3, crypt('password', gen_salt('bf')), NULL, NULL),
    (4, 4, crypt('password', gen_salt('bf')), NULL, NULL),
    (5, 5, NULL, 'accounts.google.com', '117148868352469216810');

SELECT setval('user_user_id_seq', (SELECT MAX("user_id") FROM "user"));
SELECT setval('credential_credential_id_seq', (SELECT MAX("credential_id") FROM "credential"));
