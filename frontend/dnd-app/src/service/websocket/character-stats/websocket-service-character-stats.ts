import { Injectable } from '@angular/core';
import { Client, StompConfig } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';
import { WebSocketResponse } from '../../../interface/websocket-interface';
import { CharacterBasicInfoView, CharacterInfoUpdateDTO } from '../../../interface/character-info-interface';
import { environment } from '../../../environments/environment.dev';

@Injectable({
  providedIn: 'root'
})
export class WebsocketServiceCharacterStats {

  private client!: Client;
  private connected = new BehaviorSubject<boolean>(false);
  private characterUpdates = new BehaviorSubject<WebSocketResponse | null>(null);
  private characterBroadcasts= new BehaviorSubject<WebSocketResponse <CharacterBasicInfoView>| null>(null);
  private charUuid!: string;

  constructor() { }

  public init(charUuid : string) {
    if(this.charUuid !== charUuid) {
      this.charUuid = charUuid;
      this.setupClient();
    }
  }

  private setupClient(): void {
    const config : StompConfig = {
      brokerURL : environment.webSocketUrl,
      connectHeaders: {

      },
      debug: (str) => {
        console.log('STOMP Debug:', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      webSocketFactory: () => new WebSocket(environment.webSocketUrl)
    };

    this.client = new Client(config);

    this.client.onConnect = () => {
      this.setupSubscriptions();
      this.connected.next(true);
    }

    this.client.onDisconnect = () => {
      this.connected.next(false);
    }

    this.client.onStompError = (frame) => {
      console.error('STOMP error:', frame.headers, frame.body);
    };

    this.client.onWebSocketError = (error) => {
      console.error('WebSocket error:', error);
    };

  }

  private setupSubscriptions(): void {
    this.client.subscribe("/user/queue/character-stats", message => {
      const response: WebSocketResponse = JSON.parse(message.body);
      this.characterUpdates.next(response);
    });
  }

  subscribeToCharacterStats(charUuid : string) : void {
    if(this.client.connected) {
      this.client.subscribe(`/topic/character/${charUuid}/character-stats`, (message) => {
        try {
          const response : WebSocketResponse<CharacterBasicInfoView> = JSON.parse(message.body);
          this.characterBroadcasts.next(response);
        } catch (error) {
          console.log("Error parsing character-stat broadcast:", error);
        }
      });

      this.requestInitialStats(charUuid);
    } else {
      console.warn("Cannot subscribe to character stats - WebSocket not connected")
    }
  }

  requestInitialStats(charUuid : string) {
    if(this.client.connected) {
      const message = {
        charUuid
      };

      this.client.publish({
        destination: '/app/character-stats/subscribe',
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot request inital character stats - WebSocket not connected');
    }
  }


  connect() : void {
    if(!this.client.connected) {
      console.log("Starting connection...")
      this.client.activate();
    }
  }

  disconnect() : void {
    if(this.client.connected) {
      this.client.deactivate();
    }
  }

  updateCharacterInfo(charUuid : string, update : CharacterInfoUpdateDTO) {
    if(this.client.connected) {
      const message = {
        charUuid, 
        update
      };
      console.log('Updating Characte Stats message ...', message);
      this.client.publish({
        destination: '/app/character-stats/update',
        body: JSON.stringify(message)
      });
    } else {
      console.log("Cannot send message - WebScoket not connected");
    }
  
  }

  get isConnected$() : Observable<boolean> {
    return this.connected.asObservable();
  }

  get characterUpdates$() : Observable<WebSocketResponse | null> {
    return this.characterUpdates.asObservable();
  }

  get characterBroadcasts$() : Observable<WebSocketResponse<CharacterBasicInfoView> | null> {
    return this.characterBroadcasts.asObservable();
  }

}
