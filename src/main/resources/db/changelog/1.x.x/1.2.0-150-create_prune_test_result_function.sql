-- liquibase formatted sql
-- changeset mprunier:1.2.0-150-create_prune_test_result_function.sql endDelimiter:go

CREATE OR REPLACE FUNCTION prune_test_result()
    RETURNS TRIGGER AS
$$
BEGIN
    DELETE
    FROM test_result
    WHERE id IN (SELECT id
                 FROM (SELECT id, ROW_NUMBER() OVER (PARTITION BY configuration_test_id ORDER BY created_at DESC) AS rn
                       FROM test_result) t
                 WHERE rn > 20);

    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

go

-- rollback DROP FUNCTION prune_test_result;
