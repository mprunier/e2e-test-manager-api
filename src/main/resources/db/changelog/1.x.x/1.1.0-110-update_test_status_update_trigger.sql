-- liquibase formatted sql
-- changeset mprunier:1.1.0-110-update_test_status_update_trigger.sql

DROP TRIGGER IF EXISTS test_status_create_trigger ON test;
DROP TRIGGER IF EXISTS test_status_update_trigger ON test;

CREATE TRIGGER test_update_trigger
    AFTER UPDATE OF is_waiting
    ON test
    FOR EACH ROW
    WHEN (OLD.is_waiting = true AND NEW.is_waiting = false)
EXECUTE FUNCTION update_config_test_status_and_suite();

go

-- rollback DROP TRIGGER test_update_trigger;
