-- liquibase formatted sql
-- changeset mprunier:1.0.0-250-update_config_test_status_and_suite_function.sql endDelimiter:go

CREATE OR REPLACE FUNCTION update_config_test_status_and_suite() RETURNS TRIGGER AS
$$
DECLARE
    last_test_status TEXT;
    config_suite_id INT;
    failed_count INT;
    in_progress_count INT;
    new_count INT;
    skipped_count INT;
    succeeded_count INT;
BEGIN
    -- Mettre à jour le statut d'un configuration_test en fonction des statuts de ses tests
    SELECT status INTO last_test_status FROM test WHERE configuration_test_id = NEW.configuration_test_id ORDER BY id DESC LIMIT 1;

    IF last_test_status = 'SUCCESS' THEN
        UPDATE configuration_test SET status = 'SUCCESS', updated_at = now(), last_played_at = now() WHERE id = NEW.configuration_test_id;
    ELSIF last_test_status = 'IN_PROGRESS' THEN
        UPDATE configuration_test SET status = 'IN_PROGRESS', updated_at = now(), last_played_at = now() WHERE id = NEW.configuration_test_id;
    ELSIF last_test_status = 'SKIPPED' THEN
        UPDATE configuration_test SET status = 'SKIPPED', updated_at = now(), last_played_at = now() WHERE id = NEW.configuration_test_id;
    ELSIF last_test_status = 'FAILED' OR last_test_status = 'SYSTEM_ERROR' OR last_test_status = 'NO_CORRESPONDING_TEST' OR last_test_status = 'NO_REPORT_ERROR' OR last_test_status = 'UNKNOWN' THEN
        UPDATE configuration_test SET status = 'FAILED', updated_at = now(), last_played_at = now() WHERE id = NEW.configuration_test_id;
    END IF;

    -- Mettre à jour le statut d'un configuration_suite
    SELECT configuration_suite_id INTO config_suite_id FROM configuration_test WHERE id = NEW.configuration_test_id;

    -- Compter combien de configuration_test associés ont échoué
    SELECT COUNT(*) INTO succeeded_count
    FROM (
             SELECT t.status
             FROM test t
                      JOIN (
                 SELECT configuration_test_id, MAX(id) AS MaxTestId
                 FROM test
                 GROUP BY configuration_test_id
             ) lt ON t.configuration_test_id = lt.configuration_test_id AND t.id = lt.MaxTestId
                      JOIN configuration_test ct ON t.configuration_test_id = ct.id
             WHERE ct.configuration_suite_id = config_suite_id
               AND t.status = 'SUCCESS'
         ) AS SucceededTests;

    SELECT COUNT(*) INTO failed_count
    FROM (
             SELECT t.status
             FROM test t
                      JOIN (
                 SELECT configuration_test_id, MAX(id) AS MaxTestId
                 FROM test
                 GROUP BY configuration_test_id
             ) lt ON t.configuration_test_id = lt.configuration_test_id AND t.id = lt.MaxTestId
                      JOIN configuration_test ct ON t.configuration_test_id = ct.id
             WHERE ct.configuration_suite_id = config_suite_id
               AND t.status IN ('FAILED', 'SYSTEM_ERROR', 'NO_CORRESPONDING_TEST', 'NO_REPORT_ERROR', 'UNKNOWN')
         ) AS FailedTests;

    SELECT COUNT(*) INTO in_progress_count
    FROM (
             SELECT t.status
             FROM test t
                      JOIN (
                 SELECT configuration_test_id, MAX(id) AS MaxTestId
                 FROM test
                 GROUP BY configuration_test_id
             ) lt ON t.configuration_test_id = lt.configuration_test_id AND t.id = lt.MaxTestId
                      JOIN configuration_test ct ON t.configuration_test_id = ct.id
             WHERE ct.configuration_suite_id = config_suite_id
               AND t.status = 'IN_PROGRESS'
         ) AS InProgressTests;

    SELECT COUNT(*) INTO new_count
    FROM (
             SELECT t.status
             FROM test t
                      JOIN (
                 SELECT configuration_test_id, MAX(id) AS MaxTestId
                 FROM test
                 GROUP BY configuration_test_id
             ) lt ON t.configuration_test_id = lt.configuration_test_id AND t.id = lt.MaxTestId
                      JOIN configuration_test ct ON t.configuration_test_id = ct.id
             WHERE ct.configuration_suite_id = config_suite_id
               AND t.status = 'NEW'
         ) AS NewTests;

    SELECT COUNT(*) INTO skipped_count
    FROM (
             SELECT t.status
             FROM test t
                      JOIN (
                 SELECT configuration_test_id, MAX(id) AS MaxTestId
                 FROM test
                 GROUP BY configuration_test_id
             ) lt ON t.configuration_test_id = lt.configuration_test_id AND t.id = lt.MaxTestId
                      JOIN configuration_test ct ON t.configuration_test_id = ct.id
             WHERE ct.configuration_suite_id = config_suite_id
               AND t.status = 'SKIPPED'
         ) AS SkippedTests;

    IF in_progress_count > 0 THEN
        UPDATE configuration_suite SET status = 'IN_PROGRESS', updated_at = now(), last_played_at = now() WHERE id = config_suite_id;

    ELSIF new_count > 0 THEN
        UPDATE configuration_suite SET status = 'NEW', updated_at = now(), last_played_at = now() WHERE id = config_suite_id;

    ELSIF (succeeded_count > 0 OR failed_count > 0) AND skipped_count > 0 THEN
        UPDATE configuration_suite SET status = 'PARTIAL_SKIPPED', updated_at = now(), last_played_at = now() WHERE id = config_suite_id;

    ELSIF failed_count > 0 THEN
        UPDATE configuration_suite SET status = 'FAILED', updated_at = now(), last_played_at = now() WHERE id = config_suite_id;

    ELSIF succeeded_count = 0 AND failed_count = 0 AND skipped_count > 0 THEN
        UPDATE configuration_suite SET status = 'SKIPPED', updated_at = now(), last_played_at = now() WHERE id = config_suite_id;

    ELSE
        UPDATE configuration_suite SET status = 'SUCCESS', updated_at = now(), last_played_at = now() WHERE id = config_suite_id;

    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

go

-- rollback SELECT 1;