-- Create extensions
CREATE EXTENSION IF NOT EXISTS pg_trgm WITH SCHEMA public;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;

-- Create domains and types
CREATE DOMAIN public.ability_score AS smallint
    CONSTRAINT ability_score_check CHECK (((VALUE >= 0) AND (VALUE <= 30)));

CREATE TYPE public.rarity AS ENUM (
    'common',
    'uncommon',
    'rare',
    'very_rare',
    'legendary'
);

-- Create tables (matching original schema exactly, excluding backup tables)
CREATE TABLE public.authorities (
    username text NOT NULL,
    authority text NOT NULL
);

CREATE TABLE public.background (
    background_uuid uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name character varying(50) NOT NULL,
    description text,
    starting_gold smallint,
    CONSTRAINT background_starting_gold_check CHECK ((starting_gold >= 0))
);

CREATE TABLE public.character_class (
    char_info_uuid uuid NOT NULL,
    class_uuid uuid NOT NULL,
    subclass_uuid uuid,
    level smallint DEFAULT 1 NOT NULL,
    hit_dice_remaining smallint NOT NULL,
    CONSTRAINT character_class_check CHECK (((hit_dice_remaining >= 0) AND (hit_dice_remaining <= level))),
    CONSTRAINT character_class_level_check CHECK ((level > 0))
);

CREATE TABLE public.character_has_item_slot (
    character_uuid uuid NOT NULL,
    container_uuid uuid NOT NULL,
    item_uuid uuid NOT NULL,
    attuned boolean,
    equipped boolean,
    in_attack_tab boolean,
    quantity integer NOT NULL
);

CREATE TABLE public.characters_info (
    char_info_uuid uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name character varying(50) NOT NULL,
    inspiration boolean DEFAULT false,
    race_uuid uuid NOT NULL,
    background_uuid uuid NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    ability_scores json,
    hp_handler json,
    death_saving_throws json
);

CREATE TABLE public.class (
    class_uuid uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name character varying(50) NOT NULL,
    hit_dice_value character varying(255) NOT NULL,
    description character varying(255),
    class_name character varying(255),
    CONSTRAINT class_hit_dice_value_check CHECK (((hit_dice_value)::text = ANY (ARRAY[('D4'::character varying)::text, ('D6'::character varying)::text, ('D8'::character varying)::text, ('D10'::character varying)::text, ('D12'::character varying)::text])))
);

CREATE TABLE public.item_catalog (
    item_uuid uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    item_name character varying(50) NOT NULL,
    item_description text NOT NULL,
    item_weight integer DEFAULT 0,
    item_value integer DEFAULT 0,
    attackable boolean DEFAULT false,
    ac_bonus smallint,
    add_as_to_ac json,
    equippable boolean,
    attunable boolean,
    item_equippable_type character varying[],
    ability_requirment json,
    skill_altered_roll_type json,
    skill_altered_bonus json,
    item_rarity character varying(255) DEFAULT 'common'::public.rarity NOT NULL,
    CONSTRAINT item_catalog_item_value_check CHECK ((item_value >= 0)),
    CONSTRAINT item_catalog_item_weight_check CHECK ((item_weight >= 0))
);

CREATE TABLE public.item_class_eligibility (
    item_uuid uuid NOT NULL,
    class_uuid uuid NOT NULL
);

CREATE TABLE public.race (
    race_uuid uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name character varying(50) NOT NULL,
    stat_increases json
);

CREATE TABLE public.subclass (
    subclass_uuid uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    name character varying(50) NOT NULL,
    class_source uuid NOT NULL
);

CREATE TABLE public.users (
    user_uuid uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    username character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    enabled boolean DEFAULT true NOT NULL
);

CREATE TABLE public.users_characters (
    user_uuid uuid NOT NULL,
    character_uuid uuid NOT NULL
);

-- Add primary key constraints (matching original schema exactly)
ALTER TABLE ONLY public.authorities
    ADD CONSTRAINT authorities_pkey PRIMARY KEY (username, authority);

ALTER TABLE ONLY public.background
    ADD CONSTRAINT background_pkey PRIMARY KEY (background_uuid);

ALTER TABLE ONLY public.background
    ADD CONSTRAINT background_name_key UNIQUE (name);

ALTER TABLE ONLY public.character_class
    ADD CONSTRAINT character_class_pkey PRIMARY KEY (char_info_uuid, class_uuid);

