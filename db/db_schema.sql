CREATE TABLE IF NOT EXISTS mydb.bug (
  bug_id INT NOT NULL AUTO_INCREMENT,
  bug_description VARCHAR(255),
  PRIMARY KEY (bug_id)
);

GRANT SELECT, INSERT, UPDATE, DELETE ON mydb.bug TO 'bug_user';
