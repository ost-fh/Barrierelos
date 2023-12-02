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

CREATE TYPE CATEGORY_ENUM AS ENUM('GOVERNMENT_FEDERAL', 'GOVERNMENT_CANTONAL', 'GOVERNMENT_MUNICIPAL', 'PRIVATE_AFFILIATED', 'PRIVATE_UNIVERSITY', 'PRIVATE_NEWS', 'PRIVATE_SHOP', 'PRIVATE_OTHER');
CREATE TYPE STATUS_ENUM AS ENUM('BLOCKED', 'READY', 'PENDING_INITIAL', 'PENDING_RESCAN');
CREATE TYPE REASON_ENUM AS ENUM('INCORRECT', 'MISLEADING', 'INAPPROPRIATE');

CREATE CAST (VARCHAR AS CATEGORY_ENUM) WITH INOUT AS IMPLICIT;
CREATE CAST (VARCHAR AS STATUS_ENUM) WITH INOUT AS IMPLICIT;
CREATE CAST (VARCHAR AS REASON_ENUM) WITH INOUT AS IMPLICIT;

----------------------------------------------------------------------------------------------------
-- Website
----------------------------------------------------------------------------------------------------

CREATE TABLE "website"
(
  "website_id" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "domain" VARCHAR NOT NULL,
  "url" VARCHAR NOT NULL,
  "category" CATEGORY_ENUM NOT NULL,
  "status" STATUS_ENUM NOT NULL DEFAULT 'PENDING_INITIAL',
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("website_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id")
);

CREATE TABLE "website_scan"
(
  "website_scan_id" BIGSERIAL,
  "website_fk" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "analysis_result_fk" BIGSERIAL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("website_scan_id"),
  FOREIGN KEY ("website_fk") REFERENCES "website" ("website_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id"),
  FOREIGN KEY ("analysis_result_fk") REFERENCES "analysis_result" ("analysis_result_id")
);

CREATE TABLE "website_statistic"
(
  "website_statistic_id" BIGSERIAL,
  "website_fk" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "score" DOUBLE PRECISION NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  FOREIGN KEY ("website_fk") REFERENCES "website" ("website_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id")
);

----------------------------------------------------------------------------------------------------
-- Webpage
----------------------------------------------------------------------------------------------------

CREATE TABLE "webpage"
(
  "webpage_id" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "domain" VARCHAR NOT NULL,
  "url" VARCHAR NOT NULL,
  "category" CATEGORY_ENUM NOT NULL,
  "status" STATUS_ENUM NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("webpage_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id")
);

CREATE TABLE "webpage_scan"
(
  "webpage_scan_id" BIGSERIAL,
  "webpage_fk" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "webpage_result_fk" BIGSERIAL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("webpage_scan_id"),
  FOREIGN KEY ("webpage_fk") REFERENCES "webpage" ("webpage_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id"),
  FOREIGN KEY ("webpage_result_fk") REFERENCES "webpage_result" ("webpage_result_id")
);

CREATE TABLE "webpage_statistic"
(
  "webpage_statistic_id" BIGSERIAL,
  "webpage_fk" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "score" DOUBLE PRECISION,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  FOREIGN KEY ("webpage_fk") REFERENCES "webpage" ("webpage_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id")
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
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("report_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id")
);

CREATE TABLE "report_message"
(
  "report_message_id" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "report_fk" BIGSERIAL,
  "message" TEXT NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("report_message_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id"),
  FOREIGN KEY ("report_fk") REFERENCES "report" ("report_id")
);

CREATE TABLE "user_report"
(
  "user_report_id" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "report_fk" BIGSERIAL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("user_report_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id"),
  FOREIGN KEY ("report_fk") REFERENCES "report" ("report_id")
);

CREATE TABLE "website_report"
(
  "website_report_id" BIGSERIAL,
  "website_fk" BIGSERIAL,
  "report_fk" BIGSERIAL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("website_report_id"),
  FOREIGN KEY ("website_fk") REFERENCES "website" ("website_id"),
  FOREIGN KEY ("report_fk") REFERENCES "report" ("report_id")
);

CREATE TABLE "webpage_report"
(
  "webpage_report_id" BIGSERIAL,
  "webpage_fk" BIGSERIAL,
  "report_fk" BIGSERIAL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("webpage_report_id"),
  FOREIGN KEY ("webpage_fk") REFERENCES "webpage" ("webpage_id"),
  FOREIGN KEY ("report_fk") REFERENCES "report" ("report_id")
);
