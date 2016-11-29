# Add wishee

# --- !Ups
ALTER TABLE wishes
ADD COLUMN wishee text NULL;

# --- !Downs
ALTER TABLE wishes
DROP COLUMN wishee;
