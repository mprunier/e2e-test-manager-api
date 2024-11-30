-- liquibase formatted sql
-- changeset mprunier:1.2.0-120-create_worker_unit_table.sql

CREATE TABLE worker_unit
(
    id        VARCHAR(255) PRIMARY KEY,
    worker_id UUID        NOT NULL,
    status    VARCHAR(50) NOT NULL,
    filter    TEXT,
    CONSTRAINT fk_worker_unit_worker FOREIGN KEY (worker_id)
        REFERENCES worker (id) ON DELETE CASCADE
);

CREATE INDEX idx_worker_unit_worker_id ON worker_unit (worker_id);

--rollback DROP TABLE worker_unit;