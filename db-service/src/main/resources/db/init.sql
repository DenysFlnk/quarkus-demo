DROP TABLE IF EXISTS person;

CREATE TABLE person
(
    id         SERIAL PRIMARY KEY NOT NULL,
    first_name VARCHAR,
    last_name  VARCHAR,
    age        SMALLINT
);

INSERT INTO person(first_name, last_name, age)
VALUES ('John', 'Smith', 24),
       ('Sarah', 'Connor', 32),
       ('Markus', 'Johnson', 45);