import { Injectable } from '@angular/core';
import { Client, StompConfig } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';
import { WebSocketResponse } from '../../../interface/websocket-interface';
import { environment } from '../../../environments/environment.development';
import { CoinPurse, CoinPurseDTO } from '../../../interface/coin-purse-interface';

@Injectable({
  providedIn: 'root'
})
export class WebsocketServiceCoinPurse {

  private client!: Client;
  private connected = new BehaviorSubject<boolean>(false);
  private coinPurseUpdates = new BehaviorSubject<WebSocketResponse | null>(null);
  private coinPurseBroadcast = new BehaviorSubject<WebSocketResponse<CoinPurse> | null>(null);
  private charUuid!: string;

  constructor() { }

  public init(charUuid : string) {

    if(charUuid !== this.charUuid) {
      this.charUuid = charUuid;
      this.setupClient();
    }

  }

  public setupClient() : void {
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
    this.client.subscribe("/user/queue/coin-purse", message => {
    const response: WebSocketResponse = JSON.parse(message.body);
      this.coinPurseUpdates.next(response);
    });
    
  }

  subscribeToCoinPurse(charUuid: string): void {
    if (this.client.connected) {
      this.client.subscribe(`/topic/character/${charUuid}/coin-purse`, (message) => {
        try {
          const response: WebSocketResponse<CoinPurse> = JSON.parse(message.body);
          this.coinPurseBroadcast.next(response);
        } catch (error) {
          console.error('Error parsing coin-purse broadcast:', error);
        }
      });
      
      this.requestCoinPurse(charUuid);
    } else {
      console.warn('Cannot subscribe to coin purse - WebSocket not connected');
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

  requestCoinPurse(charUuid: string): void {
    if (this.client.connected) {

      const message = {
        // type : 'COIN_PURSE_REQUEST',
        charUuid
      };

      this.client.publish({
        destination: '/app/coin-purse/subscribe',
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot request initial coin-purse - WebSocket not connected');
    }
  }


  updateCoinPurse(charUuid: string, update : CoinPurseDTO ): void {
    if (this.client.connected) {
      const message = {
        charUuid,
        update
      };
      
      console.log('Sending coin purse update message...', message);
      this.client.publish({
        destination: '/app/coin-purse/update',
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot send message - WebSocket not connected');
    }
  }

  get isConnected$(): Observable<boolean> {
    return this.connected.asObservable();
  }

  get coinPurseUpdates$(): Observable<WebSocketResponse | null> {
    return this.coinPurseUpdates.asObservable();
  }

  get coinPurseBroadcasts$(): Observable<WebSocketResponse<CoinPurse> | null> {
    return this.coinPurseBroadcast.asObservable();
  }
}
