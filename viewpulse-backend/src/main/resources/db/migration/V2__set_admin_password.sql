-- V2__set_admin_password.sql
-- Set admin password to the known bcrypt hash (applied idempotently)
UPDATE admin_user
SET password = '$2b$12$ecKCv7FwYEBjtB0RsXDoQeev3Tso.49PIgeUckvrT5k6hacOXxtw.',
    is_active = b'1',
    created_at = NOW()
WHERE username = 'admin';

-- If admin row does not exist, insert (safe guard)
INSERT INTO admin_user (created_at, created_by, is_active, location_id, password, role, username)
SELECT NOW(), NULL, b'1', NULL, '$2b$12$ecKCv7FwYEBjtB0RsXDoQeev3Tso.49PIgeUckvrT5k6hacOXxtw.', 'super_admin', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM admin_user WHERE username='admin');
