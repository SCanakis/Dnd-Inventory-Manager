CREATE TABLE users (
    user_uuid UUID PRIMARY KEY, 
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE authorities (
    username TEXT NOT NULL,
    authority TEXT NOT NULL,
    PRIMARY KEY (username, authority)
);



CREATE DOMAIN ability_score SMALLINT
    CHECK(VALUE BETWEEN 0 AND 30);

CREATE TABLE class(
    class_uuid UUID PRIMARY KEY NOT NULL,
    name VARCHAR(50) UNIQUE NOT NULL,
    hit_dice_value SMALLINT NOT NULL CHECK(hit_dice_value IN (4,6,8,10,12)),
    description TEXT

);

CREATE TABLE subclass(
    subclass_uuid UUID PRIMARY KEY NOT NULL,
    name VARCHAR(50) not NULL,
    class_source UUID NOT NULL REFERENCES class(class_uuid),
    
    UNIQUE(name)
);



CREATE TABLE background(
    background_uuid UUID PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description text,
    starting_gold SMALLINT CHECK (starting_gold >= 0)
);

CREATE TABLE race(
    race_uuid UUID NOT NULL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,

    stat_increase_str ability_score DEFAULT 0,
    stat_increase_dex ability_score DEFAULT 0,
    stat_increase_con ability_score DEFAULT 0,
    stat_increase_int ability_score DEFAULT 0,
    stat_increase_wis ability_score DEFAULT 0,
    stat_increase_cha ability_score DEFAULT 0
);

CREATE TABLE characters_info(
    char_info_uuid UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,

    inspiration BOOLEAN DEFAULT False,
    race_uuid UUID NOT NULL REFERENCES race(race_uuid) ,
    background_uuid UUID  NOT NULL REFERENCES background(background_uuid),

    strength ability_score DEFAULT 0,
    dexterity ability_score DEFAULT 0,
    constitution ability_score DEFAULT 0,
    intelligence ability_score DEFAULT 0,
    wisdom ability_score DEFAULT 0,
    charisma ability_score DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CHECK(
        strength >= 0 AND
        dexterity >= 0 AND
        constitution >= 0 AND
        intelligence >= 0 AND
        wisdom >= 0 AND
        charisma >= 0 
    )
);

CREATE TABLE character_class(
    char_info_uuid UUID NOT NULL REFERENCES characters_info(char_info_uuid) ON DELETE CASCADE,
    class_uuid UUID NOT NULL REFERENCES class(class_uuid),
    subclass_uuid UUID REFERENCES subclass(subclass_uuid),
    level SMALLINT NOT NULL DEFAULT 1 CHECK(level > 0),
    hit_dice_remaining SMALLINT NOT NULL CHECK(hit_dice_remaining BETWEEN 0 AND level),

    PRIMARY KEY(char_info_uuid, class_uuid)
);


CREATE TABLE death_saving_throws(
    char_info_uuid UUID PRIMARY KEY REFERENCES characters_info(char_info_uuid) ON DELETE CASCADE,
    success SMALLINT NOT NULL CHECK(success BETWEEN 0 AND 3) DEFAULT 0,
    failure SMALLINT NOT NULL CHECK(failure BETWEEN 0 AND 3) DEFAULT 0
);

CREATE TABLE hp_handler(
    char_info_uuid UUID PRIMARY KEY REFERENCES characters_info(char_info_uuid) ON DELETE CASCADE,
    max_hp INT NOT NULL CHECK (max_hp>=0),
    current_hp INT NOT NULL CHECK (current_hp>=0),
    temp_hp INT NOT NULL CHECK (temp_hp>=0)
);


CREATE TABLE users_characters  (
    user_uuid UUID NOT NULL REFERENCES users(user_uuid),
    character_uuid UUID NOT NULL REFERENCES characters_info(char_info_uuid),
    PRIMARY KEY (user_uuid, character_uuid)
);
