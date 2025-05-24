-- get_death_saving_throws.sql
SELECT success, failure
  FROM death_saving_throws
 WHERE char_info_uuid = :uuid;