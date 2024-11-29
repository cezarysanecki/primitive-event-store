CREATE TABLE IF NOT EXISTS events
(
    id        uuid                     not null primary key,
    data      jsonb                    not null,
    stream_id uuid                     not null,
    type      text                     not null,
    version   bigint                   not null,
    created   timestamp with time zone not null default (now()),
    foreign key (stream_id) references streams (id),
    constraint events_stream_and_version unique (stream_id, version)

);

-- id, stream_id, version, type - metadata
-- data - stored data