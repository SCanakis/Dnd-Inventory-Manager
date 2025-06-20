package com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.AbilityScore;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.RollType;

/**
 * Entity representing an item in the D&D catalog system.
 * This is the master definition of all items that can exist in the game,
 * including weapons, armor, magic items, containers, and miscellaneous equipment.
 * 
 * Each catalog item defines the base properties, requirements, and mechanical
 * effects that apply when characters acquire or use the item. Individual
 * character inventories reference these catalog entries.
 * 
 * pk       item_uuid           UUID
 *          item_name           VARCHAR(50)
 *          item_description    TEXT
 *          item_weight         INT
 *          item_value          INT
 *          item_rarity         String.Enumeration
 *          attackable          boolean
 * 
 *          ac_bonus            short
 *          add_as_to_ac        json
 * 
 *          equippable          boolean
 *          attunaable          boolean
 * 
 *      item_equippable_type    String.array[] 
 *      ability_requirments     json
 *          
 *      skill_altered_roll_type json
 *      skill_altered_bonus     json
 * 
 *          is_container        boolean
 *          capactiy            int
 */


@Entity
@Table(name = "item_catalog")
public class ItemCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "item_uuid", columnDefinition = "UUID")
    private UUID itemUuid;

    @Column(name = "item_name", length = 50, nullable = false, unique = true)
    private String itemName;

    @Column(name="item_description", columnDefinition = "TEXT", nullable = false)
    private String itemDescription;

    

    @Column(name = "item_weight", nullable = false)
    private Integer itemWeight = 0;

    @Column(name = "item_value", nullable = false)
    private Integer itemValue = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_rarity", nullable = false)
    private Rarity itemRarity = Rarity.common;

    @Column(name = "attackable", nullable = false)
    private boolean attackable = false;



    @Column(name = "ac_bonus")
    private Short acBonus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "add_as_to_ac", columnDefinition = "json")
    private Map<AbilityScore, Boolean> addToAc;


    @Column(name = "equippable", nullable = false)
    private boolean equippable = false;

    @Column(name = "attunable", nullable = false)
    private boolean attunable = false;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "item_equippable_type", columnDefinition = "varchar[]")
    @Enumerated(EnumType.STRING)
    private List<EquippableType> itemEquippableTypes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ability_requirment", columnDefinition = "json")
    private Map<AbilityScore, Integer> abilityRequirement;
    
    

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skill_altered_roll_type")
    private Map<Skill, RollType> skillAlteredRollType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "skill_altered_bonus")
    private Map<Skill, Integer> skillAlteredBonus;

    @Column(name = "is_container")
    private boolean isContainer = false;

    @Column(name = "capacity")
    private Integer capacity;

    @Transient
    private List<ClassNameIdPair> classNameIdPair;

    public ItemCatalog() {}

    public ItemCatalog(UUID itemUuid, String itemName, String itemDescription, Integer itemWeight, Integer itemValue,
            Rarity itemRarity, boolean attackable, Short acBonus, Map<AbilityScore, Boolean> addToAc,
            boolean equippable, boolean attunable, List<EquippableType> itemEquippableTypes,
            Map<AbilityScore, Integer> abilityRequirement, Map<Skill, RollType> skillAlteredRollType,
            Map<Skill, Integer> skillAlteredBonus, boolean isContainer, Integer capacity) {
        this.itemUuid = itemUuid;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemWeight = itemWeight;
        this.itemValue = itemValue;
        this.itemRarity = itemRarity;
        this.attackable = attackable;
        this.acBonus = acBonus;
        this.addToAc = addToAc;
        this.equippable = equippable;
        this.attunable = attunable;
        this.itemEquippableTypes = itemEquippableTypes;
        this.abilityRequirement = abilityRequirement;
        this.skillAlteredRollType = skillAlteredRollType;
        this.skillAlteredBonus = skillAlteredBonus;
        this.isContainer = isContainer;
        this.capacity = capacity;
    }

    public ItemCatalog(UUID itemUuid, String itemName, String itemDescription, Integer itemWeight, Integer itemValue,
            Rarity itemRarity, boolean attackable, Short acBonus, Map<AbilityScore, Boolean> addToAc,
            boolean equippable, boolean attunable, List<EquippableType> itemEquippableTypes,
            Map<AbilityScore, Integer> abilityRequirement, Map<Skill, RollType> skillAlteredRollType,
            Map<Skill, Integer> skillAlteredBonus, boolean isContainer, Integer capacity, List<ClassNameIdPair> classNameIdPairs) {
        this.itemUuid = itemUuid;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemWeight = itemWeight;
        this.itemValue = itemValue;
        this.itemRarity = itemRarity;
        this.attackable = attackable;
        this.acBonus = acBonus;
        this.addToAc = addToAc;
        this.equippable = equippable;
        this.attunable = attunable;
        this.itemEquippableTypes = itemEquippableTypes;
        this.abilityRequirement = abilityRequirement;
        this.skillAlteredRollType = skillAlteredRollType;
        this.skillAlteredBonus = skillAlteredBonus;
        this.isContainer = isContainer;
        this.classNameIdPair = classNameIdPairs;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public boolean isContainer() {
        return isContainer;
    }

    public void setContainer(boolean isContainer) {
        this.isContainer = isContainer;
    }

    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public Integer getItemWeight() {
        return itemWeight;
    }

    public void setItemWeight(Integer itemWeight) {
        this.itemWeight = itemWeight;
    }

    public Integer getItemValue() {
        return itemValue;
    }

    public void setItemValue(Integer itemValue) {
        this.itemValue = itemValue;
    }

    public Rarity getItemRarity() {
        return itemRarity;
    }

    public void setItemRarity(Rarity itemRarity) {
        this.itemRarity = itemRarity;
    }

    public boolean isAttackable() {
        return attackable;
    }

    public void setAttackable(boolean attackable) {
        this.attackable = attackable;
    }

    public Short getAcBonus() {
        return acBonus;
    }

    public void setAcBonus(Short acBonus) {
        this.acBonus = acBonus;
    }

    public Map<AbilityScore, Boolean> getAddToAc() {
        return addToAc;
    }

    public void setAddToAc(Map<AbilityScore, Boolean> addToAc) {
        this.addToAc = addToAc;
    }

    public boolean isEquippable() {
        return equippable;
    }

    public void setEquippable(boolean equippable) {
        this.equippable = equippable;
    }

    public boolean isAttunable() {
        return attunable;
    }

    public void setAttunable(boolean attunable) {
        this.attunable = attunable;
    }

    public List<EquippableType> getItemEquippableTypes() {
        return itemEquippableTypes;
    }

    public void setItemEquippableTypes(List<EquippableType> itemEquippableTypes) {
        this.itemEquippableTypes = itemEquippableTypes;
    }

    public Map<Skill, RollType> getSkillAlteredRollType() {
        return skillAlteredRollType;
    }

    public void setSkillAlteredRollType(Map<Skill, RollType> skillAlteredRollType) {
        this.skillAlteredRollType = skillAlteredRollType;
    }

    public Map<Skill, Integer> getSkillAlteredBonus() {
        return skillAlteredBonus;
    }

    public void setSkillAlteredBonus(Map<Skill, Integer> skillAlteredBonus) {
        this.skillAlteredBonus = skillAlteredBonus;
    }

    public Map<AbilityScore, Integer> getAbilityRequirement() {
        return abilityRequirement;
    }

    public void setAbilityRequirement(Map<AbilityScore, Integer> abilityRequirement) {
        this.abilityRequirement = abilityRequirement;
    }

    public List<ClassNameIdPair> getClassNameIdPair() {
        return classNameIdPair;
    }

    public void setClassNameIdPair(List<ClassNameIdPair> classNameIdPair) {
        this.classNameIdPair = classNameIdPair;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ItemCatalog) {
            ItemCatalog that = (ItemCatalog) obj;
            return Objects.equals(that.itemUuid, this.itemUuid);
        }  
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemUuid);
    }
    
}