ALTER TABLE ONLY public.character_has_item_slot
    ADD CONSTRAINT character_has_item_slot_pkey PRIMARY KEY (character_uuid, container_uuid, item_uuid);

ALTER TABLE ONLY public.characters_info
    ADD CONSTRAINT characters_info_pkey PRIMARY KEY (char_info_uuid);

ALTER TABLE ONLY public.class
    ADD CONSTRAINT class_pkey PRIMARY KEY (class_uuid);

ALTER TABLE ONLY public.class
    ADD CONSTRAINT class_name_key UNIQUE (name);

ALTER TABLE ONLY public.item_catalog
    ADD CONSTRAINT item_catalog_pkey PRIMARY KEY (item_uuid);

ALTER TABLE ONLY public.item_catalog
    ADD CONSTRAINT item_catalog_item_name_key UNIQUE (item_name);

ALTER TABLE ONLY public.item_class_eligibility
    ADD CONSTRAINT item_class_eligibility_pkey PRIMARY KEY (item_uuid, class_uuid);

ALTER TABLE ONLY public.race
    ADD CONSTRAINT race_pkey PRIMARY KEY (race_uuid);

ALTER TABLE ONLY public.race
    ADD CONSTRAINT race_name_key UNIQUE (name);

ALTER TABLE ONLY public.subclass
    ADD CONSTRAINT subclass_pkey PRIMARY KEY (subclass_uuid);

ALTER TABLE ONLY public.subclass
    ADD CONSTRAINT subclass_name_key UNIQUE (name);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_uuid);

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);

ALTER TABLE ONLY public.users_characters
    ADD CONSTRAINT users_characters_pkey PRIMARY KEY (user_uuid, character_uuid);

-- Create indexes (matching original schema exactly)
CREATE INDEX idx_item_catalog_equippable ON public.item_catalog USING btree (equippable);
CREATE INDEX idx_item_catalog_name ON public.item_catalog USING btree (item_name);
CREATE INDEX idx_item_catalog_name_trgm ON public.item_catalog USING gin (item_name public.gin_trgm_ops);
CREATE INDEX idx_item_catalog_rarity ON public.item_catalog USING btree (item_rarity);
CREATE INDEX idx_item_class_eligibility_class_uuid ON public.item_class_eligibility USING btree (class_uuid);
CREATE INDEX idx_item_class_eligibility_item_uuid ON public.item_class_eligibility USING btree (item_uuid);

-- Add foreign key constraints (matching original schema exactly)
ALTER TABLE ONLY public.character_class
    ADD CONSTRAINT character_class_char_info_uuid_fkey FOREIGN KEY (char_info_uuid) REFERENCES public.characters_info(char_info_uuid) ON DELETE CASCADE;

ALTER TABLE ONLY public.character_class
    ADD CONSTRAINT character_class_class_uuid_fkey FOREIGN KEY (class_uuid) REFERENCES public.class(class_uuid);

ALTER TABLE ONLY public.character_class
    ADD CONSTRAINT character_class_subclass_uuid_fkey FOREIGN KEY (subclass_uuid) REFERENCES public.subclass(subclass_uuid);

ALTER TABLE ONLY public.characters_info
    ADD CONSTRAINT characters_info_background_uuid_fkey FOREIGN KEY (background_uuid) REFERENCES public.background(background_uuid);

ALTER TABLE ONLY public.characters_info
    ADD CONSTRAINT characters_info_race_uuid_fkey FOREIGN KEY (race_uuid) REFERENCES public.race(race_uuid);

ALTER TABLE ONLY public.item_class_eligibility
    ADD CONSTRAINT item_class_eligibility_class_uuid_fkey FOREIGN KEY (class_uuid) REFERENCES public.class(class_uuid) ON DELETE CASCADE;

ALTER TABLE ONLY public.item_class_eligibility
    ADD CONSTRAINT item_class_eligibility_item_uuid_fkey FOREIGN KEY (item_uuid) REFERENCES public.item_catalog(item_uuid) ON DELETE CASCADE;

ALTER TABLE ONLY public.subclass
    ADD CONSTRAINT subclass_class_source_fkey FOREIGN KEY (class_source) REFERENCES public.class(class_uuid);

ALTER TABLE ONLY public.users_characters
    ADD CONSTRAINT users_characters_character_uuid_fkey FOREIGN KEY (character_uuid) REFERENCES public.characters_info(char_info_uuid);

