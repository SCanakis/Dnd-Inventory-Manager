-- get_char_info.sql
SELECT
  ci.char_info_uuid,
  ci.name,
  ci.inspiration,
  ci.background_uuid,
  b.name AS background_name,
  ci.race_uuid,
  r.name AS race_name,
  ci.strength,
  ci.dexterity,
  ci.constitution,
  ci.intelligence,
  ci.wisdom,
  ci.charisma
FROM characters_info ci
JOIN background b ON ci.background_uuid = b.background_uuid
JOIN race r       ON ci.race_uuid       = r.race_uuid
WHERE ci.char_info_uuid = :uuid;
