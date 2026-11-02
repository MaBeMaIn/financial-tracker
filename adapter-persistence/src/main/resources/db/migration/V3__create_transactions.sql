-- Money the user moved into or out of an account (FR-3.6). Transactions belong to the account
-- aggregate but have a table of their own (ADR-0009). Balances are derived from these rows,
-- never stored (FR-3.8).
create table transactions (
    id          uuid                        not null,
    account_id  uuid                        not null,
    occurred_on date                        not null,
    amount      numeric(19, 2)              not null,
    direction   varchar(20)                 not null,
    note        varchar(500),
    created_at  timestamp(6) with time zone not null,
    updated_at  timestamp(6) with time zone not null,
    constraint pk_transactions primary key (id),
    -- The amount is strictly positive; the direction carries the sign (invariant 3).
    constraint ck_transactions_amount check (amount > 0),
    -- Direction is stored by name, never by ordinal.
    constraint ck_transactions_direction check (direction in ('DEPOSIT', 'WITHDRAWAL')),
    constraint fk_transactions_account foreign key (account_id) references accounts (id)
);

-- Transactions are always read per account, ordered by the date they happened (FR-3.9).
create index ix_transactions_account_id_occurred_on on transactions (account_id, occurred_on);
