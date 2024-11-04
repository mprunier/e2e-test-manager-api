-- liquibase formatted sql
-- changeset mprunier:1.1.1-000-fix_file_group_table.sql

ALTER TABLE public.file_group
    DROP CONSTRAINT file_group_pkey;

ALTER TABLE public.file_group
    ADD PRIMARY KEY (file, environment_id);

-- rollback alter table public.file_group
-- rollback     drop constraint file_group_pkey;
-- rollback
-- rollback alter table public.file_group
-- rollback     add primary key (file, environment_id);