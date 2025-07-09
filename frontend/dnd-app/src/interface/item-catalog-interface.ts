import { AbilityScore } from "./character-info-interface";
import { Rarity } from "./inventory.types";

export interface ItemCatalogInterface {

    itemUuid : string;
    itemName : string;
    itemDescription : string;

    itemWeight?: number;
    itemValue?: number;
    itemRarity?: Rarity;
    attackable?: boolean;

    acBonus?: number;
    addToAc?: Map<AbilityScore, Boolean>;

    equippable? : boolean;
    attunable? : boolean;

    itemEquippableTypes? : EquippableTypes[];
    abilityRequirement? : Map<AbilityScore, number>;

    skillAlteredRollType? : Map<Skill, RollType>;
    skillAlteredBonus? : Map<Skill, number>;

    isContainer : boolean;

    capacity? : number;

    classNameIdPair?: ClassNameIdPair[];
}


export enum EquippableTypes {
    armor,
    cloak,
    bracers,
    head,
    belt,
    hands,
    ringl,     
    ringr,       
    feet,
    mainhand,
    offhand,
    twohand,
    back,
    spellfocus,
    custom
}

export enum Skill {
    acrobatics,
    animal_handling,
    arcana,
    athletics,
    deception,
    history,
    insight,
    intimidation,
    investigation,
    medicine,
    nature,
    perception,
    performance,
    persuasion,
    religion,
    sleight_of_hand,
    stealth,
    survival
}

export enum RollType {
    advantage,
    straight,
    disadvantage
}

export interface ClassNameIdPair {
    classUuid : string;
    className : string;
}