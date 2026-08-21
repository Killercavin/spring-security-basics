INSERT INTO sites (id, name, location)
VALUES (
           '794bdb3d-4074-4b02-8741-31dc33d912dd',
           'Default Site',
           'Set during onboarding'
       );

INSERT INTO users (id, name, email, password_hash, role_id, site_id)
SELECT
    '70dd0d7d-73ff-4930-b50c-a3b16592f518',
    'Admin',
    'admin@gatelog.app',
    '$2b$10$S6FDrSOw75yL4g6DQZM26OHFKUjua6A0dZx3l/UlDh4Jo35Am8JPi',
    r.id,
    '794bdb3d-4074-4b02-8741-31dc33d912dd'
FROM roles r
WHERE r.name = 'SUPER_ADMIN';