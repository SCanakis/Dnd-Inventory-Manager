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

-- Add the container table (matching your actual schema)
CREATE TABLE public.container (
    char_uuid uuid NOT NULL,
    container_uuid uuid NOT NULL,
    current_consumed integer NOT NULL,
    item_uuid uuid,
    max_capacity integer NOT NULL,
    CONSTRAINT container_pkey PRIMARY KEY (char_uuid, container_uuid)
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
    is_container boolean DEFAULT false,
    capacity integer,
    CONSTRAINT item_catalog_item_value_check CHECK ((item_value >= 0)),
    CONSTRAINT item_catalog_item_weight_check CHECK ((item_weight >= 0)),
    CONSTRAINT item_catalog_capacity_check CHECK ((capacity IS NULL OR capacity >= 0))
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

-- Add foreign key constraint for container table
ALTER TABLE ONLY public.container
    ADD CONSTRAINT container_char_uuid_fkey FOREIGN KEY (char_uuid) REFERENCES public.characters_info(char_info_uuid) ON DELETE CASCADE;

-- Add foreign key constraint for character_has_item_slot to container
ALTER TABLE ONLY public.character_has_item_slot
    ADD CONSTRAINT character_has_item_slot_container_fkey FOREIGN KEY (character_uuid, container_uuid) REFERENCES public.container(char_uuid, container_uuid) ON DELETE CASCADE;

-- Insert test data with explicit UUID literals

-- Insert Users with explicit UUIDs
INSERT INTO users (user_uuid, username, password, enabled) VALUES
('11111111-1111-1111-1111-111111111111', 'dungeon_master', '$2a$10$N.zmdr9k7uOCQb1TPyq.FuxHGH8dCQ1K6CYG8/AE.b', true),
('22222222-2222-2222-2222-222222222222', 'player1', '$2a$10$N.zmdr9k7uOCQb1TPyq.FuxHGH8dCQ1K6CYG8/AE.c', true),
('33333333-3333-3333-3333-333333333333', 'player2', '$2a$10$N.zmdr9k7uOCQb1TPyq.FuxHGH8dCQ1K6CYG8/AE.d', true),
('44444444-4444-4444-4444-444444444444', 'player3', '$2a$10$N.zmdr9k7uOCQb1TPyq.FuxHGH8dCQ1K6CYG8/AE.e', true);

-- Insert Authorities
INSERT INTO authorities (username, authority) VALUES
('dungeon_master', 'ROLE_ADMIN'),
('dungeon_master', 'ROLE_USER'),
('player1', 'ROLE_USER'),
('player2', 'ROLE_USER'),
('player3', 'ROLE_USER');

-- Insert Races with explicit UUIDs
INSERT INTO race (race_uuid, name, stat_increases) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Human', '{"strength": 1, "dexterity": 1, "constitution": 1, "intelligence": 1, "wisdom": 1, "charisma": 1}'::json),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Elf', '{"dexterity": 2}'::json),
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Dwarf', '{"constitution": 2}'::json),
('dddddddd-dddd-dddd-dddd-dddddddddddd', 'Halfling', '{"dexterity": 2}'::json),
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Dragonborn', '{"strength": 2, "charisma": 1}'::json),
('ffffffff-ffff-ffff-ffff-ffffffffffff', 'Gnome', '{"intelligence": 2}'::json),
('aaaabbbb-aaaa-bbbb-aaaa-bbbbaaaabbbb', 'Half-Elf', '{"charisma": 2}'::json),
('bbbbcccc-bbbb-cccc-bbbb-ccccbbbbcccc', 'Half-Orc', '{"strength": 2, "constitution": 1}'::json),
('ccccdddd-cccc-dddd-cccc-ddddccccdddd', 'Tiefling', '{"intelligence": 1, "charisma": 2}'::json);

