-- get_hp_handler.sql
SELECT current_hp, temp_hp, max_hp
  FROM hp_handler
 WHERE char_info_uuid = :uuid;
