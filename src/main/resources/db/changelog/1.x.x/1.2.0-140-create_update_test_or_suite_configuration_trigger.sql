-- liquibase formatted sql
-- changeset mprunier:1.2.0-140-create_update_test_or_suite_configuration_trigger.sql endDelimiter:go

CREATE TRIGGER update_test_or_suite_configuration_trigger
    AFTER INSERT
    ON test_result
    FOR EACH ROW
    EXECUTE FUNCTION update_test_or_suite_configuration();

go

-- rollback DROP TRIGGER IF EXISTS update_test_or_suite_configuration_trigger ON test_result;
