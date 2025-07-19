import { AbilityScore, CharacterClassDetail, HitDiceValue } from "./character-info-interface";

export interface Race {
    raceUuid : string;
    name : string;
    statIncreases : Map<AbilityScore, number>
}

export interface Background {
    backgroundUuid : string;
    name : string;
    description : string;
    startingGold : number;
}


export interface DndClass {
    classUuid : string;
    name : string;
    hitDiceValue : HitDiceValue;
    description : string;
}

export interface SubClass {
    subClassUuid : string;
    name : string;
    classSource : string;
}

export interface BasicCharInfoCreationDTO {
    name : string;
    backgroundUuid : string;
    raceUuid : string;
    abilityScores : Map<AbilityScore, number>;
    characterClassDetails : CharacterClassDetail[];
}