package com.scanakispersonalprojects.dndapp.model.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.RollType;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ClassNameIdPair;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.EquippableType;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemCatalog;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.Rarity;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.Skill;

class ItemCatalogTest {

    private ItemCatalog itemCatalog;
    private UUID testUuid;

    @BeforeEach
    void setUp() {
        testUuid = UUID.randomUUID();
        itemCatalog = new ItemCatalog();
    }

    @Test
    void testDefaultConstructor() {
        assertNull(itemCatalog.getItemUuid());
        assertNull(itemCatalog.getItemName());
        assertNull(itemCatalog.getItemDescription());
        assertEquals(0, itemCatalog.getItemWeight());
        assertEquals(0, itemCatalog.getItemValue());
        assertEquals(Rarity.common, itemCatalog.getItemRarity());
        assertFalse(itemCatalog.isAttackable());
        assertFalse(itemCatalog.isEquippable());
        assertFalse(itemCatalog.isAttunable());
    }

    @Test
    void testFullConstructorWithoutClassPairs() {
        String itemName = "Magic Sword";
        String itemDescription = "A legendary blade";
        Integer itemWeight = 5;
        Integer itemValue = 1000;
        Rarity rarity = Rarity.legendary;
        boolean attackable = true;
        Short acBonus = (short) 2;
        Map<AbilityScore, Boolean> addToAc = Map.of(AbilityScore.dexterity, true);
        boolean equippable = true;
        boolean attunable = true;
        List<EquippableType> equippableTypes = Arrays.asList(EquippableType.mainhand);
        Map<AbilityScore, Integer> abilityReq = Map.of(AbilityScore.strength, 15);
        Map<Skill, RollType> skillRollType = Map.of(Skill.athletics, RollType.advantage);
        Map<Skill, Integer> skillBonus = Map.of(Skill.athletics, 2);

        ItemCatalog item = new ItemCatalog(testUuid, itemName, itemDescription, itemWeight,
                itemValue, rarity, attackable, acBonus, addToAc, equippable, attunable,
                equippableTypes, abilityReq, skillRollType, skillBonus, false, null);

        assertEquals(testUuid, item.getItemUuid());
        assertEquals(itemName, item.getItemName());
        assertEquals(itemDescription, item.getItemDescription());
        assertEquals(itemWeight, item.getItemWeight());
        assertEquals(itemValue, item.getItemValue());
        assertEquals(rarity, item.getItemRarity());
        assertTrue(item.isAttackable());
        assertEquals(acBonus, item.getAcBonus());
        assertEquals(addToAc, item.getAddToAc());
        assertTrue(item.isEquippable());
        assertTrue(item.isAttunable());
        assertEquals(equippableTypes, item.getItemEquippableTypes());
        assertEquals(abilityReq, item.getAbilityRequirement());
        assertEquals(skillRollType, item.getSkillAlteredRollType());
        assertEquals(skillBonus, item.getSkillAlteredBonus());
    }

    @Test
    void testFullConstructorWithClassPairs() {
        List<ClassNameIdPair> classPairs = new ArrayList<>() {{
                add(new ClassNameIdPair(UUID.randomUUID(),"Warrior"));
                add(new ClassNameIdPair(UUID.randomUUID(),"Paladin"));
        }};
                

        ItemCatalog item = new ItemCatalog(testUuid, "Test Item", "Description", 1, 100,
                Rarity.common, false, null, null, true, false, null, null, null, null, false, null,classPairs);

        assertEquals(classPairs, item.getClassNameIdPair());
        assertEquals(testUuid, item.getItemUuid());
        assertTrue(item.isEquippable());
        assertFalse(item.isAttunable());
    }

    @Test
    void testSettersAndGetters() {
        String newName = "Updated Item";
        String newDescription = "Updated description";
        Integer newWeight = 10;
        Integer newValue = 500;
        Rarity newRarity = Rarity.rare;
        Short newAcBonus = (short) 3;

        itemCatalog.setItemUuid(testUuid);
        itemCatalog.setItemName(newName);
        itemCatalog.setItemDescription(newDescription);
        itemCatalog.setItemWeight(newWeight);
        itemCatalog.setItemValue(newValue);
        itemCatalog.setItemRarity(newRarity);
        itemCatalog.setAttackable(true);
        itemCatalog.setAcBonus(newAcBonus);
        itemCatalog.setEquippable(true);
        itemCatalog.setAttunable(true);

        assertEquals(testUuid, itemCatalog.getItemUuid());
        assertEquals(newName, itemCatalog.getItemName());
        assertEquals(newDescription, itemCatalog.getItemDescription());
        assertEquals(newWeight, itemCatalog.getItemWeight());
        assertEquals(newValue, itemCatalog.getItemValue());
        assertEquals(newRarity, itemCatalog.getItemRarity());
        assertTrue(itemCatalog.isAttackable());
        assertEquals(newAcBonus, itemCatalog.getAcBonus());
        assertTrue(itemCatalog.isEquippable());
        assertTrue(itemCatalog.isAttunable());
    }

