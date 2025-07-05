import { Rarity } from "./inventory.types";

export interface ItemProjection {
    itemUuid : string;
    itemName : string;
    itemWeight : number;
    itemValue : number;
    itemRarity : Rarity;
    containerUuid : string;
}