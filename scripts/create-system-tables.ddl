--
-- Security tables
--
CREATE TABLE security_user (

  id                     INTEGER NOT NULL AUTO_INCREMENT,
  date_record_valid_from DATE NOT NULL,
  date_record_valid_to   DATE NOT NULL,
  password               VARCHAR(64) NOT NULL,
  user_id                VARCHAR(32) NOT NULL,
  number                 VARCHAR(12) NOT NULL,
  email_address          VARCHAR(255) NOT NULL,
  last_name              VARCHAR(255) NOT NULL,
  first_name             VARCHAR(255) NOT NULL,
  suspend_account        BOOLEAN NOT NULL DEFAULT FALSE,
  
  PRIMARY KEY(id)
  
);

CREATE TABLE security_role (

  id          INTEGER NOT NULL AUTO_INCREMENT,
  name        VARCHAR(32) NOT NULL,
  description VARCHAR(255) NOT NULL,
  
  PRIMARY KEY(id)

);

CREATE TABLE security_permission (

  id   INTEGER NOT NULL AUTO_INCREMENT,
  name VARCHAR(32) NOT NULL,
  
  PRIMARY KEY(id)

);

CREATE TABLE security_user_role (

  user_id INTEGER NOT NULL,
  role_id INTEGER NOT NULL,
  
  PRIMARY KEY(user_id, role_id)

);

CREATE TABLE security_role_permission (

  role_id       INTEGER NOT NULL,
  permission_id INTEGER NOT NULL,
  
  PRIMARY KEY(role_id, permission_id)

);

CREATE TABLE security_user_sequence (

  id   INTEGER NOT NULL AUTO_INCREMENT,
  sequence_number INTEGER NOT NULL DEFAULT 1000,
  
  PRIMARY KEY(id)

);

