---------------------------------------------------------------------------
-- User and Credential Tables                                            --
---------------------------------------------------------------------------

DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS "credential" CASCADE;

DROP CAST IF EXISTS (VARCHAR AS ROLE_ENUM);

DROP TYPE IF EXISTS ROLE_ENUM;

DROP EXTENSION IF EXISTS "pgcrypto";

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TYPE ROLE_ENUM AS ENUM ('ADMIN', 'MODERATOR', 'CONTRIBUTOR', 'VIEWER');

CREATE CAST (VARCHAR AS ROLE_ENUM) WITH INOUT AS IMPLICIT;

CREATE TABLE "user"
(
  "user_id" BIGSERIAL,
  "username" VARCHAR NOT NULL UNIQUE,
  "firstname" VARCHAR NOT NULL,
  "lastname" VARCHAR NOT NULL,
  "email" VARCHAR NOT NULL,
  "roles" ROLE_ENUM ARRAY NOT NULL CHECK (CARDINALITY("roles") > 0),
  "deleted" BOOLEAN DEFAULT FALSE,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("user_id")
);

CREATE TABLE "credential"
(
  "credential_id" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "password" VARCHAR,
  "issuer" VARCHAR,
  "subject" VARCHAR,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("credential_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id")
);


---------------------------------------------------------------------------
-- Scan Results                                                          --
---------------------------------------------------------------------------

DROP TABLE IF EXISTS "scan_job" CASCADE;
DROP TABLE IF EXISTS "website_result" CASCADE;
DROP TABLE IF EXISTS "webpage_result" CASCADE;
DROP TABLE IF EXISTS "rule" CASCADE;
DROP TABLE IF EXISTS "wcag_references" CASCADE;
DROP TABLE IF EXISTS "check_violating_element" CASCADE;
DROP TABLE IF EXISTS "check_incomplete_element" CASCADE;
DROP TABLE IF EXISTS "check" CASCADE;
DROP TABLE IF EXISTS "check_element" CASCADE;
DROP TABLE IF EXISTS "element" CASCADE;

DROP CAST IF EXISTS (VARCHAR AS CHECK_TYPE_ENUM);
DROP CAST IF EXISTS (VARCHAR AS IMPACT_ENUM);
DROP CAST IF EXISTS (VARCHAR AS SCAN_STATUS_ENUM);

DROP TYPE IF EXISTS CHECK_TYPE_ENUM;
DROP TYPE IF EXISTS IMPACT_ENUM;
DROP TYPE IF EXISTS SCAN_STATUS_ENUM;

DROP EXTENSION IF EXISTS "pgcrypto";

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TYPE CHECK_TYPE_ENUM AS ENUM ('ANY', 'ALL', 'NONE');
CREATE TYPE IMPACT_ENUM AS ENUM ('MINOR', 'MODERATE', 'SERIOUS', 'CRITICAL');
CREATE TYPE SCAN_STATUS_ENUM AS ENUM ('SUCCESS', 'FAILED');

CREATE CAST (VARCHAR AS CHECK_TYPE_ENUM) WITH INOUT AS IMPLICIT;
CREATE CAST (VARCHAR AS IMPACT_ENUM) WITH INOUT AS IMPLICIT;
CREATE CAST (VARCHAR AS SCAN_STATUS_ENUM) WITH INOUT AS IMPLICIT;

CREATE TABLE "scan_job"
(
  "scan_job_id" BIGSERIAL,
  "model_version" VARCHAR NOT NULL,
  "locale" VARCHAR NOT NULL,
  "website_base_url" VARCHAR NOT NULL,
  "webpage_paths" VARCHAR ARRAY NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("scan_job_id")
);

