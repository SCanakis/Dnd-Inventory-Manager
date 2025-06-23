export interface CharacterHasItemProjection {
    itemUuid: string;
    itemName: string;
    itemWeight: string;
    itemValue: string;
    itemRarity: Rarity;
    quantity: number;
    equipped: boolean;
    attuned: boolean;
    containerUuid: string;
}

export enum Rarity {
    common = 'common',
    uncommon = 'uncommon',
    rare = 'rare',
    very_rare = 'very_rare',
    legendary = 'legendary'
}