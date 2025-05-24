-- file: get_character_classes.sql
SELECT
  cc.char_info_uuid,
  c.class_uuid,
  c.name                  AS class_name,
  cc.subclass_uuid,
  sc.name                 AS subclass_name,
  cc.level,
  cc.hit_dice_remaining   AS remaining_hit_dice,
  c.hit_dice_value    
FROM character_class cc
JOIN class c ON cc.class_uuid = c.class_uuid
LEFT JOIN subclass sc ON cc.class_uuid = sc.class_source
WHERE cc.char_info_uuid = '30000000-0000-0000-0000-000000000001';