-- Insert Backgrounds with explicit UUIDs
INSERT INTO background (background_uuid, name, description, starting_gold) VALUES
('10000000-0000-0000-0000-000000000001', 'Acolyte', 'You have spent your life in the service of a temple to a specific god or pantheon of gods.', 15),
('10000000-0000-0000-0000-000000000002', 'Criminal', 'You are an experienced criminal with a history of breaking the law.', 15),
('10000000-0000-0000-0000-000000000003', 'Folk Hero', 'You come from a humble social rank, but you are destined for so much more.', 10),
('10000000-0000-0000-0000-000000000004', 'Noble', 'You understand wealth, power, and privilege.', 25),
('10000000-0000-0000-0000-000000000005', 'Sage', 'You spent years learning the lore of the multiverse.', 10),
('10000000-0000-0000-0000-000000000006', 'Soldier', 'War has been your life for as long as you care to remember.', 10),
('10000000-0000-0000-0000-000000000007', 'Charlatan', 'You have always had a way with people and know what makes them tick.', 15),
('10000000-0000-0000-0000-000000000008', 'Entertainer', 'You thrive in front of an audience and know how to entrance them.', 15),
('10000000-0000-0000-0000-000000000009', 'Guild Artisan', 'You are a member of an artisan''s guild, skilled in a particular field.', 20),
('10000000-0000-0000-0000-00000000000a', 'Hermit', 'You lived in seclusion for a formative part of your life.', 5);

-- Insert Classes with explicit UUIDs
INSERT INTO class (class_uuid, name, hit_dice_value, description, class_name) VALUES
('c1a55000-0000-0000-0000-000000000001', 'Fighter', 'D10', 'A master of martial combat, skilled with a variety of weapons and armor.', 'Fighter'),
('c1a55000-0000-0000-0000-000000000002', 'Wizard', 'D6', 'A scholarly magic-user capable of manipulating structures of reality.', 'Wizard'),
('c1a55000-0000-0000-0000-000000000003', 'Rogue', 'D8', 'A scoundrel who uses stealth and trickery to accomplish goals.', 'Rogue'),
('c1a55000-0000-0000-0000-000000000004', 'Cleric', 'D8', 'A priestly champion who wields divine magic in service of a higher power.', 'Cleric'),
('c1a55000-0000-0000-0000-000000000005', 'Ranger', 'D10', 'A warrior of the wilderness, skilled in tracking, survival, and combat.', 'Ranger'),
('c1a55000-0000-0000-0000-000000000006', 'Paladin', 'D10', 'A holy warrior bound to a sacred oath to fight against the forces of evil.', 'Paladin'),
('c1a55000-0000-0000-0000-000000000007', 'Barbarian', 'D12', 'A fierce warrior of primitive background who can enter a battle rage.', 'Barbarian'),
('c1a55000-0000-0000-0000-000000000008', 'Bard', 'D8', 'A performer whose music works magic—literally, weaving spells into song.', 'Bard'),
('c1a55000-0000-0000-0000-000000000009', 'Druid', 'D8', 'A priest of nature, wielding elemental forces and transformative magic.', 'Druid'),
('c1a55000-0000-0000-0000-00000000000a', 'Monk', 'D8', 'A martial artist who harnesses inner power to achieve physical perfection.', 'Monk'),
('c1a55000-0000-0000-0000-00000000000b', 'Sorcerer', 'D6', 'A spellcaster who draws on inherent magic from a gift or bloodline.', 'Sorcerer'),
('c1a55000-0000-0000-0000-00000000000c', 'Warlock', 'D8', 'A wielder of magic derived from a bargain with an extraplanar entity.', 'Warlock');

