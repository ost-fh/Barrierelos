---------------------------------------------------------------------------
-- User and Credential Tables                                            --
---------------------------------------------------------------------------

TRUNCATE "user" CASCADE;
TRUNCATE "credential" CASCADE;


INSERT INTO "user"("user_id", "username", "firstname", "lastname", "email", "roles", "deleted")
VALUES
  (1, 'admin', 'Hans', 'Admin', 'admin@localhost', '{ADMIN}'::role_enum[], FALSE),
  (2, 'moderator', 'Hans', 'Moderator', 'moderator@localhost', '{MODERATOR}'::role_enum[], FALSE),
  (3, 'contributor', 'Hans', 'Contributor', 'contributor@localhost', '{CONTRIBUTOR}'::role_enum[], FALSE),
  (4, 'viewer', 'Hans', 'Viewer', 'viewer@localhost', '{VIEWER}'::role_enum[], FALSE),
  (10, 'hans.muster', 'Hans', 'Muster', 'hans.muster@barrierelos.ch', '{CONTRIBUTOR}'::role_enum[], FALSE),
  (11, 'ursula.beispiel', 'Ursula', 'Beispiel', 'ursula.beispiel@barrierelos.ch', '{CONTRIBUTOR}'::role_enum[], FALSE),
  (12, 'céline.example', 'Céline', 'Example', 'celine.exemple@barrierelos.ch', '{CONTRIBUTOR}'::role_enum[], FALSE),
  (13, 'giovanni.esempio', 'Giovanni', 'Esempio', 'giovanni.esempio@barrierelos.ch', '{CONTRIBUTOR}'::role_enum[], FALSE),
  (14, 'bill.gates', 'Bill', 'Gates', 'bill.gates@barrierelos.ch', '{CONTRIBUTOR}'::role_enum[], FALSE),
  (15, 'elon.musk', 'Elon', 'Musk', 'elon.musk@barrierelos.ch', '{CONTRIBUTOR}'::role_enum[], FALSE),
  (16, 'twitter', 'Tweeting', 'Twitter', 'twitter@barrierelos.ch', '{CONTRIBUTOR}'::role_enum[], FALSE),
  (17, 'xxx', 'XXX', 'XXX', 'xxx@barrierelos.ch', '{CONTRIBUTOR}'::role_enum[], FALSE);

SELECT SETVAL('user_user_id_seq', (SELECT MAX("user_id") FROM "user"));

INSERT INTO "credential"("credential_id", "user_fk", "password", "issuer", "subject")
VALUES
  (1, 1, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (2, 2, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (3, 3, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (4, 4, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (10, 10, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (11, 11, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (12, 12, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (13, 13, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (14, 14, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (15, 15, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (16, 16, "crypt"('password', "gen_salt"('bf')), NULL, NULL),
  (17, 17, "crypt"('password', "gen_salt"('bf')), NULL, NULL);

SELECT SETVAL('credential_credential_id_seq', (SELECT MAX("credential_id") FROM "credential"));


---------------------------------------------------------------------------
-- Reports                                                                  --
---------------------------------------------------------------------------

TRUNCATE "report_message" CASCADE;
TRUNCATE "webpage_report" CASCADE;
TRUNCATE "website_report" CASCADE;
TRUNCATE "user_report" CASCADE;
TRUNCATE "report" CASCADE;

INSERT INTO "report"("report_id", "user_fk", "reason", "state", "modified", "created")
VALUES
  (1, 10, 'MISLEADING'::reason_enum, 'OPEN'::state_enum, '2024-01-01 15:06:33', '2024-01-01 15:06:33'),
  (2, 10, 'MISLEADING'::reason_enum, 'OPEN'::state_enum, '2024-01-01 17:23:42', '2024-01-01 17:23:44'),
  (3, 10, 'INCORRECT'::reason_enum, 'CLOSED'::state_enum, '2024-01-01 19:17:11', '2024-01-01 19:17:11'),
  (4, 10, 'INAPPROPRIATE'::reason_enum, 'OPEN'::state_enum, '2024-01-01 21:34:57', '2024-01-01 21:34:57');

INSERT INTO "user_report"("user_report_id", "report_fk", "user_fk")
VALUES
  (1, 1, 14),
  (2, 2, 15),
  (3, 3, 16),
  (4, 4, 17);

INSERT INTO "report_message"("report_message_id", "report_fk", "user_fk", "message", "modified", "created")
VALUES
  (1, 1, 10, 'This is not the real Bill Gates, I think.', '2024-01-01 15:06:33', '2024-01-01 15:06:33'),
  (2, 2, 10, 'Someone is impersonating Elon Musk here.', '2024-01-01 17:23:44', '2024-01-01 17:23:44'),
  (3, 3, 11, 'Twitter is no longer correct, it is now called X.', '2024-01-01 19:17:11', '2024-01-01 19:17:11'),
  (4, 4, 10, 'This username is really inappropriate.', '2024-01-01 21:34:57', '2024-01-01 21:34:57'),
  (5, 3, 10, 'I refuse to call it X, it is still Twitter to me.', '2024-01-01 23:14:28', '2024-01-01 23:14:28'),
  (6, 3, 13, 'I agree, X makes no sense.', '2024-01-02 04:03:12', '2024-01-02 04:03:12'),
  (7, 3, 2, 'It is officially called X now, so case closed.', '2024-01-02 05:17:49', '2024-01-02 05:17:49');

SELECT SETVAL('"report_report_id_seq"', (SELECT MAX("report_id") FROM "report"));
SELECT SETVAL('"user_report_user_report_id_seq"', (SELECT MAX("user_report_id") FROM "user_report"));
SELECT SETVAL('"report_message_report_message_id_seq"', (SELECT MAX("report_message_id") FROM "report_message"));

---------------------------------------------------------------------------
-- Tags                                                                  --
---------------------------------------------------------------------------

TRUNCATE "tag" CASCADE;

INSERT INTO "tag"("tag_id", "name")
VALUES
  (1, 'country.switzerland'),
  (2, 'country.liechtenstein'),
  (3, 'canton.ag'),
  (4, 'canton.ai'),
  (5, 'canton.ar'),
  (6, 'canton.be'),
  (7, 'canton.bl'),
  (8, 'canton.bs'),
  (9, 'canton.fr'),
  (10, 'canton.ge'),
  (11, 'canton.gl'),
  (12, 'canton.gr'),
  (13, 'canton.ju'),
  (14, 'canton.lu'),
  (15, 'canton.ne'),
  (16, 'canton.nw'),
  (17, 'canton.ow'),
  (18, 'canton.sg'),
  (19, 'canton.sh'),
  (20, 'canton.so'),
  (21, 'canton.sz'),
  (22, 'canton.tg'),
  (23, 'canton.ti'),
  (24, 'canton.ur'),
  (25, 'canton.vd'),
  (26, 'canton.vs'),
  (27, 'canton.zg'),
  (28, 'canton.zh');

SELECT SETVAL('"tag_tag_id_seq"', (SELECT MAX("tag_id") FROM "tag"));
