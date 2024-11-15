CREATE TABLE IF NOT EXISTS mydb.bug (
  bug_uuid VARCHAR(36) NOT NULL,
  bug_description VARCHAR(255),
  PRIMARY KEY (bug_uuid)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON mydb.bug TO 'bug_user';
