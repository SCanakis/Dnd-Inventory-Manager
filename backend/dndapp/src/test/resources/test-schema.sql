CREATE TABLE IF NOT EXISTS race (
    race_uuid UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS background (
    background_uuid UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS class (
    class_uuid UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    hit_dice_value SMALLINT NOT NULL
);

CREATE TABLE IF NOT EXISTS subclass (
    subclass_uuid UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS characters_info (
    char_info_uuid UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    inspiration BOOLEAN DEFAULT FALSE,
    background_uuid UUID,
    race_uuid UUID,
    strength INTEGER,
    dexterity INTEGER,
    constitution INTEGER,
    intelligence INTEGER,
    wisdom INTEGER,
    charisma INTEGER,
    FOREIGN KEY (background_uuid) REFERENCES background(background_uuid),
    FOREIGN KEY (race_uuid) REFERENCES race(race_uuid)
);

CREATE TABLE IF NOT EXISTS character_class (
    char_info_uuid UUID,
    class_uuid UUID,
    subclass_uuid UUID,
    level INTEGER,
    hit_dice_remaining   SMALLINT NOT NULL CHECK (hit_dice_remaining BETWEEN 0 AND level),
    PRIMARY KEY (char_info_uuid, class_uuid),
    FOREIGN KEY (char_info_uuid) REFERENCES characters_info(char_info_uuid),
    FOREIGN KEY (class_uuid) REFERENCES class(class_uuid),
    FOREIGN KEY (subclass_uuid) REFERENCES subclass(subclass_uuid)
);

CREATE TABLE IF NOT EXISTS hp_handler (
    char_info_uuid UUID PRIMARY KEY,
    current_hp INTEGER,
    max_hp INTEGER,
    temp_hp INTEGER,
    FOREIGN KEY (char_info_uuid) REFERENCES characters_info(char_info_uuid)
);

CREATE TABLE IF NOT EXISTS death_saving_throws (
    char_info_uuid UUID PRIMARY KEY,
    success INTEGER,
    failure INTEGER,
    FOREIGN KEY (char_info_uuid) REFERENCES characters_info(char_info_uuid)
);