ALTER TABLE ONLY public.users_characters
    ADD CONSTRAINT users_characters_user_uuid_fkey FOREIGN KEY (user_uuid) REFERENCES public.users(user_uuid);

-- NOTE: Original schema is missing FK constraint for character_has_item_slot.item_uuid -> item_catalog.item_uuid
-- This is intentionally omitted to match the original schema exactly

-- Insert test data

-- Insert Users
INSERT INTO users (username, password, enabled) VALUES
('dungeon_master', '$2a$10$N.zmdr9k7uOCQb1TPyq.FuxHGH8dCQ1K6CYG8/AE.b', true),
('player1', '$2a$10$N.zmdr9k7uOCQb1TPyq.FuxHGH8dCQ1K6CYG8/AE.c', true),
('player2', '$2a$10$N.zmdr9k7uOCQb1TPyq.FuxHGH8dCQ1K6CYG8/AE.d', true),
('player3', '$2a$10$N.zmdr9k7uOCQb1TPyq.FuxHGH8dCQ1K6CYG8/AE.e', true);

-- Insert Authorities
INSERT INTO authorities (username, authority) VALUES
('dungeon_master', 'ROLE_ADMIN'),
('dungeon_master', 'ROLE_USER'),
('player1', 'ROLE_USER'),
('player2', 'ROLE_USER'),
('player3', 'ROLE_USER');

-- Insert Races
INSERT INTO race (name, stat_increases) VALUES
('Human', '{"strength": 1, "dexterity": 1, "constitution": 1, "intelligence": 1, "wisdom": 1, "charisma": 1}'::json),
('Elf', '{"dexterity": 2}'::json),
('Dwarf', '{"constitution": 2}'::json),
('Halfling', '{"dexterity": 2}'::json),
('Dragonborn', '{"strength": 2, "charisma": 1}'::json),
('Gnome', '{"intelligence": 2}'::json),
('Half-Elf', '{"charisma": 2}'::json),
('Half-Orc', '{"strength": 2, "constitution": 1}'::json),
('Tiefling', '{"intelligence": 1, "charisma": 2}'::json);

-- Insert Backgrounds
INSERT INTO background (name, description, starting_gold) VALUES
('Acolyte', 'You have spent your life in the service of a temple to a specific god or pantheon of gods.', 15),
('Criminal', 'You are an experienced criminal with a history of breaking the law.', 15),
('Folk Hero', 'You come from a humble social rank, but you are destined for so much more.', 10),
('Noble', 'You understand wealth, power, and privilege.', 25),
('Sage', 'You spent years learning the lore of the multiverse.', 10),
('Soldier', 'War has been your life for as long as you care to remember.', 10),
('Charlatan', 'You have always had a way with people and know what makes them tick.', 15),
('Entertainer', 'You thrive in front of an audience and know how to entrance them.', 15),
('Guild Artisan', 'You are a member of an artisan''s guild, skilled in a particular field.', 20),
('Hermit', 'You lived in seclusion for a formative part of your life.', 5);

-- Insert Classes
INSERT INTO class (name, hit_dice_value, description, class_name) VALUES
('Fighter', 'D10', 'A master of martial combat, skilled with a variety of weapons and armor.', 'Fighter'),
('Wizard', 'D6', 'A scholarly magic-user capable of manipulating structures of reality.', 'Wizard'),
('Rogue', 'D8', 'A scoundrel who uses stealth and trickery to accomplish goals.', 'Rogue'),
('Cleric', 'D8', 'A priestly champion who wields divine magic in service of a higher power.', 'Cleric'),
('Ranger', 'D10', 'A warrior of the wilderness, skilled in tracking, survival, and combat.', 'Ranger'),
('Paladin', 'D10', 'A holy warrior bound to a sacred oath to fight against the forces of evil.', 'Paladin'),
('Barbarian', 'D12', 'A fierce warrior of primitive background who can enter a battle rage.', 'Barbarian'),
('Bard', 'D8', 'A performer whose music works magic—literally, weaving spells into song.', 'Bard'),
('Druid', 'D8', 'A priest of nature, wielding elemental forces and transformative magic.', 'Druid'),
('Monk', 'D8', 'A martial artist who harnesses inner power to achieve physical perfection.', 'Monk'),
('Sorcerer', 'D6', 'A spellcaster who draws on inherent magic from a gift or bloodline.', 'Sorcerer'),
('Warlock', 'D8', 'A wielder of magic derived from a bargain with an extraplanar entity.', 'Warlock');

