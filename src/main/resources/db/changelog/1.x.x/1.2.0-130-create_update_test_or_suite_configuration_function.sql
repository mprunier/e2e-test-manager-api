-- liquibase formatted sql
-- changeset mprunier:1.2.0-130-create_update_test_or_suite_configuration_function.sql endDelimiter:go

CREATE OR REPLACE FUNCTION update_test_or_suite_configuration() RETURNS TRIGGER AS
$$
DECLARE
    last_test_status TEXT;
    config_suite_id  UUID;
    succeeded_count  INT;
    failed_count     INT;
    new_count        INT;
    skipped_count    INT;
    canceled_count   INT;
BEGIN
    SELECT status
    INTO last_test_status
    FROM test_result
    WHERE configuration_test_id = NEW.configuration_test_id
    ORDER BY created_at DESC
    LIMIT 1;

    UPDATE test_configuration
    SET status         = CASE
                             WHEN last_test_status IN ('FAILED', 'SYSTEM_ERROR', 'NO_CORRESPONDING_TEST', 'NO_REPORT_ERROR', 'UNKNOWN') THEN 'FAILED'
                             ELSE last_test_status
        END,
        last_played_at = NOW()
    WHERE id = NEW.configuration_test_id;

    SELECT suite_id
    INTO config_suite_id
    FROM test_configuration
    WHERE id = NEW.configuration_test_id;

    WITH test_status_counts AS (SELECT SUM((status = 'SUCCESS')::INT)                                                                          AS succeeded,
                                       SUM((status IN ('FAILED', 'SYSTEM_ERROR', 'NO_CORRESPONDING_TEST', 'NO_REPORT_ERROR', 'UNKNOWN'))::INT) AS failed,
                                       SUM((status = 'NEW')::INT)                                                                              AS newer,
                                       SUM((status = 'SKIPPED')::INT)                                                                          AS skipped,
                                       SUM((status = 'CANCELED')::INT)                                                                         AS canceled
                                FROM test_result tr
                                WHERE tr.configuration_test_id IN (SELECT id
                                                                   FROM test_configuration
                                                                   WHERE suite_id = config_suite_id)
                                  AND tr.created_at = (SELECT MAX(created_at)
                                                       FROM test_result tr2
                                                       WHERE tr2.configuration_test_id = tr.configuration_test_id))
    SELECT succeeded, failed, newer, skipped, canceled
    INTO succeeded_count, failed_count, new_count, skipped_count, canceled_count
    FROM test_status_counts;

    UPDATE suite_configuration
    SET status         = CASE
                             WHEN failed_count > 0 THEN 'FAILED'
                             WHEN canceled_count > 0 THEN 'CANCELED'
                             WHEN new_count > 0 THEN 'NEW'
                             WHEN succeeded_count > 0 AND skipped_count > 0 THEN 'PARTIAL_SKIPPED'
                             WHEN succeeded_count = 0 AND failed_count = 0 AND skipped_count > 0 THEN 'SKIPPED'
                             ELSE 'SUCCESS'
        END,
        last_played_at = NOW()
    WHERE id = config_suite_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

go

-- rollback DROP FUNCTION IF EXISTS update_test_or_suite_configuration();