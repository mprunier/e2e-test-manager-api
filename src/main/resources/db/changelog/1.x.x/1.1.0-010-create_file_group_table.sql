-- liquibase formatted sql
-- changeset mprunier:1.1.0-010-create_file_group_table.sql

CREATE TABLE file_group
(
    file           VARCHAR(500) PRIMARY KEY,
    environment_id BIGINT       NOT NULL,
    group_name     VARCHAR(100) NOT NULL,
    CONSTRAINT fk__file_group__environment_id FOREIGN KEY (environment_id) REFERENCES environment (id) ON DELETE CASCADE
);


-- rollback DROP TABLE IF EXISTS file_group;


