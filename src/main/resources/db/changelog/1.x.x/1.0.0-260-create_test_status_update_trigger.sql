-- liquibase formatted sql
-- changeset mprunier:1.0.0-260-create_test_status_update_trigger.sql endDelimiter:go

DROP TRIGGER IF EXISTS test_status_create_trigger ON test;

CREATE TRIGGER test_status_update_trigger
    AFTER INSERT OR UPDATE
    ON test
    FOR EACH ROW
EXECUTE FUNCTION update_config_test_status_and_suite();

go

-- rollback DROP TRIGGER test_status_create_trigger;
