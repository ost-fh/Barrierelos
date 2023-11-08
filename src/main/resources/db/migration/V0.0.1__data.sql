INSERT INTO "user"("user_id", "username", "firstname", "lastname", "email", "password", "issuer", "subject", "roles") VALUES
    (1, 'admin', 'Hans', 'Root', 'admin@localhost', crypt('password', gen_salt('bf')), NULL, NULL, '{ADMIN}'::role_enum[]),
    (2, 'moderator', 'Hans', 'Admin', 'moderator@localhost', crypt('password', gen_salt('bf')), NULL, NULL, '{MODERATOR}'::role_enum[]),
    (3, 'contributor', 'Hans', 'Moderator', 'contributor@localhost', crypt('password', gen_salt('bf')), NULL, NULL, '{CONTRIBUTOR}'::role_enum[]),
    (4, 'viewer', 'Hans', 'Creator', 'viewer@localhost', crypt('password', gen_salt('bf')), NULL, NULL, '{VIEWER}'::role_enum[]),
    (5, 'google', 'Hans', 'Google', 'google@localhost', NULL, 'accounts.google.com', '117148868352469216810', '{CONTRIBUTOR,VIEWER}'::role_enum[]);

SELECT setval('user_user_id_seq', (SELECT MAX("user_id") FROM "user"));
