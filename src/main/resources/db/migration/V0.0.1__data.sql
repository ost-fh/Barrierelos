INSERT INTO "user"("user_id", "username", "firstname", "lastname", "email", "password", "issuer", "subject") VALUES
    (1, 'admin', 'Hans', 'Root', 'admin@localhost', crypt('password', gen_salt('bf')), NULL, NULL),
    (2, 'moderator', 'Hans', 'Admin', 'moderator@localhost', crypt('password', gen_salt('bf')), NULL, NULL),
    (3, 'contributor', 'Hans', 'Moderator', 'contributor@localhost', crypt('password', gen_salt('bf')), NULL, NULL),
    (4, 'viewer', 'Hans', 'Creator', 'viewer@localhost', crypt('password', gen_salt('bf')), NULL, NULL),
    (5, 'google', 'Hans', 'Google', 'google@localhost', NULL, 'accounts.google.com', '117148868352469216810');

INSERT INTO "user_role"("user_role_id", "user_fk", "role") VALUES
    (1, 1, 'ADMIN'::role_enum),
    (2, 2, 'MODERATOR'::role_enum),
    (3, 3, 'CONTRIBUTOR'::role_enum),
    (4, 4, 'VIEWER'::role_enum),
    (5, 5, 'ADMIN'::role_enum);

SELECT setval('user_user_id_seq', (SELECT MAX("user_id") FROM "user"));
SELECT setval('user_role_user_role_id_seq', (SELECT MAX("user_role_id") FROM "user_role"));
