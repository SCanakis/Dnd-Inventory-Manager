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

export enum HitDiceValue {
    D4 = 4,
    D6 = 6,
    D8 = 8,
    D10 = 10,
    D12 = 12
}

export interface CharacterClassDetail {
    classUuid: string,
    className: string;
    hitDiceValue: HitDiceValue, 
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

export class CharacterInfoUpdateDTO {
    name: string | null = null;
    inspiration: boolean | null = null;
    backgroundUuid: string | null = null;
    raceUuid: string | null = null; 
    abilityScores: Record<AbilityScore, number> = {} as Record<AbilityScore, number>;
    hpHandler: HPHandler | null = null;
    deathSavingThrowsHelper: DeathSavingThrowsHelper | null = null;
    characterClassDetail: CharacterClassDetail[] = [];

    constructor() {

    }

    setName(name: string): void {
        this.name = name;
    }

    setInspiration(inspiration: boolean): void {
        this.inspiration = inspiration;
    }

    setBackgroundUuid(backgroundUuid: string): void {
        this.backgroundUuid = backgroundUuid;
    }

    setRaceUuid(raceUuid: string): void {
        this.raceUuid = raceUuid;
    }

    setAbilityScore(ability: AbilityScore, value: number): void {
        this.abilityScores[ability] = value; 
    }

    setHpHandler(hpHandler: HPHandler): void {
        this.hpHandler = hpHandler;
    }

    setDeathSavingThrowsHelper(deathSavingThrowsHelper: DeathSavingThrowsHelper): void {
        this.deathSavingThrowsHelper = deathSavingThrowsHelper;
    }

    setCharacterClassDetail(characterClassDetail: CharacterClassDetail[]): void {
        this.characterClassDetail = characterClassDetail;
    }

    getName(): string | null {
        return this.name;
    }

    getInspiration(): boolean | null {
        return this.inspiration;
    }

    getBackgroundUuid(): string | null {
        return this.backgroundUuid;
    }

    getRaceUuid(): string | null {
        return this.raceUuid;
    }

    getAbilityScores(): Record<AbilityScore, number> {
        return this.abilityScores; // Changed return type
    }

    getAbilityScore(ability: AbilityScore): number {
        return this.abilityScores[ability] || 10; // Changed from Map.get to object access
    }

    getHpHandler(): HPHandler | null{ 
        return this.hpHandler;
    }

    getDeathSavingThrowsHelper(): DeathSavingThrowsHelper | null {
        return this.deathSavingThrowsHelper;
    }

    getCharacterClassDetail(): CharacterClassDetail[] {
        return this.characterClassDetail;
    }
}