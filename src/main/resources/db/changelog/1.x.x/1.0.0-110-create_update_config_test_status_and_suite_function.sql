-- liquibase formatted sql
-- changeset mprunier:1.0.0-110-create_update_config_test_status_and_suite_function.sql endDelimiter:go

CREATE OR REPLACE FUNCTION update_config_test_status_and_suite() RETURNS TRIGGER AS
$$
BEGIN
    -- Mettre à jour le statut d'un configuration_test en fonction des statuts de ses tests
    DECLARE
        last_test_status TEXT;
    BEGIN
        -- Compter combien de tests associés ont échoué
        SELECT status INTO last_test_status FROM test WHERE configuration_test_id = NEW.configuration_test_id ORDER BY id DESC LIMIT 1;

        IF last_test_status = 'SUCCESS' THEN
            UPDATE configuration_test SET status = 'SUCCESS' WHERE id = NEW.configuration_test_id;
        ELSE
            UPDATE configuration_test SET status = 'FAILED' WHERE id = NEW.configuration_test_id;
        END IF;
        UPDATE configuration_test SET updated_at = NOW(), last_played_at = NOW() WHERE id = NEW.configuration_test_id;

        -- Mettre à jour le statut d'un configuration_suite et ses parents
        DECLARE
            config_suite_id INT;
            parent_id INT;
            failed_count INT;
        BEGIN
            SELECT configuration_suite_id INTO config_suite_id FROM configuration_test WHERE id = NEW.configuration_test_id;

            LOOP
                -- Compter combien de configuration_test associés ont échoué
                -- en considérant uniquement le dernier test pour chaque configuration_test
                WITH LastTests AS (SELECT ct.configuration_suite_id, t.status
                                   FROM test t
                                            JOIN (SELECT configuration_test_id, MAX(id) AS MaxTestId
                                                  FROM test
                                                  GROUP BY configuration_test_id) lt ON t.configuration_test_id = lt.configuration_test_id AND t.id = lt.MaxTestId
                                            JOIN configuration_test ct ON t.configuration_test_id = ct.id)
                SELECT COUNT(*)
                INTO failed_count
                FROM LastTests
                WHERE configuration_suite_id = config_suite_id
                    AND status = 'FAILED'
                   OR status = 'CANCELED'
                   OR status = 'SYSTEM_ERROR'
                   OR status = 'NO_CORRESPONDING_TEST'
                   OR status = 'NO_REPORT_ERROR'
                   OR status = 'UNKNOWN';

                IF failed_count = 0 THEN
                    UPDATE configuration_suite SET status = 'SUCCESS' WHERE id = config_suite_id;
                ELSE
                    UPDATE configuration_suite SET status = 'FAILED' WHERE id = config_suite_id;
                END IF;
                UPDATE configuration_suite SET updated_at = NOW(), last_played_at = NOW() WHERE id = config_suite_id;

                -- Chercher le parent
                SELECT parent_configuration_suite_id INTO parent_id FROM configuration_suite WHERE id = config_suite_id;
                IF parent_id IS NULL THEN
                    EXIT; -- Pas de parent, sortie de la boucle
                END IF;

                config_suite_id := parent_id; -- Continue avec le parent
            END LOOP;
        END;
    END;

    RETURN NEW;
END;
$$
LANGUAGE plpgsql;

go

-- rollback DROP FUNCTION update_config_test_status_and_suite;