-- Insert Subclasses
INSERT INTO subclass (name, class_source) 
SELECT 'Champion', class_uuid FROM class WHERE name = 'Fighter'
UNION ALL
SELECT 'Battle Master', class_uuid FROM class WHERE name = 'Fighter'
UNION ALL
SELECT 'School of Evocation', class_uuid FROM class WHERE name = 'Wizard'
UNION ALL
SELECT 'School of Abjuration', class_uuid FROM class WHERE name = 'Wizard'
UNION ALL
SELECT 'Thief', class_uuid FROM class WHERE name = 'Rogue'
UNION ALL
SELECT 'Assassin', class_uuid FROM class WHERE name = 'Rogue'
UNION ALL
SELECT 'Life Domain', class_uuid FROM class WHERE name = 'Cleric'
UNION ALL
SELECT 'War Domain', class_uuid FROM class WHERE name = 'Cleric'
UNION ALL
SELECT 'Hunter', class_uuid FROM class WHERE name = 'Ranger'
UNION ALL
SELECT 'Beast Master', class_uuid FROM class WHERE name = 'Ranger'
UNION ALL
SELECT 'Oath of Devotion', class_uuid FROM class WHERE name = 'Paladin'
UNION ALL
SELECT 'Oath of Vengeance', class_uuid FROM class WHERE name = 'Paladin';

-- Insert Item Catalog
INSERT INTO item_catalog (item_name, item_description, item_weight, item_value, attackable, equippable, item_equippable_type, item_rarity) VALUES
('Longsword', 'A versatile martial weapon with a straight double-edged blade.', 3, 15, true, true, '{weapon}', 'common'),
('Leather Armor', 'The breastplate and shoulder protectors of this armor are made of leather.', 10, 10, false, true, '{armor}', 'common'),
('Shield', 'A shield is made from wood or metal and is carried in one hand.', 6, 10, false, true, '{shield}', 'common'),
('Shortbow', 'A simple ranged weapon made of flexible wood.', 2, 25, true, true, '{weapon}', 'common'),
('Dagger', 'A simple light melee weapon that can also be thrown.', 1, 2, true, true, '{weapon}', 'common'),
('Chain Mail', 'Made of interlocking metal rings, chain mail includes a layer of quilted fabric.', 55, 75, false, true, '{armor}', 'common'),
('Battleaxe', 'A versatile martial weapon with a broad, sharp blade.', 4, 10, true, true, '{weapon}', 'common'),
('Crossbow, Light', 'A simple ranged weapon that fires crossbow bolts.', 5, 25, true, true, '{weapon}', 'common'),
('Rapier', 'A finesse martial weapon with a thin, sharp blade.', 2, 25, true, true, '{weapon}', 'common'),
('Studded Leather', 'Made from tough but flexible leather reinforced with close-set rivets.', 13, 45, false, true, '{armor}', 'common'),
('Warhammer', 'A versatile martial weapon with a heavy metal head.', 2, 15, true, true, '{weapon}', 'common'),
('Scimitar', 'A finesse light martial weapon with a curved blade.', 3, 25, true, true, '{weapon}', 'common'),
('Spellbook', 'Essential for wizards, this leather-bound tome contains spells.', 3, 50, false, false, '{}', 'common'),
('Thieves'' Tools', 'This set of tools includes a small file, lock picks, and more.', 1, 25, false, false, '{}', 'common'),
('Healing Potion', 'A magical red liquid that restores hit points when consumed.', 0, 50, false, false, '{}', 'common'),
('+1 Sword', 'A magical longsword with a +1 enhancement to attack and damage rolls.', 3, 1000, true, true, '{weapon}', 'uncommon'),
('Cloak of Elvenkind', 'This cloak grants advantage on Dexterity (Stealth) checks.', 1, 5000, false, true, '{cloak}', 'uncommon'),
('Bag of Holding', 'This bag can hold much more than its size would suggest.', 15, 4000, false, false, '{}', 'uncommon'),
('Ring of Protection', 'You gain a +1 bonus to AC and saving throws while wearing this ring.', 0, 3500, false, true, '{ring}', 'rare'),
('Flame Tongue', 'This magic sword''s blade erupts in flames when activated.', 3, 5000, true, true, '{weapon}', 'rare');