-- Insert Subclasses with explicit UUIDs
INSERT INTO subclass (subclass_uuid, name, class_source) VALUES
('5cb00000-0000-0000-0000-000000000001', 'Champion', 'c1a55000-0000-0000-0000-000000000001'),
('5cb00000-0000-0000-0000-000000000002', 'Battle Master', 'c1a55000-0000-0000-0000-000000000001'),
('5cb00000-0000-0000-0000-000000000003', 'School of Evocation', 'c1a55000-0000-0000-0000-000000000002'),
('5cb00000-0000-0000-0000-000000000004', 'School of Abjuration', 'c1a55000-0000-0000-0000-000000000002'),
('5cb00000-0000-0000-0000-000000000005', 'Thief', 'c1a55000-0000-0000-0000-000000000003'),
('5cb00000-0000-0000-0000-000000000006', 'Assassin', 'c1a55000-0000-0000-0000-000000000003'),
('5cb00000-0000-0000-0000-000000000007', 'Life Domain', 'c1a55000-0000-0000-0000-000000000004'),
('5cb00000-0000-0000-0000-000000000008', 'War Domain', 'c1a55000-0000-0000-0000-000000000004'),
('5cb00000-0000-0000-0000-000000000009', 'Hunter', 'c1a55000-0000-0000-0000-000000000005'),
('5cb00000-0000-0000-0000-00000000000a', 'Beast Master', 'c1a55000-0000-0000-0000-000000000005'),
('5cb00000-0000-0000-0000-00000000000b', 'Oath of Devotion', 'c1a55000-0000-0000-0000-000000000006'),
('5cb00000-0000-0000-0000-00000000000c', 'Oath of Vengeance', 'c1a55000-0000-0000-0000-000000000006');

-- Insert Item Catalog with explicit UUIDs
INSERT INTO item_catalog (item_uuid, item_name, item_description, item_weight, item_value, attackable, equippable, attunable, item_equippable_type, item_rarity, is_container, capacity) VALUES
('aaaa0000-0000-0000-0000-000000000001', 'Longsword', 'A versatile martial weapon with a straight double-edged blade.', 3, 15, true, true, false, '{mainhand,offhand,twohand}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-000000000002', 'Leather Armor', 'The breastplate and shoulder protectors of this armor are made of leather.', 10, 10, false, true, false, '{armor}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-000000000003', 'Shield', 'A shield is made from wood or metal and is carried in one hand.', 6, 10, false, true, false, '{offhand}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-000000000004', 'Shortbow', 'A simple ranged weapon made of flexible wood.', 2, 25, true, true, false, '{twohand}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-000000000005', 'Dagger', 'A simple light melee weapon that can also be thrown.', 1, 2, true, true, false, '{mainhand,offhand}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-000000000006', 'Chain Mail', 'Made of interlocking metal rings, chain mail includes a layer of quilted fabric.', 55, 75, false, true, false, '{armor}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-000000000007', 'Battleaxe', 'A versatile martial weapon with a broad, sharp blade.', 4, 10, true, true, false, '{mainhand,twohand}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-000000000008', 'Crossbow, Light', 'A simple ranged weapon that fires crossbow bolts.', 5, 25, true, true, false, '{mainhand}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-000000000009', 'Rapier', 'A finesse martial weapon with a thin, sharp blade.', 2, 25, true, true, false, '{mainhand}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-00000000000a', 'Studded Leather', 'Made from tough but flexible leather reinforced with close-set rivets.', 13, 45, false, true, false, '{armor}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-00000000000b', 'Warhammer', 'A versatile martial weapon with a heavy metal head.', 2, 15, true, true, false, '{mainhand,twohand}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-00000000000c', 'Scimitar', 'A finesse light martial weapon with a curved blade.', 3, 25, true, true, false, '{mainhand,offhand}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-00000000000d', 'Spellbook', 'Essential for wizards, this leather-bound tome contains spells.', 3, 50, false, false, false, '{}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-00000000000e', 'Thieves'' Tools', 'This set of tools includes a small file, lock picks, and more.', 1, 25, false, false, false, '{}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-00000000000f', 'Healing Potion', 'A magical red liquid that restores hit points when consumed.', 0, 50, false, false, false, '{}', 'common', false, NULL),
('aaaa0000-0000-0000-0000-000000000010', '+1 Sword', 'A magical longsword with a +1 enhancement to attack and damage rolls.', 3, 1000, true, true, true, '{mainhand,offhand,twohand}', 'uncommon', false, NULL),
('aaaa0000-0000-0000-0000-000000000011', 'Cloak of Elvenkind', 'This cloak grants advantage on Dexterity (Stealth) checks.', 1, 5000, false, true, true, '{cloak}', 'uncommon', false, NULL),
('aaaa0000-0000-0000-0000-000000000012', 'Bag of Holding', 'This bag can hold much more than its size would suggest.', 15, 4000, false, false, false, '{}', 'uncommon', true, 500),
('aaaa0000-0000-0000-0000-000000000013', 'Ring of Protection', 'You gain a +1 bonus to AC and saving throws while wearing this ring.', 0, 3500, false, true, true, '{ringl,ringr}', 'rare', false, NULL),
('aaaa0000-0000-0000-0000-000000000014', 'Flame Tongue', 'This magic sword''s blade erupts in flames when activated.', 3, 5000, true, true, true, '{mainhand,offhand,twohand}', 'rare', false, NULL),
-- Container items
('cccc0000-0000-0000-0000-000000000001', 'Belt Pouch', 'A small leather pouch that attaches to a belt.', 1, 5, false, true, false, '{belt}', 'common', true, 20),
('cccc0000-0000-0000-0000-000000000002', 'Backpack', 'A sturdy canvas pack for carrying gear.', 5, 10, false, false, false, '{}', 'common', true, 30),
('cccc0000-0000-0000-0000-000000000003', 'Hidden Pocket', 'A concealed pocket sewn into clothing.', 0, 15, false, false, false, '{}', 'common', true, 5),
('cccc0000-0000-0000-0000-000000000004', 'Noble Pack', 'An ornate traveling pack made of fine materials.', 8, 50, false, false, false, '{}', 'common', true, 40),
('cccc0000-0000-0000-0000-000000000005', 'Instrument Case', 'A protective case for musical instruments.', 3, 20, false, false, false, '{}', 'common', true, 15),
('cccc0000-0000-0000-0000-000000000006', 'Portable Hole', 'A magical cloth that opens to an extradimensional space.', 0, 20000, false, false, false, '{}', 'rare', true, 1000);

