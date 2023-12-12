DROP TABLE IF EXISTS "website" CASCADE;
DROP TABLE IF EXISTS "webpage" CASCADE;
DROP TABLE IF EXISTS "webpage_statistic" CASCADE;
DROP TABLE IF EXISTS "tag" CASCADE;
DROP TABLE IF EXISTS "website_tag" CASCADE;

DROP CAST IF EXISTS (VARCHAR AS CATEGORY_ENUM);
DROP CAST IF EXISTS (VARCHAR AS STATUS_ENUM);

DROP TYPE IF EXISTS CATEGORY_ENUM;
DROP TYPE IF EXISTS STATUS_ENUM;

CREATE TYPE CATEGORY_ENUM AS ENUM('GOVERNMENT_FEDERAL', 'GOVERNMENT_CANTONAL', 'GOVERNMENT_MUNICIPAL', 'PRIVATE_AFFILIATED', 'PRIVATE_UNIVERSITY', 'PRIVATE_NEWS', 'PRIVATE_SHOP', 'PRIVATE_OTHER');
CREATE TYPE STATUS_ENUM AS ENUM('BLOCKED', 'READY', 'PENDING_INITIAL', 'PENDING_RESCAN');

CREATE CAST (VARCHAR AS CATEGORY_ENUM) WITH INOUT AS IMPLICIT;
CREATE CAST (VARCHAR AS STATUS_ENUM) WITH INOUT AS IMPLICIT;

----------------------------------------------------------------------------------------------------
-- Website
----------------------------------------------------------------------------------------------------

CREATE TABLE "website"
(
  "website_id" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "domain" VARCHAR NOT NULL UNIQUE,
  "url" VARCHAR NOT NULL UNIQUE,
  "category" CATEGORY_ENUM NOT NULL,
  "status" STATUS_ENUM NOT NULL DEFAULT 'PENDING_INITIAL',
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("website_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id")
);

CREATE TABLE "website_statistic"
(
  "website_statistic_id" BIGSERIAL,
  "score" DOUBLE PRECISION NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now()
);

----------------------------------------------------------------------------------------------------
-- Webpage
----------------------------------------------------------------------------------------------------

CREATE TABLE "webpage"
(
  "webpage_id" BIGSERIAL,
  "website_fk" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "path" VARCHAR NOT NULL,
  "url" VARCHAR NOT NULL UNIQUE,
  "status" STATUS_ENUM NOT NULL,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("webpage_id"),
  FOREIGN KEY ("website_fk") REFERENCES "website" ("website_id"),
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
