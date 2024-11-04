-- liquibase formatted sql
-- changeset mprunier:1.1.0-050-update_pipeline_table.sql

ALTER TABLE pipeline
    RENAME COLUMN test_ids TO configuration_test_ids_filter;
ALTER TABLE pipeline
    ADD COLUMN variables  VARCHAR(1000),
    ADD COLUMN created_by VARCHAR(500) NOT NULL DEFAULT 'System';

-- rollback ALTER TABLE executor
-- rollback RENAME COLUMN configuration_test_ids_filter TO test_ids;
-- rollback ALTER TABLE executor
-- rollback DROP COLUMN variables TO test_ids,
-- rollback DROP COLUMN created_by TO test_ids;
