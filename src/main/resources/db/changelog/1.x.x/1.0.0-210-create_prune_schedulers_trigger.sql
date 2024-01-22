-- liquibase formatted sql
-- changeset mprunier:1.0.0-210-create_prune_schedulers_trigger.sql endDelimiter:go

CREATE TRIGGER prune_schedulers_trigger
    AFTER INSERT ON scheduler
    FOR EACH ROW
EXECUTE FUNCTION prune_schedulers();

go

-- rollback DROP TRIGGER prune_schedulers_trigger;
