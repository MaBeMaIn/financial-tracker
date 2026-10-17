-- A registered person (FR-2.1, FR-2.3). "users" rather than "user", which is reserved.
-- The key is the identifier the domain generated: a UUID version 7, time-ordered, so inserts
-- land at the end of the primary key index rather than scattered through it (ADR-0008).
create table users (
    id            uuid                        not null,
    email         varchar(320)                not null,
    display_name  varchar(100)                not null,
    password_hash varchar(255)                not null,
    created_at    timestamp(6) with time zone not null,
    updated_at    timestamp(6) with time zone not null,
    constraint pk_users primary key (id)
);

-- Email is the identity a user logs in with, so it is unique. EmailAddress lower-cases
-- before validating, which is what makes this index mean what it says.
create unique index ux_users_email on users (email);
