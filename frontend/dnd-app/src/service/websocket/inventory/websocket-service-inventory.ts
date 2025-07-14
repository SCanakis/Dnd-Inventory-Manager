import { Injectable } from '@angular/core';
import { Client, StompConfig } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { CharacterHasItemUpdate, WebSocketResponse } from '../../../interface/websocket-interface';
import { CharacterHasItemProjection } from '../../../interface/inventory.types';



@Injectable({
  providedIn: 'root'
})
export class WebSocketServiceInventory {
  private client!: Client; 
  private connected = new BehaviorSubject<boolean>(false);
  private inventoryUpdates = new BehaviorSubject<WebSocketResponse | null>(null);
  private inventoryBroadcasts = new BehaviorSubject<WebSocketResponse<CharacterHasItemProjection[]> | null>(null);
  private charUuid!: string;

  

  constructor() {
  }

  public init(charUuid : string) {
    if(this.charUuid !== charUuid) {
      this.charUuid = charUuid;
      this.setupClient();
    }
  }

  private setupClient(): void {
    const config: StompConfig = {
      brokerURL: environment.webSocketUrl, 
      connectHeaders: {
        
      },
      debug: (str) => {
        console.log('STOMP Debug:', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      // Browser environment uses native WebSocket
      webSocketFactory: () => new WebSocket(environment.webSocketUrl),
    };

    this.client = new Client(config);

    this.client.onConnect = () => {
      this.setupSubscriptions();
      this.connected.next(true);
    };

    this.client.onDisconnect = () => {
      this.connected.next(false);
    };

    this.client.onStompError = (frame) => {
      console.error('STOMP error:', frame.headers, frame.body);
    };

    this.client.onWebSocketError = (error) => {
      console.error('WebSocket error:', error);
    };
  }

  private setupSubscriptions(): void {
    this.client.subscribe("/user/queue/inventory", message => {
      const response: WebSocketResponse = JSON.parse(message.body);
      this.inventoryUpdates.next(response);
    });
    
  }

  subscribeToCharacterInventory(charUuid: string): void {
    if (this.client.connected) {
      this.client.subscribe(`/topic/character/${charUuid}/inventory`, (message) => {
        try {
          const response: WebSocketResponse<CharacterHasItemProjection[]> = JSON.parse(message.body);
          this.inventoryBroadcasts.next(response);
        } catch (error) {
          console.error('Error parsing inventory broadcast:', error);
        }
      });
      
      this.requestInitialInventory(charUuid);
    } else {
      console.warn('Cannot subscribe to character inventory - WebSocket not connected');
    }
  }

  connect(): void {
    if (!this.client.connected) {
      console.log('Starting connection...');
      this.client.activate();
    }
  }

  disconnect(): void {
    if (this.client.connected) {
      this.client.deactivate();
    }
  }

  updateInventoryItem(charUuid: string, itemUuid: string, containerUuid: string | undefined, update: CharacterHasItemUpdate): void {
    if (this.client.connected) {
      const message = {
        charUuid,
        itemUuid,
        containerUuid,
        update
      };
      
      console.log('Sending inventory update message...', message);
      this.client.publish({
        destination: '/app/inventory/update',
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot send message - WebSocket not connected');
    }
  }

  addInventoryItem(charUuid: string, itemUuid: string, quantity: number): void {
    if (this.client.connected) {
      const message = {
        charUuid,
        itemUuid,
        quantity
      };
      
      this.client.publish({
        destination: '/app/inventory/add',
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot send message - WebSocket not connected');
    }
  }

  deleteInventoryItem(charUuid: string, itemUuid: string, containerUuid?: string): void {
    if (this.client.connected) {
      const message = {
        charUuid,
        itemUuid,
        containerUuid
      };
      
      this.client.publish({
        destination: '/app/inventory/delete',
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot send message - WebSocket not connected');
    }
  }

  requestInitialInventory(charUuid: string, containerUuid? : string, searchTerm? : string): void {
    if (this.client.connected) {

      const message = {
        type : 'INVENTORY_SEARCH_REQUEST',
        charUuid,
        containerUuid : containerUuid || null,
        searchTerm: searchTerm || null
      };

      this.client.publish({
        destination: '/app/inventory/subscribe',
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot request initial inventory - WebSocket not connected');
    }
  }

  requestInventory(charUuid: string, containerUuid? : string, searchTerm? : string): void {
    if (this.client.connected) {
      const message = {
        type : 'INVENTORY_SEARCH_REQUEST',
        charUuid,
        containerUuid : containerUuid || null,
        searchTerm: searchTerm || null
      };

      console.log('Request inventory with filters:' , message);
      this.client.publish({
        destination: '/app/inventory/subscribe',
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot request initial inventory - WebSocket not connected');
    }
  }

  get isConnected$(): Observable<boolean> {
    return this.connected.asObservable();
  }

  get inventoryUpdates$(): Observable<WebSocketResponse | null> {
    return this.inventoryUpdates.asObservable();
  }

  get inventoryBroadcasts$(): Observable<WebSocketResponse<CharacterHasItemProjection[]> | null> {
    return this.inventoryBroadcasts.asObservable();
  }


}