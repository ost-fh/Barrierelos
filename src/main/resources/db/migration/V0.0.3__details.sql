DROP TABLE IF EXISTS "website_details" CASCADE;
DROP TABLE IF EXISTS "webpage_details" CASCADE;

CREATE TABLE "website_details"
(
    "website_details_id" BIGSERIAL,
    "score" DOUBLE PRECISION NOT NULL,
    "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
    PRIMARY KEY ("website_details_id")
);

CREATE TABLE "webpage_details"
(
    "webpage_details_id" BIGSERIAL,
    "website_details_fk" BIGSERIAL NOT NULL,
    path VARCHAR NOT NULL,
    "score" DOUBLE PRECISION NOT NULL,
    "modified" TIMESTAMP(3) NOT NULL DEFAULT now(),
    PRIMARY KEY ("webpage_details_id")
);

INSERT INTO "website_details"
    ("website_details_id", "score")
VALUES
    (1, 0)
;

INSERT INTO "webpage_details"
("webpage_details_id", "website_details_fk", "path", "score")
VALUES
    (1, 1, '/', 30),
    (2, 1, '/test', 50),
    (3, 1, '/about', 80)
;
