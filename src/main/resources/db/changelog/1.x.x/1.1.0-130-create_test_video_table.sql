-- liquibase formatted sql
-- changeset mprunier:1.1.0-130-create_test_video_table.sql

CREATE TABLE test_video
(
    id    BIGSERIAL PRIMARY KEY,
    video BYTEA NOT NULL
);

-- rollback DROP TABLE IF EXISTS test_video;