CREATE TABLE "website_result"
(
  "website_result_id" BIGSERIAL,
  "scan_job_fk" BIGINT,
  "model_version" VARCHAR NOT NULL,
  "website" VARCHAR NOT NULL,
  "scan_timestamp" TIMESTAMP(3) NOT NULL,
  "scan_status" SCAN_STATUS_ENUM,
  "error_message" VARCHAR DEFAULT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("website_result_id"),
  FOREIGN KEY ("scan_job_fk") REFERENCES "scan_job" ("scan_job_id") ON DELETE CASCADE
);

CREATE TABLE "webpage_result"
(
  "webpage_result_id" BIGSERIAL,
  "website_result_fk" BIGSERIAL NOT NULL,
  "path" VARCHAR NOT NULL,
  "scan_status" SCAN_STATUS_ENUM,
  "error_message" VARCHAR DEFAULT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("webpage_result_id"),
  FOREIGN KEY ("website_result_fk") REFERENCES "website_result" ("website_result_id") ON DELETE CASCADE
);

CREATE TABLE "rule"
(
  "rule_id" BIGSERIAL,
  "webpage_result_fk" BIGSERIAL NOT NULL,
  "code" VARCHAR NOT NULL,
  "description" VARCHAR NOT NULL,
  "axe_url" VARCHAR NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("rule_id"),
  FOREIGN KEY ("webpage_result_fk") REFERENCES "webpage_result" ("webpage_result_id") ON DELETE CASCADE
);

CREATE TABLE "wcag_references"
(
  "wcag_references_id" BIGSERIAL,
  "rule_fk" BIGSERIAL NOT NULL,
  "version" VARCHAR NOT NULL,
  "level" VARCHAR NOT NULL,
  "criteria" VARCHAR ARRAY NOT NULL,
  PRIMARY KEY ("wcag_references_id"),
  FOREIGN KEY ("rule_fk") REFERENCES "rule" ("rule_id") ON DELETE CASCADE
);

CREATE TABLE "check"
(
  "check_id" BIGSERIAL,
  "rule_fk" BIGSERIAL NOT NULL,
  "code" VARCHAR NOT NULL,
  "type" CHECK_TYPE_ENUM,
  "impact" IMPACT_ENUM,
  "tested_count" INTEGER,
  "passed_count" INTEGER,
  "violated_count" INTEGER,
  "incomplete_count" INTEGER,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("check_id"),
  FOREIGN KEY ("rule_fk") REFERENCES "rule" ("rule_id") ON DELETE CASCADE
);

CREATE TABLE "check_element"
(
  "check_element_id" BIGSERIAL,
  "target" VARCHAR NOT NULL,
  "html" VARCHAR NOT NULL,
  "issue_description" VARCHAR NOT NULL,
  "data" VARCHAR NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("check_element_id")
);

CREATE TABLE "check_violating_element"
(
  "check_violating_element_id" BIGSERIAL,
  "check_fk" BIGSERIAL NOT NULL,
  "check_element_fk" BIGSERIAL NOT NULL,
  PRIMARY KEY ("check_violating_element_id"),
  FOREIGN KEY ("check_fk") REFERENCES "check" ("check_id") ON DELETE CASCADE,
  FOREIGN KEY ("check_element_fk") REFERENCES "check_element" ("check_element_id") ON DELETE CASCADE
);

CREATE TABLE "check_incomplete_element"
(
  "check_incomplete_element_id" BIGSERIAL,
  "check_fk" BIGSERIAL NOT NULL,
  "check_element_fk" BIGSERIAL NOT NULL,
  PRIMARY KEY ("check_incomplete_element_id"),
  FOREIGN KEY ("check_fk") REFERENCES "check" ("check_id") ON DELETE CASCADE,
  FOREIGN KEY ("check_element_fk") REFERENCES "check_element" ("check_element_id") ON DELETE CASCADE
);

CREATE TABLE "element"
(
  "element_id" BIGSERIAL,
  "check_element_fk" BIGSERIAL NOT NULL,
  "target" VARCHAR NOT NULL,
  "html" VARCHAR NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("element_id"),
  FOREIGN KEY ("check_element_fk") REFERENCES "check_element" ("check_element_id") ON DELETE CASCADE
);


