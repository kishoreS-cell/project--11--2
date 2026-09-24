-- Seed data for the FlowNet database (matches the original front-end demo)

USE flownet;

-- Demo staff accounts (matches the credentials on the front-end staff login
-- page). Passwords are hashed with SHA2(...,256) - never stored plaintext.
INSERT INTO users (username, password_hash, role) VALUES
('admin',   SHA2('flownet123', 256), 'admin'),
('staff',   SHA2('ward2026', 256),   'staff'),
('citizen', SHA2('chennai01', 256),  'citizen');

-- New accounts created from the web sign-up form (POST /api/signup) or the
-- console's "Create an account" option land in this same table. Check them with:
--   SELECT id, username, role, created_at, last_login FROM users;

INSERT INTO zones (id, name, risk, water_level, flow, status) VALUES
('wardT', 'T Nagar Outfall',      'safe', '0.6 m', 'Normal',   'Normal drainage flow'),
('wardN', 'Nungambakkam Channel', 'safe', '0.8 m', 'Normal',   'Normal drainage flow'),
('wardA', 'Adyar Confluence',     'warn', '1.1 m', 'Elevated', 'Water levels rising - monitor'),
('wardV', 'Velachery Basin',      'safe', '1.4 m', 'Rising',   'Normal drainage flow'),
('wardP', 'Perungudi Lowland',    'safe', '0.7 m', 'Normal',   'Normal drainage flow');

INSERT INTO issue_types (id, label) VALUES
('waterlogging', 'Waterlogged road'),
('blocked',      'Blocked / overflowing drain'),
('manhole',      'Damaged manhole cover'),
('sewage',       'Sewage overflow'),
('other',        'Something else');

INSERT INTO tickets
(id, issue_type_id, zone_id, landmark, description, severity, priority, team, status, stage, source, submitted_at)
VALUES
('FN-2026-0001', 'waterlogging', 'wardV', 'Near Velachery bus depot',
 'Ankle-deep water near the bus depot.', 'worse', 'med', 'Ward 12 Field Crew',
 'Assigned to team', 2, 'Citizen report', DATE_SUB(NOW(), INTERVAL 6 HOUR)),
('FN-2026-0002', 'blocked', 'wardT', '2nd Street outfall',
 'Drain was overflowing, now cleared.', 'notice', 'low', 'Ward 7 Sanitation',
 'Resolved', 3, 'Citizen report', DATE_SUB(NOW(), INTERVAL 1 DAY));
