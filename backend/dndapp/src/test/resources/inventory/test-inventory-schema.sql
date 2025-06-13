CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- Fuzzy Finder
CREATE EXTENSION IF NOT EXISTS pg_trgm;


CREATE TYPE equippable_type AS ENUM (
    'armor',
    'cloak',
    'bracers',
    'head',
    'belt',
    'hands',
    'ringL',
    'ringR',
    'feet',
    'mainhand',
    'offhand',
    'twohand',
    'back',
    'spellfocus',
    'custom'
);

CREATE TYPE rarity AS ENUM (
    'common',
    'uncommon',
    'rare',
    'very_rare',
    'legendary'
);

CREATE TABLE item_catalog (
    item_uuid UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_name VARCHAR(50) NOT NULL UNIQUE,
    item_description TEXT NOT NULL,
   
    item_weight INT DEFAULT 0 CHECK(item_weight >= 0),
    item_value INT DEFAULT 0 CHECK(item_value >= 0),
    item_rarity rarity NOT NULL DEFAULT 'common',

    attackable BOOLEAN DEFAULT false,
    
    ac_bonus SMALLINT,
    add_as_to_ac json,

    equippable BOOLEAN,
    attunable BOOLEAN,
    item_equippable_type equippable_type[],

    ability_requirment json,

    skill_altered_roll_type json,
    skill_altered_bonus json
);

CREATE TABLE item_class_eligibility (
    item_uuid UUID REFERENCES item_catalog(item_uuid) ON DELETE CASCADE,
    class_uuid UUID REFERENCES class(class_uuid) ON DELETE CASCADE,
    PRIMARY KEY (item_uuid, class_uuid)    
);




-- Create some useful indexes for performance
CREATE INDEX idx_item_catalog_name ON item_catalog(item_name);
CREATE INDEX idx_item_catalog_rarity ON item_catalog(item_rarity);
CREATE INDEX idx_item_catalog_equippable ON item_catalog(equippable);
CREATE INDEX idx_item_class_eligibility_item_uuid ON item_class_eligibility(item_uuid);
CREATE INDEX idx_item_class_eligibility_class_uuid ON item_class_eligibility(class_uuid);

-- Enable fuzzy search on item names
CREATE INDEX idx_item_catalog_name_trgm ON item_catalog USING gin (item_name gin_trgm_ops);