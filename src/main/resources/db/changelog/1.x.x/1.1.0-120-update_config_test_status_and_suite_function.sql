-- liquibase formatted sql
-- changeset mprunier:1.1.0-120-update_config_test_status_and_suite_function.sql endDelimiter:go

CREATE OR REPLACE FUNCTION update_config_test_status_and_suite() RETURNS TRIGGER AS
$$
DECLARE
    last_test_status  TEXT;
    config_suite_id   INT;
    succeeded_count   INT;
    failed_count      INT;
    new_count         INT;
    skipped_count     INT;
    canceled_count    INT;
BEGIN
    SELECT status INTO last_test_status FROM test WHERE configuration_test_id = NEW.configuration_test_id ORDER BY id DESC LIMIT 1;

    UPDATE configuration_test
    SET status         = CASE
                             WHEN last_test_status IN ('FAILED', 'SYSTEM_ERROR', 'NO_CORRESPONDING_TEST', 'NO_REPORT_ERROR', 'UNKNOWN') THEN 'FAILED'
                             ELSE last_test_status END,
        updated_at     = NOW(),
        last_played_at = NOW()
    WHERE id = NEW.configuration_test_id;

    SELECT configuration_suite_id INTO config_suite_id FROM configuration_test WHERE id = NEW.configuration_test_id;

    WITH test_status_counts AS (SELECT SUM((status = 'SUCCESS')::INT)                                                                          AS succeeded,
                                       SUM((status IN ('FAILED', 'SYSTEM_ERROR', 'NO_CORRESPONDING_TEST', 'NO_REPORT_ERROR', 'UNKNOWN'))::INT) AS failed,
                                       SUM((status = 'NEW')::INT)                                                                              AS newer,
                                       SUM((status = 'SKIPPED')::INT)                                                                          AS skipped,
                                       SUM((status = 'CANCELED')::INT)                                                                         AS canceled
                                FROM test
                                WHERE configuration_test_id IN (SELECT id
                                                                FROM configuration_test
                                                                WHERE configuration_suite_id = config_suite_id)
                                  AND id IN (SELECT MAX(id)
                                             FROM test
                                             GROUP BY configuration_test_id))

    SELECT succeeded, failed, newer, skipped, canceled
    INTO succeeded_count, failed_count, new_count, skipped_count, canceled_count
    FROM test_status_counts;

    UPDATE configuration_suite
    SET status         = CASE
                             WHEN failed_count > 0 THEN 'FAILED'
                             WHEN canceled_count > 0 THEN 'CANCELED'
                             WHEN new_count > 0 THEN 'NEW'
                             WHEN succeeded_count > 0 AND skipped_count > 0 THEN 'PARTIAL_SKIPPED'
                             WHEN succeeded_count = 0 AND failed_count = 0 AND skipped_count > 0 THEN 'SKIPPED'
                             ELSE 'SUCCESS'
        END,
        updated_at     = NOW(),
        last_played_at = NOW()
    WHERE id = config_suite_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

go

-- rollback SELECT 1;