SELECT item_uuid, item_name, item_weight, item_value, item_rarity FROM item_catalog 
WHERE SIMILARITY(item_name, :name) > 0.1
ORDER BY SIMILARITY(item_name, :name) DESC;