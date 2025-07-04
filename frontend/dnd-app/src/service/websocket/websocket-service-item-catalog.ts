import { Injectable } from '@angular/core';
import { Client, StompConfig } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';
import { WebSocketResponse } from '../../interface/websocket-interface';
import { ItemProjection } from '../../interface/item-projection-interface';
import { environment } from '../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class WebsocketServiceItemCatalog {
  private client!: Client; // Use definite assignment assertion
  private connected = new BehaviorSubject<boolean>(false);
  private itemCatalogUpdates = new BehaviorSubject<WebSocketResponse | null>(null);
  private itemCatalogBroadcast = new BehaviorSubject<WebSocketResponse<ItemProjection[]> | null>(null);
  private charUuid!: string;

  constructor() { }

  public init(charUuid : string) {
    if(this.charUuid !== charUuid) {
      this.charUuid = charUuid;
      this.setUpClient();
    }
  }

  private setUpClient() : void {
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
    this.client.subscribe("/user/queue/itemCatalog", message => {
      const response: WebSocketResponse = JSON.parse(message.body);
      this.itemCatalogUpdates.next(response);
    });
  }

  subsribeToItemCatalog(charUuid: string): void {
      if (this.client.connected) {
        this.client.subscribe(`/topic/character/${charUuid}/itemCatalog`, (message) => {
          try {
            const response: WebSocketResponse<ItemProjection[]> = JSON.parse(message.body);
            this.itemCatalogBroadcast.next(response);
          } catch (error) {
            console.error('Error parsing itemCatalog broadcast:', error);
          }
        });
        
        this.requestInitialItemCatalog(charUuid);
      } else {
        console.warn('Cannot subscribe to itemCatalog - WebSocket not connected');
      }
  }

  requestInitialItemCatalog(charUuid: string, searchTerm? : string): void {
    if (this.client.connected) {

      const message = {
        type : 'ITEM_CATALOG_SEARCH_REQUEST',
        charUuid,
        searchTerm: searchTerm || null
      };

      this.client.publish({
        destination: '/app/itemCatalog/subscribe',
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot request initial itemCatalog - WebSocket not connected');
    }
  }

  requestItemCatalog(charUuid: string, searchTerm? : string): void {
    if (this.client.connected) {

      const message = {
        type : 'ITEM_CATALOG_SEARCH_REQUEST',
        charUuid,
        searchTerm: searchTerm || null
      };

      this.client.publish({
        destination: '/app/itemCatalog/subscribe',
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot request itemCatalog - WebSocket not connected');
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

  get isConnected$(): Observable<boolean> {
    return this.connected.asObservable();
  }
  
  get itemCatalogUpdates$(): Observable<WebSocketResponse | null> {
    return this.itemCatalogUpdates.asObservable();
  }

  get itemCatalogBroadcasts$(): Observable<WebSocketResponse<ItemProjection[]> | null> {
    return this.itemCatalogBroadcast.asObservable();
  }

}
