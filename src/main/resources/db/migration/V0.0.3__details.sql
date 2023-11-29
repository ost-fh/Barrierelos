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


ALTER TABLE "analysis_result"
    ADD COLUMN IF NOT EXISTS "analysis_job_fk" BIGINT NULL REFERENCES "analysis_job" ON DELETE SET NULL;

ALTER TABLE "rule"
    ADD COLUMN IF NOT EXISTS description VARCHAR NOT NULL DEFAULT '';

ALTER TABLE "webpage_result"
    DROP CONSTRAINT "webpage_result_analysis_result_fk_fkey",
    ADD CONSTRAINT "webpage_result_analysis_result_fk_fkey"
        FOREIGN KEY ("analysis_result_fk")
            REFERENCES "analysis_result" ("analysis_result_id")
            ON DELETE CASCADE;

ALTER TABLE "rule"
    DROP CONSTRAINT "rule_webpage_result_fk_fkey",
    ADD CONSTRAINT "rule_webpage_result_fk_fkey"
        FOREIGN KEY ("webpage_result_fk")
            REFERENCES "webpage_result" ("webpage_result_id")
            ON DELETE CASCADE;

ALTER TABLE "check"
    DROP CONSTRAINT "check_rule_fk_fkey",
    ADD CONSTRAINT "check_rule_fk_fkey"
        FOREIGN KEY ("rule_fk")
            REFERENCES "rule" ("rule_id")
            ON DELETE CASCADE;

ALTER TABLE "check_violating_element"
    DROP CONSTRAINT "check_violating_element_check_fk_fkey",
    ADD CONSTRAINT "check_violating_element_check_fk_fkey"
        FOREIGN KEY ("check_fk")
            REFERENCES "check" ("check_id")
            ON DELETE CASCADE,
    DROP CONSTRAINT "check_violating_element_check_element_fk_fkey",
    ADD CONSTRAINT "check_violating_element_check_element_fk_fkey"
        FOREIGN KEY ("check_element_fk")
            REFERENCES "check_element" ("check_element_id")
            ON DELETE CASCADE;

ALTER TABLE "check_incomplete_element"
    DROP CONSTRAINT "check_incomplete_element_check_fk_fkey",
    ADD CONSTRAINT "check_incomplete_element_check_fk_fkey"
        FOREIGN KEY ("check_fk")
            REFERENCES "check" ("check_id")
            ON DELETE CASCADE,
    DROP CONSTRAINT "check_incomplete_element_check_element_fk_fkey",
    ADD CONSTRAINT "check_incomplete_element_check_element_fk_fkey"
        FOREIGN KEY ("check_element_fk")
            REFERENCES "check_element" ("check_element_id")
            ON DELETE CASCADE;

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
