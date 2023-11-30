DROP TABLE IF EXISTS "user" CASCADE;
DROP TABLE IF EXISTS "credential" CASCADE;

DROP CAST IF EXISTS (VARCHAR AS ROLE_ENUM);

DROP TYPE IF EXISTS ROLE_ENUM;

DROP EXTENSION IF EXISTS pgcrypto;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TYPE ROLE_ENUM AS ENUM('ADMIN', 'MODERATOR', 'CONTRIBUTOR', 'VIEWER');

CREATE CAST (VARCHAR AS ROLE_ENUM) WITH INOUT AS IMPLICIT;

CREATE TABLE "user"
(
  "user_id" BIGSERIAL,
  "username" VARCHAR NOT NULL UNIQUE,
  "firstname" VARCHAR NOT NULL,
  "lastname" VARCHAR NOT NULL,
  "email" VARCHAR NOT NULL,
  "roles" ROLE_ENUM ARRAY NOT NULL CHECK (cardinality("roles") > 0),
  "deleted" BOOLEAN DEFAULT FALSE,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("user_id")
);

CREATE TABLE "credential"
(
  "credential_id" BIGSERIAL,
  "user_fk" BIGSERIAL,
  "password" VARCHAR,
  "issuer" VARCHAR,
  "subject" VARCHAR,
  "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
  "created" TIMESTAMP(3) NOT NULL DEFAULT now(),
  PRIMARY KEY ("credential_id"),
  FOREIGN KEY ("user_fk") REFERENCES "user" ("user_id")
);
