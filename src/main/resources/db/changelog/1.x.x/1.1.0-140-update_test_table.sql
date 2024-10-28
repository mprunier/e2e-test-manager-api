-- liquibase formatted sql
-- changeset mprunier:1.1.0-140-update_test_table.sql

ALTER TABLE test
    DROP COLUMN video,
    ADD COLUMN video_id BIGINT,
    ADD CONSTRAINT fk__test__video_id FOREIGN KEY (video_id) REFERENCES test_video (id) ON DELETE CASCADE;

-- rollback ALTER TABLE test ADD COLUMN video BYTEA;