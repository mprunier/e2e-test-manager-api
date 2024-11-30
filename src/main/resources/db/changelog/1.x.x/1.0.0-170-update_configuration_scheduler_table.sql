-- liquibase formatted sql
-- changeset mprunier:1.0.0-170-update_configuration_scheduler_table.sql
ALTER TABLE configuration_scheduler
    DROP COLUMN hour,
    DROP COLUMN minute,
    ADD COLUMN scheduled_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT TO_TIMESTAMP(0),
    ADD COLUMN days_of_week   VARCHAR(255);

-- rollback ALTER TABLE configuration_scheduler
-- rollback     ADD COLUMN hour   INTEGER NOT NULL DEFAULT 23,
-- rollback     ADD COLUMN minute INTEGER NOT NULL DEFAULT 30,
-- rollback     DROP COLUMN scheduled_time,
-- rollback     DROP COLUMN list;