package com.scanakispersonalprojects.dndapp.testutils.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.inventory.RollType;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.EquippableType;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemCatalog;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.Rarity;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.Skill;

public class ItemCatalogBuilder {
        private UUID itemUuid;
        private String name;
        private String description;
        private Double weight;
        private Integer cost;
        private Rarity rarity;
        private Boolean isWeapon;
        private Short damageAmount;
        private Map<AbilityScore, Boolean> damageTypes = new HashMap<>();
        private Boolean isEquippable;
        private Boolean isMagical;
        private List<EquippableType> equippableTypes = new ArrayList<>();
        private Map<AbilityScore, Integer> bonusAbilityScores = new HashMap<>();
        private Map<Skill, RollType> bonusSkillRollTypes = new HashMap<>();
        private Map<Skill, Integer> bonusSkillModifiers = new HashMap<>();
        
        public ItemCatalogBuilder itemUuid(UUID itemUuid) {
            this.itemUuid = itemUuid;
            return this;
        }
        
        public ItemCatalogBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public ItemCatalogBuilder description(String description) {
            this.description = description;
            return this;
        }
        
        public ItemCatalogBuilder weight(Double weight) {
            this.weight = weight;
            return this;
        }
        
        public ItemCatalogBuilder cost(Integer cost) {
            this.cost = cost;
            return this;
        }
        
        public ItemCatalogBuilder rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }
        
        public ItemCatalogBuilder isWeapon(Boolean isWeapon) {
            this.isWeapon = isWeapon;
            return this;
        }
        
        public ItemCatalogBuilder damageAmount(Short damageAmount) {
            this.damageAmount = damageAmount;
            return this;
        }
        
        public ItemCatalogBuilder damageType(AbilityScore abilityScore, Boolean value) {
            this.damageTypes.put(abilityScore, value);
            return this;
        }
        
        public ItemCatalogBuilder isEquippable(Boolean isEquippable) {
            this.isEquippable = isEquippable;
            return this;
        }
        
        public ItemCatalogBuilder isMagical(Boolean isMagical) {
            this.isMagical = isMagical;
            return this;
        }
        
        public ItemCatalogBuilder equippableType(EquippableType type) {
            this.equippableTypes.add(type);
            return this;
        }
        
        public ItemCatalogBuilder bonusAbilityScore(AbilityScore score, Integer bonus) {
            this.bonusAbilityScores.put(score, bonus);
            return this;
        }
        
        public ItemCatalogBuilder bonusSkillRollType(Skill skill, RollType rollType) {
            this.bonusSkillRollTypes.put(skill, rollType);
            return this;
        }
        
        public ItemCatalogBuilder bonusSkillModifier(Skill skill, Integer modifier) {
            this.bonusSkillModifiers.put(skill, modifier);
            return this;
        }
        
        public ItemCatalog build() {
            return new ItemCatalog(
                itemUuid,
                name,
                description,
                weight,
                cost,
                rarity,
                isWeapon,
                damageAmount,
                new HashMap<>(damageTypes),
                isEquippable,
                isMagical,
                new ArrayList<>(equippableTypes),
                new HashMap<>(bonusAbilityScores),
                new HashMap<>(bonusSkillRollTypes),
                new HashMap<>(bonusSkillModifiers),
                false,
                null
            );
        }
    }