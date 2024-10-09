-- liquibase formatted sql
-- changeset mprunier:1.1.0-030-create_pipeline_group_table.sql

CREATE TABLE pipeline_group
(
    id              BIGSERIAL PRIMARY KEY,
    environment_id  BIGINT                   NOT NULL,
    total_pipelines BIGINT                   NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk__pipeline_group__environment_id FOREIGN KEY (environment_id) REFERENCES environment (id) ON DELETE CASCADE
);


-- rollback DROP TABLE IF EXISTS pipeline_group;


