-- liquibase formatted sql
-- changeset mprunier:1.0.0-160-create_prune_test_result_trigger.sql endDelimiter:go

CREATE TRIGGER prune_test_result_trigger
    AFTER INSERT
    ON test_result
    FOR EACH ROW
EXECUTE FUNCTION prune_test_result();

go

-- rollback DROP TRIGGER prune_test_result_trigger ON test_result;