-- Insert Characters with corrected JSON field names (camelCase)
INSERT INTO characters_info (char_info_uuid, name, inspiration, race_uuid, background_uuid, ability_scores, hp_handler, death_saving_throws)
SELECT 
    'eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9'::uuid,
    'Thorin Ironbeard', 
    false,
    r.race_uuid,
    b.background_uuid,
    '{"strength": 16, "dexterity": 12, "constitution": 15, "intelligence": 10, "wisdom": 13, "charisma": 8}'::json,
    '{"maxHp": 12, "currentHp": 12, "temporaryHp": 0}'::json,
    '{"successes": 0, "failures": 0}'::json
FROM race r, background b 
WHERE r.name = 'Dwarf' AND b.name = 'Soldier'
UNION ALL
SELECT 
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid,
    'Elaria Moonwhisper', 
    true,
    r.race_uuid,
    b.background_uuid,
    '{"strength": 8, "dexterity": 16, "constitution": 12, "intelligence": 15, "wisdom": 14, "charisma": 13}'::json,
    '{"maxHp": 8, "currentHp": 6, "temporaryHp": 2}'::json,
    '{"successes": 0, "failures": 0}'::json
FROM race r, background b 
WHERE r.name = 'Elf' AND b.name = 'Sage'
UNION ALL
SELECT 
    'b2c3d4e5-f6a7-8901-bcde-f12345678901'::uuid,
    'Pip Lightfinger', 
    false,
    r.race_uuid,
    b.background_uuid,
    '{"strength": 10, "dexterity": 17, "constitution": 14, "intelligence": 12, "wisdom": 13, "charisma": 11}'::json,
    '{"maxHp": 10, "currentHp": 10, "temporaryHp": 0}'::json,
    '{"successes": 0, "failures": 0}'::json
FROM race r, background b 
WHERE r.name = 'Halfling' AND b.name = 'Criminal'
UNION ALL
SELECT 
    'c3d4e5f6-a7b8-9012-cdef-123456789012'::uuid,
    'Sir Gareth the Bold', 
    true,
    r.race_uuid,
    b.background_uuid,
    '{"strength": 15, "dexterity": 10, "constitution": 14, "intelligence": 11, "wisdom": 12, "charisma": 16}'::json,
    '{"maxHp": 14, "currentHp": 14, "temporaryHp": 0}'::json,
    '{"successes": 0, "failures": 0}'::json
FROM race r, background b 
WHERE r.name = 'Human' AND b.name = 'Noble'
UNION ALL
SELECT 
    'd4e5f6a7-b8c9-0123-def0-234567890123'::uuid,
    'Zara Flameheart', 
    false,
    r.race_uuid,
    b.background_uuid,
    '{"strength": 13, "dexterity": 12, "constitution": 13, "intelligence": 10, "wisdom": 15, "charisma": 16}'::json,
    '{"maxHp": 9, "currentHp": 9, "temporaryHp": 0}'::json,
    '{"successes": 0, "failures": 0}'::json
FROM race r, background b 
WHERE r.name = 'Tiefling' AND b.name = 'Entertainer';

-- Insert Character Classes using specific UUIDs
INSERT INTO character_class (char_info_uuid, class_uuid, subclass_uuid, level, hit_dice_remaining)
SELECT 
    'eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9'::uuid, 
    c.class_uuid, 
    s.subclass_uuid, 
    2, 
    2
FROM class c, subclass s
WHERE c.name = 'Fighter' AND s.name = 'Champion'
UNION ALL
SELECT 
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid, 
    c.class_uuid, 
    s.subclass_uuid, 
    1, 
    1
FROM class c, subclass s
WHERE c.name = 'Wizard' AND s.name = 'School of Evocation'
UNION ALL
SELECT 
    'b2c3d4e5-f6a7-8901-bcde-f12345678901'::uuid, 
    c.class_uuid, 
    s.subclass_uuid, 
    2, 
    1
FROM class c, subclass s
WHERE c.name = 'Rogue' AND s.name = 'Thief'
UNION ALL
SELECT 
    'c3d4e5f6-a7b8-9012-cdef-123456789012'::uuid, 
    c.class_uuid, 
    s.subclass_uuid, 
    3, 
    3
FROM class c, subclass s
WHERE c.name = 'Paladin' AND s.name = 'Oath of Devotion'
UNION ALL
SELECT 
    'd4e5f6a7-b8c9-0123-def0-234567890123'::uuid, 
    c.class_uuid, 
    NULL, 
    1, 
    1
FROM class c
WHERE c.name = 'Bard';