---------------------------------------------------------------------------
-- Structure                                                             --
---------------------------------------------------------------------------

DROP TABLE IF EXISTS "website" CASCADE;
DROP TABLE IF EXISTS "website_scan" CASCADE;
DROP TABLE IF EXISTS "website_statistic" CASCADE;
DROP TABLE IF EXISTS "webpage" CASCADE;
DROP TABLE IF EXISTS "webpage_scan" CASCADE;
DROP TABLE IF EXISTS "webpage_statistic" CASCADE;
DROP TABLE IF EXISTS "tag" CASCADE;
DROP TABLE IF EXISTS "website_tag" CASCADE;
DROP TABLE IF EXISTS "report" CASCADE;
DROP TABLE IF EXISTS "report_message" CASCADE;
DROP TABLE IF EXISTS "user_report" CASCADE;
DROP TABLE IF EXISTS "website_report" CASCADE;
DROP TABLE IF EXISTS "webpage_report" CASCADE;

DROP CAST IF EXISTS (VARCHAR AS CATEGORY_ENUM);
DROP CAST IF EXISTS (VARCHAR AS STATUS_ENUM);
DROP CAST IF EXISTS (VARCHAR AS REASON_ENUM);

DROP TYPE IF EXISTS CATEGORY_ENUM;
DROP TYPE IF EXISTS STATUS_ENUM;
DROP TYPE IF EXISTS REASON_ENUM;

CREATE TYPE CATEGORY_ENUM AS ENUM ('GOVERNMENT_FEDERAL', 'GOVERNMENT_CANTONAL', 'GOVERNMENT_MUNICIPAL', 'PRIVATE_AFFILIATED', 'PRIVATE_UNIVERSITY', 'PRIVATE_NEWS', 'PRIVATE_SHOP', 'PRIVATE_OTHER');
CREATE TYPE STATUS_ENUM AS ENUM ('BLOCKED', 'READY', 'PENDING_INITIAL', 'PENDING_RESCAN');
CREATE TYPE REASON_ENUM AS ENUM ('INCORRECT', 'MISLEADING', 'INAPPROPRIATE');

CREATE CAST (VARCHAR AS CATEGORY_ENUM) WITH INOUT AS IMPLICIT;
CREATE CAST (VARCHAR AS STATUS_ENUM) WITH INOUT AS IMPLICIT;
CREATE CAST (VARCHAR AS REASON_ENUM) WITH INOUT AS IMPLICIT;


-- Website

CREATE TABLE "website"
(
  "website_id" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "domain" VARCHAR NOT NULL,
  "url" VARCHAR NOT NULL,
  "category" CATEGORY_ENUM NOT NULL,
  "status" STATUS_ENUM NOT NULL DEFAULT 'PENDING_INITIAL',
  "webpage_count" INTEGER NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("website_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id")
);

CREATE TABLE "website_statistic"
(
  "website_statistic_id" BIGSERIAL,
  "website_fk" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "score" DOUBLE PRECISION NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("website_statistic_id"),
  FOREIGN KEY ("website_fk") REFERENCES "website" ("website_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id")
);

CREATE TABLE "website_scan"
(
  "website_scan_id" BIGSERIAL,
  "website_fk" BIGSERIAL,
  "website_statistic_fk" BIGSERIAL,
  "website_result_fk" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("website_scan_id"),
  FOREIGN KEY ("website_fk") REFERENCES "website" ("website_id"),
  FOREIGN KEY ("website_statistic_fk") REFERENCES "website_statistic" ("website_statistic_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id"),
  FOREIGN KEY ("website_result_fk") REFERENCES "website_result" ("website_result_id")
);

----------------------------------------------------------------------------------------------------
-- Webpage
----------------------------------------------------------------------------------------------------

