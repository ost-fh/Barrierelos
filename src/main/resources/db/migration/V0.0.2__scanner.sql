DROP TABLE IF EXISTS "analysis_job" CASCADE;
DROP TABLE IF EXISTS "analysis_result" CASCADE;
DROP TABLE IF EXISTS "webpage_result" CASCADE;
DROP TABLE IF EXISTS "rule" CASCADE;
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

DROP EXTENSION IF EXISTS pgcrypto;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TYPE CHECK_TYPE_ENUM AS ENUM('ANY', 'ALL', 'NONE');
CREATE TYPE IMPACT_ENUM AS ENUM('MINOR', 'MODERATE', 'SERIOUS', 'CRITICAL');
CREATE TYPE SCAN_STATUS_ENUM AS ENUM('SUCCESS', 'FAILED');

CREATE CAST (VARCHAR AS CHECK_TYPE_ENUM) WITH INOUT AS IMPLICIT;
CREATE CAST (VARCHAR AS IMPACT_ENUM) WITH INOUT AS IMPLICIT;
CREATE CAST (VARCHAR AS SCAN_STATUS_ENUM) WITH INOUT AS IMPLICIT;

CREATE TABLE "analysis_job"
(
  "analysis_job_id" BIGSERIAL,
  "model_version" VARCHAR NOT NULL,
  "website_base_url" VARCHAR NOT NULL,
  "webpage_paths" VARCHAR ARRAY NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("analysis_job_id")
);

CREATE TABLE "analysis_result"
(
  "analysis_result_id" BIGSERIAL,
  "model_version" VARCHAR NOT NULL,
  "website" VARCHAR NOT NULL,
  "scan_timestamp" TIMESTAMP(3) NOT NULL,
  "scan_status" SCAN_STATUS_ENUM,
  "error_message" VARCHAR DEFAULT null,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("analysis_result_id")
);

CREATE TABLE "webpage_result"
(
  "webpage_result_id" BIGSERIAL,
  "analysis_result_fk" BIGSERIAL NOT NULL,
  "path" VARCHAR NOT NULL,
  "scan_status" SCAN_STATUS_ENUM,
  "error_message" VARCHAR DEFAULT null,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("webpage_result_id"),
  FOREIGN KEY ("analysis_result_fk") REFERENCES "analysis_result" ("analysis_result_id")
);

CREATE TABLE "rule"
(
  "rule_id" BIGSERIAL,
  "webpage_result_fk" BIGSERIAL NOT NULL,
  "code" VARCHAR NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("rule_id"),
  FOREIGN KEY ("webpage_result_fk") REFERENCES "webpage_result" ("webpage_result_id")
);

CREATE TABLE "check"
(
  "check_id" BIGSERIAL,
  "rule_fk" BIGSERIAL NOT NULL,
  "code" VARCHAR NOT NULL,
  "type" CHECK_TYPE_ENUM,
  "impact" IMPACT_ENUM,
  "tested_count" Int,
  "passed_count" Int,
  "violated_count" Int,
  "incomplete_count" Int,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("check_id"),
  FOREIGN KEY ("rule_fk") REFERENCES "rule" ("rule_id")
);

CREATE TABLE "check_element"
(
  "check_element_id" BIGSERIAL,
  "target" VARCHAR NOT NULL,
  "html" VARCHAR NOT NULL,
  "issue_description" VARCHAR NOT NULL,
  "data" VARCHAR NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("check_element_id")
);

CREATE TABLE "check_violating_element"
(
  "check_violating_element_id" BIGSERIAL,
  "check_fk" BIGSERIAL NOT NULL,
  "check_element_fk" BIGSERIAL NOT NULL,
  PRIMARY KEY ("check_violating_element_id"),
  FOREIGN KEY ("check_fk") REFERENCES "check" ("check_id"),
  FOREIGN KEY ("check_element_fk") REFERENCES "check_element" ("check_element_id")
);

CREATE TABLE "check_incomplete_element"
(
  "check_incomplete_element_id" BIGSERIAL,
  "check_fk" BIGSERIAL NOT NULL,
  "check_element_fk" BIGSERIAL NOT NULL,
  PRIMARY KEY ("check_incomplete_element_id"),
  FOREIGN KEY ("check_fk") REFERENCES "check" ("check_id"),
  FOREIGN KEY ("check_element_fk") REFERENCES "check_element" ("check_element_id")
);

CREATE TABLE "element"
(
  "element_id" BIGSERIAL,
  "check_element_fk" BIGSERIAL NOT NULL,
  "target" VARCHAR NOT NULL,
  "html" VARCHAR NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("element_id"),
  FOREIGN KEY ("check_element_fk") REFERENCES "check_element" ("check_element_id")
);
