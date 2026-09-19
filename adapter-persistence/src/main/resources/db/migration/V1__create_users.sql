-- A registered person (FR-2.1, FR-2.3). "users" rather than "user", which is reserved.
create table users (
    id            uuid          not null,
    email         varchar(320)  not null,
    display_name  varchar(100)  not null,
    password_hash varchar(255)  not null,
    constraint pk_users primary key (id)
);

-- Email is the identity a user logs in with, so it is unique. EmailAddress lower-cases
-- before validating, which is what makes this index mean what it says.
create unique index ux_users_email on users (email);
