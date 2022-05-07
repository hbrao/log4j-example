DROP DATABASE log4j;
CREATE DATABASE log4j;
USE log4j;

--
-- Message table
--
CREATE TABLE message (

  id          INTEGER      NOT NULL AUTO_INCREMENT,
  
  thread_name VARCHAR(32),
  logger_name VARCHAR(256) NOT NULL,
  level_name  VARCHAR(16)  NOT NULL,
  message     VARCHAR(512) NOT NULL,
  when_logged TIMESTAMP    NOT NULL,
  
  PRIMARY KEY(id)
  
);

CREATE TABLE mapped_diagnostic_context (

  id             INTEGER      NOT NULL AUTO_INCREMENT,
  
  message_id     INTEGER      NOT NULL,
  property_name  VARCHAR(128) NOT NULL,
  property_value VARCHAR(128) NOT NULL,
  
  PRIMARY KEY(id)
);

ALTER TABLE mapped_diagnostic_context ADD FOREIGN KEY (message_id) REFERENCES message(id);