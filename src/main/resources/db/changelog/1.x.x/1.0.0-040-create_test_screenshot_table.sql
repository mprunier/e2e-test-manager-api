-- liquibase formatted sql
-- changeset mprunier:1.0.0-040-create_test_screenshot_table.sql

CREATE TABLE test_screenshot
(
    id BIGSERIAL PRIMARY KEY,
    test_id  BIGINT        NOT NULL,
    filename VARCHAR(1000) NOT NULL,
    screenshot BYTEA NOT NULL,
    CONSTRAINT fk__test_screenshot__test_id FOREIGN KEY (test_id) REFERENCES test (id) ON DELETE CASCADE
);

-- rollback DROP TABLE IF EXISTS test_screenshot;
