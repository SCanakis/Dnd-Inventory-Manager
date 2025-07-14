UPDATE death_saving_throws
SET failure = :failure
WHERE char_info_uuid = :uuid;