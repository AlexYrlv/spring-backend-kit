CREATE TABLE users (
    username      VARCHAR(100)  PRIMARY KEY,
    first_name    VARCHAR(200)  NOT NULL,
    last_name     VARCHAR(200)  NOT NULL,
    email         VARCHAR(320)  NOT NULL,
    date_of_birth DATE
);
