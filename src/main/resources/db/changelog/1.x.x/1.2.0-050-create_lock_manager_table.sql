-- liquibase formatted sql
-- changeset mprunier:1.2.0-050-create_lock_manager_table.sql

CREATE TABLE lock_manager
(
    resource_type VARCHAR(255) NOT NULL,
    resource_id   VARCHAR(255) NOT NULL,
    PRIMARY KEY (resource_type, resource_id)
);
--rollback DROP TABLE lock_manager;