-- liquibase formatted sql
-- changeset mprunier:1.2.0-080-create_test_result_table.sql

CREATE TABLE test_result
(
    id                    UUID PRIMARY KEY,
    worker_id             UUID,
    configuration_test_id UUID                     NOT NULL,
    status                VARCHAR(50)              NOT NULL,
    reference             VARCHAR(255),
    error_url             VARCHAR(1024),
    error_message         TEXT,
    error_stacktrace      TEXT,
    code                  TEXT,
    duration              INTEGER,
    variables             TEXT,
    created_at            TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by            VARCHAR(255)             NOT NULL,
    updated_at            TIMESTAMP WITH TIME ZONE,
    updated_by            VARCHAR(255)
);

CREATE INDEX idx_test_result_configuration_test_id ON test_result (configuration_test_id);
CREATE INDEX idx_test_result_worker_id ON test_result (worker_id);

--rollback DROP TABLE test_result;