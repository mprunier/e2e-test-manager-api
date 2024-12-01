-- liquibase formatted sql
-- changeset mprunier:1.2.0-090-create_test_result_screenshot_table.sql

CREATE TABLE test_result_screenshot
(
    id             UUID PRIMARY KEY,
    test_result_id UUID         NOT NULL,
    filename       VARCHAR(255) NOT NULL,
    screenshot     BYTEA        NOT NULL,
    CONSTRAINT fk_test_result_screenshot_test_result FOREIGN KEY (test_result_id) REFERENCES test_result (id) ON DELETE CASCADE
);

CREATE INDEX idx_test_result_screenshot_test_result ON test_result_screenshot (test_result_id);

--rollback DROP TABLE test_result_screenshot;