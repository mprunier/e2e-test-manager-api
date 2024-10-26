-- liquibase formatted sql
-- changeset mprunier:1.1.0-100-update_test_screenshot_table.sql

ALTER TABLE test_screenshot
    ALTER COLUMN test_id DROP NOT NULL;

ALTER TABLE test_screenshot
    ADD COLUMN temporary_test_id BIGINT,
    ADD CONSTRAINT fk__test_screenshot__temporary_test_id FOREIGN KEY (temporary_test_id) REFERENCES temporary_test (id)

-- rollback ALTER TABLE test_screenshot DROP COLUMN temporary_test_id;
