INSERT INTO "user"("user_id", "username", "password", "firstname", "lastname", "email") VALUES
    (1, 'admin', crypt('password', gen_salt('bf')), 'Hans', 'Root', 'admin@localhost'),
    (2, 'moderator', crypt('password', gen_salt('bf')), 'Hans', 'Admin', 'moderator@localhost'),
    (3, 'contributor', crypt('password', gen_salt('bf')), 'Hans', 'Moderator', 'contributor@localhost'),
    (4, 'viewer', crypt('password', gen_salt('bf')), 'Hans', 'Creator', 'viewer@localhost');

INSERT INTO "user_role"("user_role_id", "user_fk", "role") VALUES
    (1, 1, 'ADMIN'::role_enum),
    (2, 2, 'MODERATOR'::role_enum),
    (3, 3, 'CONTRIBUTOR'::role_enum),
    (4, 4, 'VIEWER'::role_enum);

SELECT setval('user_user_id_seq', (SELECT MAX("user_id") FROM "user"));
SELECT setval('user_role_user_role_id_seq', (SELECT MAX("user_role_id") FROM "user_role"));
