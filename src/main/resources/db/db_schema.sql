CREATE TABLE IF NOT EXISTS bug (
  bug_uuid VARCHAR(36) NOT NULL,
  bug_description VARCHAR(255),
  PRIMARY KEY (bug_uuid)
);

