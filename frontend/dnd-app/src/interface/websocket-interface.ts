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

export interface CharacterHasItemProjection {
  itemUuid: string;
  charUuid: string;
  quantity: number;
}

export interface CharacterHasItemUpdate {
  itemUuid: string;
  quantity: number;
  equipped: boolean;
  attuned: boolean
  inAttackTab: boolean;
  conatinerUuid: string;
}