CREATE OR REPLACE FUNCTION append_event(
    id uuid,
    data jsonb,
    type text,
    stream_id uuid,
    stream_type text,
    expected_stream_version bigint default null
) RETURNS boolean
    LANGUAGE plpgsql
AS
$$
DECLARE
    stream_version int;
BEGIN
    -- get stream version
    SELECT version
    INTO stream_version
    FROM streams as s
    WHERE s.id = stream_id FOR UPDATE;

    -- if stream does not exist create new one with version 0
    IF stream_version IS NULL THEN
        stream_version := 0;

        INSERT INTO streams
            (id, type, version)
        VALUES (stream_id, stream_type, stream_version);
    END IF;

    -- check optimistic concurrency
    IF expected_stream_version IS NOT NULL AND stream_version != expected_stream_version THEN
        RETURN FALSE;
    END IF;

    -- increment event_version
    stream_version := stream_version + 1;

    -- append event
    INSERT INTO events
        (id, data, stream_id, type, version)
    VALUES (id, data::jsonb, stream_id, type, stream_version);

    -- update stream version
    UPDATE streams as s
    SET version = stream_version
    WHERE s.id = stream_id;

    RETURN TRUE;
END;
$$;