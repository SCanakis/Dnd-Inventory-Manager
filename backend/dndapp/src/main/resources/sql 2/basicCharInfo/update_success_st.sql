UPDATE death_saving_throws
SET success = :success
WHERE char_info_uuid = :uuid;