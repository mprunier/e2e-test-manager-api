-- liquibase formatted sql
-- changeset mprunier:1.0.0-200-create_prune_schedulers_function.sql endDelimiter:go

CREATE OR REPLACE FUNCTION prune_schedulers()
    RETURNS TRIGGER AS
$$
BEGIN
    DELETE
    FROM schedulerConfiguration
    WHERE id IN (SELECT id
                 FROM (SELECT id, ROW_NUMBER() OVER (PARTITION BY environment_id ORDER BY created_at DESC) AS rn
                       FROM schedulerConfiguration) t
                 WHERE rn > 10);

    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

go

-- rollback DROP FUNCTION prune_schedulers;
