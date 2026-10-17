-- A place a user's money sits (FR-3.1, FR-3.2). Value is derived from transactions or
-- valuations, so nothing about an amount is stored here.
create table accounts (
    id         uuid                        not null,
    user_id    uuid                        not null,
    name       varchar(100)                not null,
    type       varchar(20)                 not null,
    archived   boolean                     not null default false,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    constraint pk_accounts primary key (id),
    -- AccountType is stored by name, never by ordinal, so the values are readable and stable.
    constraint ck_accounts_type check (type in ('SAVINGS', 'INVESTMENT')),
    -- An account belongs to exactly one user and never outlives them unnoticed (FR-3.4).
    constraint fk_accounts_user foreign key (user_id) references users (id)
);

-- Accounts are always read per user, and H2 does not index the child side of a foreign key.
create index ix_accounts_user_id on accounts (user_id);
