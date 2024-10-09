-- liquibase formatted sql
-- changeset mprunier:1.1.0-100-update_prune_tests_function.sql endDelimiter:go

CREATE OR REPLACE FUNCTION prune_tests()
    RETURNS TRIGGER AS
$$
BEGIN
    DELETE
    FROM test
    WHERE id IN (SELECT id
                 FROM (SELECT id, ROW_NUMBER() OVER (PARTITION BY configuration_test_id ORDER BY created_at DESC) AS rn
                       FROM test) t
                 WHERE rn > 20);

    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

go

-- rollback DROP FUNCTION prune_tests;
