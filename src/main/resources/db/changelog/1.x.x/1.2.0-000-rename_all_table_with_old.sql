-- liquibase formatted sql
-- changeset mprunier:1.2.0-000-rename_all_table_with_old.sql

ALTER TABLE configuration_scheduler
    RENAME TO old_configuration_scheduler;
ALTER TABLE configuration_suite
    RENAME TO old_configuration_suite;
ALTER TABLE configuration_suite_tag
    RENAME TO old_configuration_suite_tag;
ALTER TABLE configuration_test
    RENAME TO old_configuration_test;
ALTER TABLE configuration_test_tag
    RENAME TO old_configuration_test_tag;
ALTER TABLE environment
    RENAME TO old_environment;
ALTER TABLE environment_synchronization_error
    RENAME TO old_environment_synchronization_error;
ALTER TABLE environment_variable
    RENAME TO old_environment_variable;
ALTER TABLE file_group
    RENAME TO old_file_group;
ALTER TABLE metrics
    RENAME TO old_metrics;
ALTER TABLE pipeline
    RENAME TO old_pipeline;
ALTER TABLE pipeline_group
    RENAME TO old_pipeline_group;
ALTER TABLE scheduler
    RENAME TO old_scheduler;
ALTER TABLE test
    RENAME TO old_test;
ALTER TABLE test_screenshot
    RENAME TO old_test_screenshot;
ALTER TABLE test_video
    RENAME TO old_test_video;

-- rollback ALTER TABLE old_configuration_scheduler
-- rollback     RENAME TO configuration_scheduler;
-- rollback ALTER TABLE old_configuration_suite
-- rollback     RENAME TO configuration_suite;
-- rollback ALTER TABLE old_configuration_suite_tag
-- rollback     RENAME TO configuration_suite_tag;
-- rollback ALTER TABLE old_configuration_test
-- rollback     RENAME TO configuration_test;
-- rollback ALTER TABLE old_configuration_test_tag
-- rollback     RENAME TO configuration_test_tag;
-- rollback ALTER TABLE old_environment
-- rollback     RENAME TO environment;
-- rollback ALTER TABLE old_environment_synchronization_error
-- rollback     RENAME TO environment_synchronization_error;
-- rollback ALTER TABLE old_environment_variable
-- rollback     RENAME TO environment_variable;
-- rollback ALTER TABLE old_file_group
-- rollback     RENAME TO file_group;
-- rollback ALTER TABLE old_metrics
-- rollback     RENAME TO metrics;
-- rollback ALTER TABLE old_pipeline
-- rollback     RENAME TO executor;
-- rollback ALTER TABLE old_pipeline_group
-- rollback     RENAME TO pipeline_group;
-- rollback ALTER TABLE old_scheduler
-- rollback     RENAME TO scheduler;
-- rollback ALTER TABLE old_test
-- rollback     RENAME TO test;
-- rollback ALTER TABLE old_test_screenshot
-- rollback     RENAME TO test_screenshot;
-- rollback ALTER TABLE old_test_video
-- rollback     RENAME TO test_video;