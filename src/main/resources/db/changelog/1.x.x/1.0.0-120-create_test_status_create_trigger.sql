-- liquibase formatted sql
-- changeset mprunier:1.0.0-120-create_test_status_create_trigger.sql endDelimiter:go

CREATE TRIGGER test_status_create_trigger
    AFTER INSERT ON test
    FOR EACH ROW
EXECUTE FUNCTION update_config_test_status_and_suite();

go

-- rollback DROP TRIGGER test_status_create_trigger;
