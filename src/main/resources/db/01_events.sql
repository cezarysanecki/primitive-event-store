CREATE TABLE events
(
    id        uuid         not null primary key,
    stream_id uuid         not null,
    version   bigint       not null,
    type      varchar(500) not null,
    data      jsonb        not null
);

-- id, stream_id, version, type - metadata
-- data - stored data