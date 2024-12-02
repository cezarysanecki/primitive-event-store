CREATE TABLE IF NOT EXISTS long_projection
(
    id    uuid   not null primary key,
    value bigint not null
);

CREATE TABLE IF NOT EXISTS string_projection
(
    letter char   not null primary key,
    amount bigint not null
);
