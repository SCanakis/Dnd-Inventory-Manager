export interface CharacterBasicInfoView {
    charInfoUUID: string;
    name: string;
    inspiration: boolean;
    background: string;
    backgroundUUID: string;
    race: string;
    raceUUID: string;
    abilityScores: Record<AbilityScore, number>;
    classes: CharacterClassDetail[];
    hpHandler: HPHandler;
    deathSavingThrowsHelper: DeathSavingThrowsHelper;
}

export enum AbilityScore {
    strength = 'strength',
    dexterity = 'dexterity',
    constitution = 'constitution',
    intelligence = 'intelligence',
    wisdom = 'wisdom',
    charisma = 'charisma'
}

export enum hitDiceValue {
    D4 = 4,
    D6 = 6,
    D8 = 8,
    D10 = 10,
    D12 = 12
}

export interface CharacterClassDetail {
    classUuid: string,
    className: string;
    hitDiceValue: hitDiceValue, 
    subClassUuid: string,
    subClassName: string,
    level: number;
    hitDiceRemaining: number
}

export interface HPHandler {
    currentHp: number;
    maxHp: number;
    temporaryHp?: number;
}

export interface DeathSavingThrowsHelper {
    successes: number;
    failures: number;
}

export interface CharacterInfoUpdateDTO {
    name : String;
    inspiration : Boolean;
    backgroundUuid : String;
    raceUuid : string;
    abilityScores : Map<AbilityScore, number>;
    hpHandler : HPHandler;
    deathSavingThrowsHelper : DeathSavingThrowsHelper;
    characterClassDetail : CharacterClassDetail[];
}