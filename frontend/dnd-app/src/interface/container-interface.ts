import { CharacterHasItemProjection } from "./inventory.types";

export interface ContainerView {
    container : Container;
    name : string;
    items : CharacterHasItemProjection[];
}

export interface Container {
    id : ContainerId;
    itemUuid : string;
    maxCapacity : number;
    currentCapacity : number;
}

export interface ContainerId {
    containerUuid : string;
    charUuid : string;
}