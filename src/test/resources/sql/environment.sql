INSERT INTO environment (id, description, project_id, token, branch, is_enabled, max_parallel_test_number, created_by, updated_by, created_at, updated_at)
VALUES ('a13aae1b-385a-4d3b-85b9-e0a4f62386fd', 'Test', '1391', 'xxxx', 'master', TRUE, 4, 'xxxx', 'xxxx', '2023-11-05 15:28:41.730518 +00:00', '2025-01-05 12:40:52.494466 +00:00')
ON CONFLICT (id) DO NOTHING;
