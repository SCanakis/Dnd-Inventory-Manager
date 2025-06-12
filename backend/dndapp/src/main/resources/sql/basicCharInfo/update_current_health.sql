UPDATE hp_handler
SET current_hp = :currentHP
WHERE char_info_uuid= :uuid;