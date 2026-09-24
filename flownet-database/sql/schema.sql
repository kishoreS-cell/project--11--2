-- FlowNet database schema (MySQL)

CREATE DATABASE IF NOT EXISTS flownet;
USE flownet;

DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS issue_types;
DROP TABLE IF EXISTS zones;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id            INT          AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash CHAR(64)     NOT NULL,   -- SHA2(password, 256), hex-encoded
    role          VARCHAR(30)  NOT NULL DEFAULT 'citizen', -- citizen | staff | admin
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login    DATETIME     NULL,      -- set by UserDAO.recordLogin() on every successful sign-in
    CONSTRAINT chk_users_role CHECK (role IN ('citizen','staff','admin'))
);

-- Usernames are compared case-insensitively by the default utf8mb4 collation,
-- and the UNIQUE constraint above is what finally stops duplicate sign-ups
-- even if two requests race past the ApiServer's usernameExists() check.

CREATE TABLE zones (
    id          VARCHAR(20)  PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    risk        VARCHAR(10)  NOT NULL DEFAULT 'safe',   -- safe | warn | crit
    water_level VARCHAR(20),
    flow        VARCHAR(20),
    status      VARCHAR(150)
);

CREATE TABLE issue_types (
    id    VARCHAR(30)  PRIMARY KEY,
    label VARCHAR(100) NOT NULL
);

CREATE TABLE tickets (
    id                VARCHAR(20)  PRIMARY KEY,
    issue_type_id     VARCHAR(30)  NOT NULL,
    zone_id           VARCHAR(20)  NOT NULL,
    landmark          VARCHAR(200),
    description       TEXT,
    severity          VARCHAR(10),                      -- notice | worse | urgent
    priority          VARCHAR(10),                       -- low | med | high
    team              VARCHAR(100),
    status            VARCHAR(30)  NOT NULL DEFAULT 'Submitted',
    stage             INT          NOT NULL DEFAULT 0,   -- 0..3
    source            VARCHAR(50)  DEFAULT 'Citizen report',
    reporter_name     VARCHAR(100),
    reporter_contact  VARCHAR(100),
    submitted_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ticket_issue_type FOREIGN KEY (issue_type_id) REFERENCES issue_types(id),
    CONSTRAINT fk_ticket_zone FOREIGN KEY (zone_id) REFERENCES zones(id)
);

CREATE INDEX idx_tickets_zone ON tickets(zone_id);
CREATE INDEX idx_tickets_status ON tickets(status);
