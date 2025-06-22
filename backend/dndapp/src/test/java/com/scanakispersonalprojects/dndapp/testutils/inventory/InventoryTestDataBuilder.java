package com.scanakispersonalprojects.dndapp.testutils.inventory;

import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.inventory.RollType;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.EquippableType;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.Rarity;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.Skill;

public class InventoryTestDataBuilder {
    
    public static UserBuilder defaultUser() {
        return new UserBuilder()
        .username("testuser")
        .password("{noop}password");
    }

     public static UserBuilder adminUser() {
        return new UserBuilder()
            .username("adminuser")
            .password("{noop}adminpass");
    }
    
    // Item builders
    public static ItemCatalogBuilder longSword() {
        return new ItemCatalogBuilder()
            .name("LongSword")
            .description("A sharp, well-balanced blade")
            .weight(5)
            .cost(10)
            .rarity(Rarity.common)
            .isWeapon(true)
            .damageAmount((short) 2)
            .damageType(AbilityScore.strength, true)
            .isEquippable(true)
            .isMagical(false)
            .equippableType(EquippableType.mainhand)
            .equippableType(EquippableType.offhand)
            .equippableType(EquippableType.twohand)
            .bonusAbilityScore(AbilityScore.strength, 15)
            .bonusAbilityScore(AbilityScore.dexterity, 12)
            .bonusSkillRollType(Skill.acrobatics, RollType.advantage)
            .bonusSkillRollType(Skill.stealth, RollType.straight)
            .bonusSkillModifier(Skill.acrobatics, 3)
            .bonusSkillModifier(Skill.sleight_of_hand, 5);
    }

    public static ItemCatalogBuilder shield() {
        return new ItemCatalogBuilder()
            .name("Shield")
            .description("A sturdy wooden shield")
            .weight(3)
            .cost(15)
            .rarity(Rarity.common)
            .isWeapon(false)
            .isEquippable(true)
            .isMagical(false)
            .equippableType(EquippableType.offhand)
            .bonusAbilityScore(AbilityScore.constitution, 2);
    }
    
    public static ItemCatalogBuilder magicRing() {
        return new ItemCatalogBuilder()
            .name("Ring of Protection")
            .description("A magical ring that provides protection")
            .weight(0)
            .cost(500)
            .rarity(Rarity.rare)
            .isWeapon(false)
            .isEquippable(true)
            .isMagical(true)
            .equippableType(EquippableType.ringl)
            .bonusAbilityScore(AbilityScore.constitution, 5)
            .bonusSkillRollType(Skill.perception, RollType.advantage);
    }
    
    public static ItemCatalogBuilder potion() {
        return new ItemCatalogBuilder()
            .name("Health Potion")
            .description("Restores health when consumed")
            .weight(1)
            .cost(50)
            .rarity(Rarity.common)
            .isWeapon(false)
            .isEquippable(false)
            .isMagical(true);
    }
    
    // Inventory slot builders
    public static CharacterHasItemSlotBuilder itemSlot(UUID itemUuid, UUID charUuid) {
        return new CharacterHasItemSlotBuilder()
            .itemUuid(itemUuid)
            .characterUuid(charUuid)
            .slotUuid(UUID.randomUUID())
            .quantity(1)
            .isEquipped(false)
            .isAttuned(false)
            .isIdentified(true);
    }
    
    public static CharacterHasItemSlotBuilder equippedItemSlot(UUID itemUuid, UUID charUuid) {
        return itemSlot(itemUuid, charUuid)
            .isEquipped(true)
            .quantity(1);
    }
    
    public static CharacterHasItemSlotBuilder stackableItemSlot(UUID itemUuid, UUID charUuid, int quantity) {
        return itemSlot(itemUuid, charUuid)
            .quantity(quantity)
            .isEquipped(false);
    }

}
