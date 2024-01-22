-- liquibase formatted sql
-- changeset mprunier:1.0.0-270-add_index.sql

CREATE INDEX idx_configuration_suite_id ON configuration_test (configuration_suite_id);
CREATE INDEX idx_configuration_test_id ON test (configuration_test_id);


-- rollback DROP INDEX idx_configuration_suite_id;
-- rollback DROP INDEX idx_configuration_test_id;