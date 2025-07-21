import { SelectCharacter } from "../app/components/select-character/select-character";
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
    subclassUuid : string;
    name : string;
    classSource : string;
}

export class BasicCharInfoCreationDTO {
    name : string;
    backgroundUuid : string | null;
    raceUuid : string | null;
    abilityScores : Map<AbilityScore, number>;
    characterClassDetails : CharacterClassDetail[];

    constructor() {
        this.name = '';
        this.backgroundUuid = null;
        this.raceUuid = null;
        this.abilityScores = new Map<AbilityScore, number>(
            Object.values(AbilityScore).map(ability => [ability, 10])
        );
        this.characterClassDetails = [];
    }

    getName(): string {
        return this.name;
    }

    setName(name: string): void {
        this.name = name;
    }

    getBackgroundUuid(): string | null {
        return this.backgroundUuid;
    }

    setBackgroundUuid(backgroundUuid: string | null): void {
        this.backgroundUuid = backgroundUuid;
    }

    getRaceUuid(): string | null {
        return this.raceUuid;
    }

    setRaceUuid(raceUuid: string | null): void {
        this.raceUuid = raceUuid;
    }

    getAbilityScores(): Map<AbilityScore, number> {
        return this.abilityScores;
    }

    getAbilityScore(ability: AbilityScore): number {
        return this.abilityScores.get(ability) || 10;
    }

    setAbilityScore(ability: AbilityScore, score: number): void {
        if (score < 1 || score > 20) {
            throw new Error(`Ability score must be between 1 and 20. Received: ${score}`);
        }
        this.abilityScores.set(ability, score);
    }

    getCharacterClassDetails() : CharacterClassDetail[] {
        return this.characterClassDetails;
    }

    setCharacterClassDetails(classDetail : CharacterClassDetail[]) : void {
        this.characterClassDetails = classDetail;
    } 

    getTotalLevel() {
        let sum = 0;

        for(let cls of this.characterClassDetails) {
            sum += cls.level;
        }
        return sum
    }

    // UPDATED: Properly handle null values for UUIDs
    toJSON(): any {
        // Clean the character class details to ensure proper null handling
        const cleanedClassDetails = this.characterClassDetails.map(detail => ({
            ...detail,
            subclassUuid: detail.subclassUuid === 'undefined' || detail.subclassUuid === '' ? null : detail.subclassUuid
        }));

        return {
            name: this.name,
            backgroundUuid: this.backgroundUuid,
            raceUuid: this.raceUuid,
            abilityScores: Object.fromEntries(this.abilityScores), // Convert Map to Object
            characterClassDetails: cleanedClassDetails
        };
    }

    // UPDATED: Alternative serialization method with proper null handling
    toSerializableObject(): any {
        // Clean the character class details to ensure proper null handling
        const cleanedClassDetails = this.characterClassDetails.map(detail => ({
            ...detail,
            subclassUuid: detail.subclassUuid === 'undefined' || detail.subclassUuid === '' ? null : detail.subclassUuid
        }));

        return {
            name: this.name,
            backgroundUuid: this.backgroundUuid,
            raceUuid: this.raceUuid,
            abilityScores: Object.fromEntries(this.abilityScores),
            characterClassDetails: cleanedClassDetails
        };
    }
}