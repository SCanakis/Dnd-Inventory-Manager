import { CharacterInfoUpdateDTO } from "./character-info-interface";

export interface WebSocketResponse<T = any> {
  type: string;
  success: boolean;
  message: string;
  data: T;
  timestamp: number;
}

export interface WebSocketMessage {
  type: string;
  charUuid: string;
  timestamp: number;
}

export interface InventoryUpdateMessage extends WebSocketMessage {
  itemUuid: string;
  containerUuid?: string;
  update: CharacterHasItemUpdate;
}

export interface InventoryAddMessage extends WebSocketMessage {
  itemUuid: string;
  quantity: number;
}

export interface InventoryDeleteMessage extends WebSocketMessage {
  itemUuid: string;
  containerUuid?: string;
}

export interface InventoryRequestMessage extends WebSocketMessage{
  containerUuid?: string;
  searchTerm?: string;
}

export interface CharacterStatsUpdateMessage extends WebSocketMessage {
  update : CharacterInfoUpdateDTO;
}

export interface ContainerDeletionMessage extends WebSocketMessage {
  containerUuid : string;
}

// export interface CharacterHasItemProjection {
//   itemUuid: string;
//   charUuid: string;
//   quantity: number;
// }

export interface CharacterHasItemUpdate {
  itemUuid: string | null;
  quantity: number | null;
  equipped: boolean | null;
  attuned: boolean | null,
  inAttackTab: boolean | null;
  containerUuid: string | null;
}

