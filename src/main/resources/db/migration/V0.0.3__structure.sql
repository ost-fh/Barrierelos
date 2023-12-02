DROP TABLE IF EXISTS "tag" CASCADE;

----------------------------------------------------------------------------------------------------
-- Tag
----------------------------------------------------------------------------------------------------

CREATE TABLE "tag"
(
  "tag_id" BIGSERIAL,
  "name" VARCHAR NOT NULL UNIQUE,
  PRIMARY KEY ("tag_id")
);
