CREATE DATABASE IF NOT EXISTS resaresusers;
USE resaresusers;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS user_roles;

CREATE TABLE roles
(
    id   INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(60) DEFAULT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE users
(
    id INT NOT NULL AUTO_INCREMENT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    email VARCHAR(255),
    name VARCHAR(100),
    organization_id INT,
    password VARCHAR(100),
    username VARCHAR(100),
    PRIMARY KEY (id),
    UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (username),
    UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (email)
);

CREATE TABLE user_roles
(
    role_id INT REFERENCES roles (id),
    user_id INT REFERENCES users (id),
    PRIMARY KEY (user_id, role_id)

);

INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_EDITOR');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO users (id, created_at, updated_at, email, name, organization_id, password, username) VALUES (1, '2020-04-13 15:32:57.181000000', '2020-04-13 15:32:57.181000000', 'test@test.fr', 'Admin Principale', null, '$2a$10$w0qS1dPdn6zcrujzZXyuu.NhgzctpooITaRPgcB.KiWdUzO4gE4hm', 'admin');
INSERT INTO user_roles (user_id, role_id) VALUES (1,3);

CREATE DATABASE IF NOT EXISTS resaresresource;
USE resaresresource;

DROP TABLE IF EXISTS organisation;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS attributes;

CREATE DATABASE IF NOT EXISTS resaresreservation;
USE resaresreservation;

DROP TABLE IF EXISTS reservation;
