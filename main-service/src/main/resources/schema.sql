DROP TABLE IF EXISTS EVENTS CASCADE;
DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS CATEGORIES CASCADE;
DROP TABLE IF EXISTS LOCATIONS CASCADE;
DROP TABLE IF EXISTS REQUESTS CASCADE;
DROP TABLE IF EXISTS COMMENTS CASCADE;

CREATE TABLE IF NOT EXISTS USERS (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    EMAIL VARCHAR(255) NOT NULL UNIQUE,
    NAME VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS CATEGORIES (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS LOCATIONS (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    LAT REAL NOT NULL,
    LON REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS EVENTS (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    ANNOTATION VARCHAR(2000) NOT NULL,
    CREATED_ON TIMESTAMP NOT NULL,
    DESCRIPTION VARCHAR(7000) NOT NULL,
    EVENT_DATE TIMESTAMP NOT NULL,
    PAID BOOLEAN NOT NULL,
    PARTICIPANT_LIMIT INTEGER NOT NULL,
    PUBLISHED_ON TIMESTAMP NOT NULL,
    REQUEST_MODERATION BOOLEAN NOT NULL,
    STATE VARCHAR(255) NOT NULL,
    TITLE VARCHAR(120) NOT NULL,
    LOCATION_ID BIGINT REFERENCES LOCATIONS(ID),
    CATEGORY_ID BIGINT REFERENCES CATEGORIES(ID),
    USER_ID BIGINT REFERENCES USERS(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REQUESTS (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    CREATED TIMESTAMP NOT NULL,
    STATUS VARCHAR(255) NOT NULL,
    EVENT_ID BIGINT REFERENCES EVENTS(ID) ON DELETE CASCADE,
    REQUESTER_ID BIGINT REFERENCES USERS(ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMPILATIONS (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    PINNED BOOLEAN NOT NULL,
    TITLE VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS COMPILATIONS_EVENTS (
    COMPILATION_ID BIGINT REFERENCES COMPILATIONS (ID) ON DELETE CASCADE,
    EVENT_ID BIGINT REFERENCES EVENTS (ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMMENTS (
    ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    TEXT VARCHAR(7000) NOT NULL,
    AUTHOR_ID BIGINT REFERENCES USERS(ID) NOT NULL,
    EVENT_ID BIGINT REFERENCES EVENTS(ID) NOT NULL,
    CREATED_ON TIMESTAMP NOT NULL,
    EDITED_ON TIMESTAMP
);