-- liquibase formatted sql
-- changeset mprunier:1.0.0-190-create_prune_tests_trigger.sql endDelimiter:go

CREATE TRIGGER prune_tests_trigger
    AFTER INSERT
    ON test
    FOR EACH ROW
    EXECUTE FUNCTION prune_tests();

go

-- rollback DROP TRIGGER prune_tests_trigger;