-- Insert Item Class Eligibility (which classes can use which items)
INSERT INTO item_class_eligibility (item_uuid, class_uuid)
SELECT i.item_uuid, c.class_uuid
FROM item_catalog i, class c
WHERE i.item_name = 'Longsword' AND c.name IN ('Fighter', 'Paladin', 'Ranger')
UNION ALL
SELECT i.item_uuid, c.class_uuid
FROM item_catalog i, class c
WHERE i.item_name = 'Shortbow' AND c.name IN ('Fighter', 'Ranger', 'Rogue')
UNION ALL
SELECT i.item_uuid, c.class_uuid
FROM item_catalog i, class c
WHERE i.item_name = 'Dagger' AND c.name IN ('Fighter', 'Rogue', 'Wizard', 'Bard')
UNION ALL
SELECT i.item_uuid, c.class_uuid
FROM item_catalog i, class c
WHERE i.item_name = 'Spellbook' AND c.name = 'Wizard'
UNION ALL
SELECT i.item_uuid, c.class_uuid
FROM item_catalog i, class c
WHERE i.item_name = 'Thieves'' Tools' AND c.name = 'Rogue';

-- Link users to characters using the specific UUIDs
INSERT INTO users_characters (user_uuid, character_uuid)
SELECT u.user_uuid, 'eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9'::uuid
FROM users u
WHERE u.username = 'player1'
UNION ALL
SELECT u.user_uuid, 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid
FROM users u
WHERE u.username = 'player1'
UNION ALL
SELECT u.user_uuid, 'b2c3d4e5-f6a7-8901-bcde-f12345678901'::uuid
FROM users u
WHERE u.username = 'player2'
UNION ALL
SELECT u.user_uuid, 'c3d4e5f6-a7b8-9012-cdef-123456789012'::uuid
FROM users u
WHERE u.username = 'player3'
UNION ALL
SELECT u.user_uuid, 'd4e5f6a7-b8c9-0123-def0-234567890123'::uuid
FROM users u
WHERE u.username = 'player3';

-- Insert some character inventory items using specific character UUIDs
INSERT INTO character_has_item_slot (character_uuid, container_uuid, item_uuid, attuned, equipped, in_attack_tab, quantity)
SELECT 
    'eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9'::uuid,
    uuid_generate_v4(),
    i.item_uuid,
    false,
    true,
    true,
    1
FROM item_catalog i
WHERE i.item_name = 'Battleaxe'
UNION ALL
SELECT 
    'eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9'::uuid,
    uuid_generate_v4(),
    i.item_uuid,
    false,
    true,
    false,
    1
FROM item_catalog i
WHERE i.item_name = 'Chain Mail'
UNION ALL
SELECT 
    'eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9'::uuid,
    uuid_generate_v4(),
    i.item_uuid,
    false,
    false,
    false,
    3
FROM item_catalog i
WHERE i.item_name = 'Healing Potion'
UNION ALL
SELECT 
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid,
    uuid_generate_v4(),
    i.item_uuid,
    false,
    false,
    true,
    1
FROM item_catalog i
WHERE i.item_name = 'Dagger'
UNION ALL
SELECT 
    'a1b2c3d4-e5f6-7890-abcd-ef1234567890'::uuid,
    uuid_generate_v4(),
    i.item_uuid,
    false,
    false,
    false,
    1
FROM item_catalog i
WHERE i.item_name = 'Spellbook'
UNION ALL
SELECT 
    'b2c3d4e5-f6a7-8901-bcde-f12345678901'::uuid,
    uuid_generate_v4(),
    i.item_uuid,
    false,
    true,
    false,
    1
FROM item_catalog i
WHERE i.item_name = 'Leather Armor'
UNION ALL
SELECT 
    'b2c3d4e5-f6a7-8901-bcde-f12345678901'::uuid,
    uuid_generate_v4(),
    i.item_uuid,
    false,
    false,
    false,
    1
FROM item_catalog i
WHERE i.item_name = 'Thieves'' Tools'
UNION ALL
SELECT 
    'c3d4e5f6-a7b8-9012-cdef-123456789012'::uuid,
    uuid_generate_v4(),
    i.item_uuid,
    true,
    true,
    true,
    1
FROM item_catalog i
WHERE i.item_name = '+1 Sword'
UNION ALL
SELECT 
    'c3d4e5f6-a7b8-9012-cdef-123456789012'::uuid,
    uuid_generate_v4(),
    i.item_uuid,
    false,
    true,
    false,
    1
FROM item_catalog i
WHERE i.item_name = 'Shield';