    @Test
    void testComplexFieldSettersAndGetters() {
        Map<AbilityScore, Boolean> addToAc = new HashMap<>();
        addToAc.put(AbilityScore.dexterity, true);
        addToAc.put(AbilityScore.wisdom, false);

        List<EquippableType> equippableTypes = Arrays.asList(
                EquippableType.mainhand, EquippableType.offhand
        );

        Map<AbilityScore, Integer> abilityReq = Map.of(
                AbilityScore.strength, 12,
                AbilityScore.constitution, 10
        );

        Map<Skill, RollType> skillRollTypes = Map.of(Skill.athletics, RollType.advantage);
        Map<Skill, Integer> skillBonuses = Map.of(Skill.athletics, 5);

        List<ClassNameIdPair> classPairs = Arrays.asList(
                new ClassNameIdPair(UUID.randomUUID(),"Rogue")
        );

        itemCatalog.setAddToAc(addToAc);
        itemCatalog.setItemEquippableTypes(equippableTypes);
        itemCatalog.setAbilityRequirement(abilityReq);
        itemCatalog.setSkillAlteredRollType(skillRollTypes);
        itemCatalog.setSkillAlteredBonus(skillBonuses);
        itemCatalog.setClassNameIdPair(classPairs);

        assertEquals(addToAc, itemCatalog.getAddToAc());
        assertEquals(equippableTypes, itemCatalog.getItemEquippableTypes());
        assertEquals(abilityReq, itemCatalog.getAbilityRequirement());
        assertEquals(skillRollTypes, itemCatalog.getSkillAlteredRollType());
        assertEquals(skillBonuses, itemCatalog.getSkillAlteredBonus());
        assertEquals(classPairs, itemCatalog.getClassNameIdPair());
    }

    @SuppressWarnings("unlikely-arg-type")
    @Test
    void testEqualsMethod() {
        ItemCatalog item1 = new ItemCatalog();
        ItemCatalog item2 = new ItemCatalog();
        
        item1.setItemUuid(testUuid);
        item2.setItemUuid(testUuid);
        
        assertTrue(item1.equals(item2));
        
        UUID differentUuid = UUID.randomUUID();
        item2.setItemUuid(differentUuid);
        assertFalse(item1.equals(item2));
        
        item1.setItemUuid(null);
        item2.setItemUuid(null);
        assertTrue(item1.equals(item2));
        
        assertFalse(item1.equals("not an ItemCatalog"));
        assertFalse(item1.equals(null));
    }

    @Test
    void testNullAndEdgeCases() {
        itemCatalog.setItemName(null);
        itemCatalog.setItemDescription(null);
        itemCatalog.setAcBonus(null);
        itemCatalog.setAddToAc(null);
        itemCatalog.setItemEquippableTypes(null);
        itemCatalog.setAbilityRequirement(null);
        itemCatalog.setSkillAlteredRollType(null);
        itemCatalog.setSkillAlteredBonus(null);
        itemCatalog.setClassNameIdPair(null);

        assertNull(itemCatalog.getItemName());
        assertNull(itemCatalog.getItemDescription());
        assertNull(itemCatalog.getAcBonus());
        assertNull(itemCatalog.getAddToAc());
        assertNull(itemCatalog.getItemEquippableTypes());
        assertNull(itemCatalog.getAbilityRequirement());
        assertNull(itemCatalog.getSkillAlteredRollType());
        assertNull(itemCatalog.getSkillAlteredBonus());
        assertNull(itemCatalog.getClassNameIdPair());

        itemCatalog.setItemWeight(Integer.MAX_VALUE);
        itemCatalog.setItemValue(Integer.MIN_VALUE);
        itemCatalog.setAcBonus(Short.MAX_VALUE);

        assertEquals(Integer.MAX_VALUE, itemCatalog.getItemWeight());
        assertEquals(Integer.MIN_VALUE, itemCatalog.getItemValue());
        assertEquals(Short.MAX_VALUE, itemCatalog.getAcBonus());
    }
}