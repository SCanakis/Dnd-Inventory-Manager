SELECT c.class_uuid, c.name 
FROM item_class_eligibility AS ice
JOIN class AS c ON ice.class_uuid = c.class_uuid
WHERE ice.item_uuid = :item_uuid; 