# Wish schema

# --- !Ups
CREATE TABLE wishes (
  name text NOT NULL,
  pseudonym text NOT NULL,
  wish text NOT NULL,
  PRIMARY KEY (name)
);

# --- !Downs
DROP TABLE wishes;