CREATE TABLE "webpage"
(
  "webpage_id" BIGSERIAL,
  "website_fk" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "domain" VARCHAR NOT NULL,
  "url" VARCHAR NOT NULL,
  "category" CATEGORY_ENUM NOT NULL,
  "status" STATUS_ENUM NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("webpage_id"),
  FOREIGN KEY ("website_fk") REFERENCES "website" ("website_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id")
);

CREATE TABLE "webpage_statistic"
(
  "webpage_statistic_id" BIGSERIAL,
  "website_statistic_fk" BIGSERIAL,
  "webpage_fk" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "score" DOUBLE PRECISION,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("webpage_statistic_id"),
  FOREIGN KEY ("webpage_fk") REFERENCES "webpage" ("webpage_id"),
  FOREIGN KEY ("website_statistic_fk") REFERENCES "website_statistic" ("website_statistic_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id")
);

CREATE TABLE "webpage_scan"
(
  "webpage_scan_id" BIGSERIAL,
  "website_scan_fk" BIGSERIAL,
  "webpage_fk" BIGSERIAL,
  "webpage_statistic_fk" BIGSERIAL,
  "webpage_result_fk" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("webpage_scan_id"),
  FOREIGN KEY ("website_scan_fk") REFERENCES "website_scan" ("website_scan_id"),
  FOREIGN KEY ("webpage_fk") REFERENCES "webpage" ("webpage_id"),
  FOREIGN KEY ("webpage_statistic_fk") REFERENCES "webpage_statistic" ("webpage_statistic_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id"),
  FOREIGN KEY ("webpage_result_fk") REFERENCES "webpage_result" ("webpage_result_id")
);

----------------------------------------------------------------------------------------------------
-- Tag
----------------------------------------------------------------------------------------------------

CREATE TABLE "tag"
(
  "tag_id" BIGSERIAL,
  "name" VARCHAR NOT NULL UNIQUE,
  PRIMARY KEY ("tag_id")
);

CREATE TABLE "website_tag"
(
  "website_tag_id" BIGSERIAL,
  "website_fk" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "tag_fk" BIGSERIAL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("website_tag_id"),
  FOREIGN KEY ("website_fk") REFERENCES "website" ("website_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id"),
  FOREIGN KEY ("tag_fk") REFERENCES "tag" ("tag_id")
);

----------------------------------------------------------------------------------------------------
-- Report
----------------------------------------------------------------------------------------------------

CREATE TABLE "report"
(
  "report_id" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "reason" REASON_ENUM NOT NULL,
  "status" STATUS_ENUM NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("report_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id")
);

CREATE TABLE "report_message"
(
  "report_message_id" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "report_fk" BIGSERIAL,
  "message" TEXT NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("report_message_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id"),
  FOREIGN KEY ("report_fk") REFERENCES "report" ("report_id")
);

CREATE TABLE "user_report"
(
  "user_report_id" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "report_fk" BIGSERIAL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("user_report_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id"),
  FOREIGN KEY ("report_fk") REFERENCES "report" ("report_id")
);

CREATE TABLE "website_report"
(
  "website_report_id" BIGSERIAL,
  "website_fk" BIGSERIAL,
  "report_fk" BIGSERIAL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("website_report_id"),
  FOREIGN KEY ("website_fk") REFERENCES "website" ("website_id"),
  FOREIGN KEY ("report_fk") REFERENCES "report" ("report_id")
);

CREATE TABLE "webpage_report"
(
  "webpage_report_id" BIGSERIAL,
  "webpage_fk" BIGSERIAL,
  "report_fk" BIGSERIAL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT NOW(),
  PRIMARY KEY ("webpage_report_id"),
  FOREIGN KEY ("webpage_fk") REFERENCES "webpage" ("webpage_id"),
  FOREIGN KEY ("report_fk") REFERENCES "report" ("report_id")
);