-- Insert Characters with explicit UUIDs
INSERT INTO characters_info (char_info_uuid, name, inspiration, race_uuid, background_uuid, ability_scores, hp_handler, death_saving_throws) VALUES
('eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9', 'Thorin Ironbeard', false, 'cccccccc-cccc-cccc-cccc-cccccccccccc', '10000000-0000-0000-0000-000000000006', '{"strength": 16, "dexterity": 12, "constitution": 15, "intelligence": 10, "wisdom": 13, "charisma": 8}'::json, '{"maxHp": 12, "currentHp": 12, "temporaryHp": 0}'::json, '{"successes": 0, "failures": 0}'::json),
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'Elaria Moonwhisper', true, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '10000000-0000-0000-0000-000000000005', '{"strength": 8, "dexterity": 16, "constitution": 12, "intelligence": 15, "wisdom": 14, "charisma": 13}'::json, '{"maxHp": 8, "currentHp": 6, "temporaryHp": 2}'::json, '{"successes": 0, "failures": 0}'::json),
('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'Pip Lightfinger', false, 'dddddddd-dddd-dddd-dddd-dddddddddddd', '10000000-0000-0000-0000-000000000002', '{"strength": 10, "dexterity": 17, "constitution": 14, "intelligence": 12, "wisdom": 13, "charisma": 11}'::json, '{"maxHp": 10, "currentHp": 10, "temporaryHp": 0}'::json, '{"successes": 0, "failures": 0}'::json),
('c3d4e5f6-a7b8-9012-cdef-123456789012', 'Sir Gareth the Bold', true, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '10000000-0000-0000-0000-000000000004', '{"strength": 15, "dexterity": 10, "constitution": 14, "intelligence": 11, "wisdom": 12, "charisma": 16}'::json, '{"maxHp": 14, "currentHp": 14, "temporaryHp": 0}'::json, '{"successes": 0, "failures": 0}'::json),
('d4e5f6a7-b8c9-0123-def0-234567890123', 'Zara Flameheart', false, 'ccccdddd-cccc-dddd-cccc-ddddccccdddd', '10000000-0000-0000-0000-000000000008', '{"strength": 13, "dexterity": 12, "constitution": 13, "intelligence": 10, "wisdom": 15, "charisma": 16}'::json, '{"maxHp": 9, "currentHp": 9, "temporaryHp": 0}'::json, '{"successes": 0, "failures": 0}'::json);

-- Insert Character Classes with explicit UUIDs
INSERT INTO character_class (char_info_uuid, class_uuid, subclass_uuid, level, hit_dice_remaining) VALUES
('eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9', 'c1a55000-0000-0000-0000-000000000001', '5cb00000-0000-0000-0000-000000000001', 2, 2),
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'c1a55000-0000-0000-0000-000000000002', '5cb00000-0000-0000-0000-000000000003', 1, 1),
('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'c1a55000-0000-0000-0000-000000000003', '5cb00000-0000-0000-0000-000000000005', 2, 1),
('c3d4e5f6-a7b8-9012-cdef-123456789012', 'c1a55000-0000-0000-0000-000000000006', '5cb00000-0000-0000-0000-00000000000b', 3, 3),
('d4e5f6a7-b8c9-0123-def0-234567890123', 'c1a55000-0000-0000-0000-000000000008', NULL, 1, 1);

-- Insert Item Class Eligibility with explicit UUIDs
INSERT INTO item_class_eligibility (item_uuid, class_uuid) VALUES
('aaaa0000-0000-0000-0000-000000000001', 'c1a55000-0000-0000-0000-000000000001'), -- Longsword -> Fighter
('aaaa0000-0000-0000-0000-000000000001', 'c1a55000-0000-0000-0000-000000000006'), -- Longsword -> Paladin
('aaaa0000-0000-0000-0000-000000000001', 'c1a55000-0000-0000-0000-000000000005'), -- Longsword -> Ranger
('aaaa0000-0000-0000-0000-000000000004', 'c1a55000-0000-0000-0000-000000000001'), -- Shortbow -> Fighter
('aaaa0000-0000-0000-0000-000000000004', 'c1a55000-0000-0000-0000-000000000005'), -- Shortbow -> Ranger
('aaaa0000-0000-0000-0000-000000000004', 'c1a55000-0000-0000-0000-000000000003'), -- Shortbow -> Rogue
('aaaa0000-0000-0000-0000-000000000005', 'c1a55000-0000-0000-0000-000000000001'), -- Dagger -> Fighter
('aaaa0000-0000-0000-0000-000000000005', 'c1a55000-0000-0000-0000-000000000003'), -- Dagger -> Rogue
('aaaa0000-0000-0000-0000-000000000005', 'c1a55000-0000-0000-0000-000000000002'), -- Dagger -> Wizard
('aaaa0000-0000-0000-0000-000000000005', 'c1a55000-0000-0000-0000-000000000008'), -- Dagger -> Bard
('aaaa0000-0000-0000-0000-00000000000d', 'c1a55000-0000-0000-0000-000000000002'), -- Spellbook -> Wizard
('aaaa0000-0000-0000-0000-00000000000e', 'c1a55000-0000-0000-0000-000000000003'); -- Thieves' Tools -> Rogue

-- Link users to characters using explicit UUIDs
INSERT INTO users_characters (user_uuid, character_uuid) VALUES
('22222222-2222-2222-2222-222222222222', 'eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9'), -- player1 -> Thorin
('22222222-2222-2222-2222-222222222222', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890'), -- player1 -> Elaria
('33333333-3333-3333-3333-333333333333', 'b2c3d4e5-f6a7-8901-bcde-f12345678901'), -- player2 -> Pip
('44444444-4444-4444-4444-444444444444', 'c3d4e5f6-a7b8-9012-cdef-123456789012'), -- player3 -> Sir Gareth
('44444444-4444-4444-4444-444444444444', 'd4e5f6a7-b8c9-0123-def0-234567890123'); -- player3 -> Zara

-- Insert containers for each character (main inventory + container items they own)
INSERT INTO container (char_uuid, container_uuid, current_consumed, item_uuid, max_capacity) VALUES
-- Thorin's containers
('eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9', '00000000-0000-0000-0000-000000000000', 59, NULL, 100), -- Main inventory: Battleaxe(4) + Chain Mail(55) = 59kg
('eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9', 'cccc0000-0000-0000-0000-000000000001', 2, 'cccc0000-0000-0000-0000-000000000001', 20),   -- Belt Pouch: 2 Daggers(2kg), item_uuid references the pouch itself

-- Elaria's containers  
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', '00000000-0000-0000-0000-000000000000', 19, NULL, 80),    -- Main inventory: Dagger(1) + Spellbook(3) + Bag of Holding(15) = 19kg
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'aaaa0000-0000-0000-0000-000000000012', 0, 'aaaa0000-0000-0000-0000-000000000012', 500),  -- Bag of Holding: 2 Healing Potions(0kg)

-- Pip's containers
('b2c3d4e5-f6a7-8901-bcde-f12345678901', '00000000-0000-0000-0000-000000000000', 13, NULL, 70),   -- Main inventory: Leather Armor(10) + Thieves' Tools(1) + Shortbow(2) = 13kg
('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'cccc0000-0000-0000-0000-000000000003', 0, 'cccc0000-0000-0000-0000-000000000003', 5),    -- Hidden Pocket: empty

-- Sir Gareth's containers
('c3d4e5f6-a7b8-9012-cdef-123456789012', '00000000-0000-0000-0000-000000000000', 17, NULL, 120),   -- Main inventory: +1 Sword(3) + Shield(6) + Noble Pack(8) = 17kg  
('c3d4e5f6-a7b8-9012-cdef-123456789012', 'cccc0000-0000-0000-0000-000000000004', 0, 'cccc0000-0000-0000-0000-000000000004', 40),   -- Noble's Pack: 5 Healing Potions(0kg)

-- Zara's containers
('d4e5f6a7-b8c9-0123-def0-234567890123', '00000000-0000-0000-0000-000000000000', 14, NULL, 90),   -- Main inventory: Dagger(1) + Leather Armor(10) + Instrument Case(3) = 14kg
('d4e5f6a7-b8c9-0123-def0-234567890123', 'cccc0000-0000-0000-0000-000000000005', 0, 'cccc0000-0000-0000-0000-000000000005', 15);   -- Instrument Case: empty

-- Insert character inventory items with corrected in_attack_tab values
-- NOTE: in_attack_tab is now NULL for non-attackable items instead of false
INSERT INTO character_has_item_slot (character_uuid, container_uuid, item_uuid, attuned, equipped, in_attack_tab, quantity) VALUES
-- Thorin's items
('eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9', '00000000-0000-0000-0000-000000000000', 'aaaa0000-0000-0000-0000-000000000007', false, true, true, 1), -- Battleaxe in main inventory (4kg) - ATTACKABLE
('eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9', '00000000-0000-0000-0000-000000000000', 'aaaa0000-0000-0000-0000-000000000006', false, true, NULL, 1), -- Chain Mail in main inventory (55kg) - NON-ATTACKABLE
('eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9', '00000000-0000-0000-0000-000000000000', 'aaaa0000-0000-0000-0000-00000000000f', false, false, NULL, 3), -- 3 Healing Potions in main inventory (0kg) - NON-ATTACKABLE
('eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9', '00000000-0000-0000-0000-000000000000', 'cccc0000-0000-0000-0000-000000000001', false, true, NULL, 1), -- Belt Pouch in main inventory (1kg) - NON-ATTACKABLE CONTAINER ITEM
('eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9', 'cccc0000-0000-0000-0000-000000000001', 'aaaa0000-0000-0000-0000-000000000005', false, false, false, 2), -- 2 Daggers in belt pouch (2kg) - ATTACKABLE BUT NOT IN ATTACK TAB

-- Elaria's items
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', '00000000-0000-0000-0000-000000000000', 'aaaa0000-0000-0000-0000-000000000005', false, false, true, 1), -- Dagger in main inventory (1kg) - ATTACKABLE
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', '00000000-0000-0000-0000-000000000000', 'aaaa0000-0000-0000-0000-00000000000d', false, false, NULL, 1), -- Spellbook in main inventory (3kg) - NON-ATTACKABLE
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', '00000000-0000-0000-0000-000000000000', 'aaaa0000-0000-0000-0000-000000000012', false, false, NULL, 1), -- Bag of Holding in main inventory (15kg) - NON-ATTACKABLE CONTAINER ITEM
('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'aaaa0000-0000-0000-0000-000000000012', 'aaaa0000-0000-0000-0000-00000000000f', false, false, NULL, 2), -- 2 Healing Potions in Bag of Holding (0kg) - NON-ATTACKABLE

-- Pip's items
('b2c3d4e5-f6a7-8901-bcde-f12345678901', '00000000-0000-0000-0000-000000000000', 'aaaa0000-0000-0000-0000-000000000002', false, true, NULL, 1), -- Leather Armor in main inventory (10kg) - NON-ATTACKABLE
('b2c3d4e5-f6a7-8901-bcde-f12345678901', '00000000-0000-0000-0000-000000000000', 'aaaa0000-0000-0000-0000-00000000000e', false, false, NULL, 1), -- Thieves' Tools in main inventory (1kg) - NON-ATTACKABLE
('b2c3d4e5-f6a7-8901-bcde-f12345678901', '00000000-0000-0000-0000-000000000000', 'aaaa0000-0000-0000-0000-000000000004', false, false, false, 1), -- Shortbow in main inventory (2kg) - ATTACKABLE BUT NOT IN ATTACK TAB
('b2c3d4e5-f6a7-8901-bcde-f12345678901', '00000000-0000-0000-0000-000000000000', 'cccc0000-0000-0000-0000-000000000003', false, false, NULL, 1), -- Hidden Pocket in main inventory (0kg) - NON-ATTACKABLE CONTAINER ITEM

-- Sir Gareth's items
('c3d4e5f6-a7b8-9012-cdef-123456789012', '00000000-0000-0000-0000-000000000000', 'aaaa0000-0000-0000-0000-000000000010', true, true, true, 1), -- +1 Sword in main inventory (3kg) - ATTACKABLE
('c3d4e5f6-a7b8-9012-cdef-123456789012', '00000000-0000-0000-0000-000000000000', 'aaaa0000-0000-0000-0000-000000000003', false, true, NULL, 1), -- Shield in main inventory (6kg) - NON-ATTACKABLE
('c3d4e5f6-a7b8-9012-cdef-123456789012', '00000000-0000-0000-0000-000000000000', 'cccc0000-0000-0000-0000-000000000004', false, false, NULL, 1), -- Noble Pack in main inventory (8kg) - NON-ATTACKABLE CONTAINER ITEM
('c3d4e5f6-a7b8-9012-cdef-123456789012', 'cccc0000-0000-0000-0000-000000000004', 'aaaa0000-0000-0000-0000-00000000000f', false, false, NULL, 5), -- 5 Healing Potions in Noble's Pack (0kg) - NON-ATTACKABLE

-- Zara's items
('d4e5f6a7-b8c9-0123-def0-234567890123', '00000000-0000-0000-0000-000000000000', 'aaaa0000-0000-0000-0000-000000000005', false, false, false, 1), -- Dagger in main inventory (1kg) - ATTACKABLE BUT NOT IN ATTACK TAB
('d4e5f6a7-b8c9-0123-def0-234567890123', '00000000-0000-0000-0000-000000000000', 'aaaa0000-0000-0000-0000-000000000002', false, true, NULL, 1), -- Leather Armor in main inventory (10kg) - NON-ATTACKABLE
('d4e5f6a7-b8c9-0123-def0-234567890123', '00000000-0000-0000-0000-000000000000', 'cccc0000-0000-0000-0000-000000000005', false, false, NULL, 1); -- Instrument Case in main inventory (3kg) - NON-ATTACKABLE CONTAINER ITEM

-- =============================================================================
-- CHARACTER INVENTORY SUMMARY (UPDATED FOR ATTACK TAB LOGIC)
-- =============================================================================

-- THORIN IRONBEARD (Dwarf Fighter) - UUID: eb5a1cd2-97b3-4f2e-90d2-b1e99dfaeac9
-- Owner: player1 (22222222-2222-2222-2222-222222222222)
-- Class: Fighter (Champion), Level 2
-- Background: Soldier
-- 
-- CONTAINERS:
--   • Main Inventory: 59/100kg capacity
--     - Battleaxe (4kg) [EQUIPPED, IN ATTACK TAB] ✓ attackable=true, in_attack_tab=true
--     - Chain Mail (55kg) [EQUIPPED] ✓ attackable=false, in_attack_tab=NULL
--     - 3x Healing Potions (0kg) ✓ attackable=false, in_attack_tab=NULL
--     - Belt Pouch (1kg) [EQUIPPED] ✓ attackable=false, in_attack_tab=NULL - the container item itself
--   
--   • Belt Pouch: 2/20kg capacity (container UUID: cccc0000-0000-0000-0000-000000000001)
--     - 2x Daggers (2kg total) ✓ attackable=true, in_attack_tab=false (stored, not actively used)

-- ELARIA MOONWHISPER (Elf Wizard) - UUID: a1b2c3d4-e5f6-7890-abcd-ef1234567890
-- Owner: player1 (22222222-2222-2222-2222-222222222222)
-- Class: Wizard (School of Evocation), Level 1
-- Background: Sage
-- HP: 6/8 (2 temp HP)
-- 
-- CONTAINERS:
--   • Main Inventory: 19/80kg capacity
--     - Dagger (1kg) [IN ATTACK TAB] ✓ attackable=true, in_attack_tab=true
--     - Spellbook (3kg) ✓ attackable=false, in_attack_tab=NULL
--     - Bag of Holding (15kg) ✓ attackable=false, in_attack_tab=NULL - the container item itself
--   
--   • Bag of Holding: 0/500kg capacity (container UUID: aaaa0000-0000-0000-0000-000000000012)
--     - 2x Healing Potions (0kg total) ✓ attackable=false, in_attack_tab=NULL

-- PIP LIGHTFINGER (Halfling Rogue) - UUID: b2c3d4e5-f6a7-8901-bcde-f12345678901
-- Owner: player2 (33333333-3333-3333-3333-333333333333)
-- Class: Rogue (Thief), Level 2
-- Background: Criminal
-- 
-- CONTAINERS:
--   • Main Inventory: 13/70kg capacity
--     - Leather Armor (10kg) [EQUIPPED] ✓ attackable=false, in_attack_tab=NULL
--     - Thieves' Tools (1kg) ✓ attackable=false, in_attack_tab=NULL
--     - Shortbow (2kg) ✓ attackable=true, in_attack_tab=false (not actively used)
--     - Hidden Pocket (0kg) ✓ attackable=false, in_attack_tab=NULL - the container item itself
--   
--   • Hidden Pocket: 0/5kg capacity (container UUID: cccc0000-0000-0000-0000-000000000003)
--     - (empty)

-- SIR GARETH THE BOLD (Human Paladin) - UUID: c3d4e5f6-a7b8-9012-cdef-123456789012
-- Owner: player3 (44444444-4444-4444-4444-444444444444)
-- Class: Paladin (Oath of Devotion), Level 3
-- Background: Noble
-- 
-- CONTAINERS:
--   • Main Inventory: 17/120kg capacity
--     - +1 Sword (3kg) [EQUIPPED, ATTUNED, IN ATTACK TAB] ✓ attackable=true, in_attack_tab=true
--     - Shield (6kg) [EQUIPPED] ✓ attackable=false, in_attack_tab=NULL
--     - Noble Pack (8kg) ✓ attackable=false, in_attack_tab=NULL - the container item itself
--   
--   • Noble Pack: 0/40kg capacity (container UUID: cccc0000-0000-0000-0000-000000000004)
--     - 5x Healing Potions (0kg total) ✓ attackable=false, in_attack_tab=NULL

-- ZARA FLAMEHEART (Tiefling Bard) - UUID: d4e5f6a7-b8c9-0123-def0-234567890123
-- Owner: player3 (44444444-4444-4444-4444-444444444444)
-- Class: Bard, Level 1
-- Background: Entertainer
-- 
-- CONTAINERS:
--   • Main Inventory: 14/90kg capacity
--     - Dagger (1kg) ✓ attackable=true, in_attack_tab=false (not actively used)
--     - Leather Armor (10kg) [EQUIPPED] ✓ attackable=false, in_attack_tab=NULL
--     - Instrument Case (3kg) ✓ attackable=false, in_attack_tab=NULL - the container item itself
--   
--   • Instrument Case: 0/15kg capacity (container UUID: cccc0000-0000-0000-0000-000000000005)
--     - (empty)