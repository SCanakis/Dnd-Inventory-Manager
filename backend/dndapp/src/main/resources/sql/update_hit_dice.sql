UPDATE character_class
SET hit_dice_remaining = :hit_dice
WHERE char_info_uuid= :uuid AND class_uuid = :class_uuid;