-- liquibase formatted sql
-- changeset mprunier:1.0.0-340-create_metrics_table.sql

CREATE TABLE metrics
(
    id             BIGSERIAL PRIMARY KEY,
    environment_id BIGINT                   NOT NULL,
    suites         INTEGER,
    tests          INTEGER,
    passes         INTEGER,
    failures       INTEGER,
    skipped        INTEGER,
    pass_percent   INTEGER,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk__scheduler__environment_id FOREIGN KEY (environment_id) REFERENCES environment (id) ON DELETE CASCADE
);

INSERT INTO metrics (environment_id, suites, tests, passes, failures, skipped, pass_percent, created_at)
SELECT environment_id,
       suites,
       tests,
       passes,
       failures,
       skipped,
       pass_percent,
       created_at
FROM schedulerConfiguration;

-- rollback DROP TABLE IF EXISTS metrics;
