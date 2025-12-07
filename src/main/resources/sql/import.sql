-- LPM Database Seed Data
-- Development and Test Default Data
-- Note: Tables are created automatically by Hibernate based on entity definitions

-- Seed data: Default roles
INSERT INTO role (name, description, color, icon, "order", created_at, updated_at) VALUES
('ADMIN', 'System administrator with full access', '#FF0000', 'shield', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('PROJECT_MANAGER', 'Project lead with project management permissions', '#0000FF', 'briefcase', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('DEVELOPER', 'Team member with ticket creation and editing', '#00AA00', 'code', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('VIEWER', 'Read-only access to projects and tickets', '#999999', 'eye', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Seed data: Default users
-- Passwords: admin/admin, manager/manager, developer/developer
INSERT INTO "user" (username, email, password_hash, display_name, status, created_at, updated_at) VALUES
('admin', 'admin@lpm.local', 'bHBtZGV2c2FsdDEyMzQ1Njc4OTA=:1+UXcFhT3b+FyQrAtXaH9nrBwGc2MHOo6vat9EEXtPI=', 'Administrator', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('manager', 'manager@lpm.local', 'bHBtZGV2c2FsdDEyMzQ1Njc4OTA=:mhtdft2mhgY2TyOtHK9zm/CZFsHoVg1SxdQCT8CWQTk=', 'Project Manager', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('developer', 'developer@lpm.local', 'bHBtZGV2c2FsdDEyMzQ1Njc4OTA=:HciRvYkm9aY+oit7mtFAH8PvDEuRsN9Bdi+bFAypk0g=', 'Developer', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Assign roles to default users
INSERT INTO user_role (user_id, role_id) 
SELECT u.id, r.id FROM "user" u, role r 
WHERE u.username = 'admin' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO user_role (user_id, role_id) 
SELECT u.id, r.id FROM "user" u, role r 
WHERE u.username = 'manager' AND r.name = 'PROJECT_MANAGER'
ON CONFLICT DO NOTHING;

INSERT INTO user_role (user_id, role_id) 
SELECT u.id, r.id FROM "user" u, role r 
WHERE u.username = 'developer' AND r.name = 'DEVELOPER'
ON CONFLICT DO NOTHING;

