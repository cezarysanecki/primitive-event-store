CREATE TABLE IF NOT EXISTS streams
(
    id      uuid   not null primary key,
    type    text   not null,
    version bigint not null
);