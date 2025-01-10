-- liquibase formatted sql
-- changeset mprunier:1.0.0-100-create_test_result_video_table.sql

CREATE TABLE test_result_video
(
    id             UUID PRIMARY KEY,
    test_result_id UUID  NOT NULL,
    video          BYTEA NOT NULL,
    CONSTRAINT fk_test_result_video_test_result FOREIGN KEY (test_result_id) REFERENCES test_result (id) ON DELETE CASCADE
);

CREATE INDEX idx_test_result_video_test_result ON test_result_video (test_result_id);

--rollback DROP TABLE test_result_video;