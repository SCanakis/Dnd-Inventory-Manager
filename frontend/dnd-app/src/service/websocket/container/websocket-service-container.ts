import { Injectable } from '@angular/core';
import { Client, StompConfig } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';

import { WebSocketResponse } from '../../../interface/websocket-interface';
import { environment } from '../../../environments/environment.development';
import { ContainerView } from '../../../interface/container-interface';

@Injectable({
  providedIn: 'root'
})
export class WebsocketServiceContainer {

  private client! : Client;
  private connected = new BehaviorSubject<boolean>(false);
  private containerUpdates = new BehaviorSubject<WebSocketResponse | null>(null);
  private containerBroadcast = new BehaviorSubject<WebSocketResponse | null>(null);
  
  private charUuid! : string;
  
  constructor() { }

  public init(chardUuid : string) {
    if(this.charUuid !== chardUuid) {
      this.charUuid = chardUuid;
      this.setupClient();
    }
  }

  private setupClient() : void {
    const config : StompConfig = {
      brokerURL : environment.webSocketUrl,
      connectHeaders : {

      },
      debug: (str) => {
        console.log('STOMP Debug' , str);
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

  private setupSubscriptions() {
    this.client.subscribe("/user/queue/containers", message => {
      const response: WebSocketResponse = JSON.parse(message.body);
      this.containerUpdates.next(response);
    });
  }

  subscribeToCharacterContainers(charUuid : string) : void {
    if(this.client.connected) {
      this.client.subscribe(`/topic/character/${charUuid}/containers`, (message) => {
        try {
          const response : WebSocketResponse<ContainerView[]> = JSON.parse(message.body);
          this.containerBroadcast.next(response);
        } catch (error) {
          console.error('Error parsing container broadcast:', error);
        }
      });
      this.requestInitalContainers(charUuid);
    } else {
      console.warn('Cannot subscribe to character containers - WebSocket not connected');
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

  requestInitalContainers(charUuid: string): void {
    if (this.client.connected) {

      const message = {
        type : 'CONTAINER_SEARCH_REQUEST',
        charUuid
      };

      this.client.publish({
        destination: '/app/container/subscribe',
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot request inital containers - WebSocket not connected');
    }
  }

  requestContainers(charUuid: string): void {
    if (this.client.connected) {

      const message = {
        type : 'CONTAINER_SEARCH_REQUEST',
        charUuid
      };

      this.client.publish({
        destination: '/app/container/subscribe',
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot request inital containers - WebSocket not connected');
    }
  }

  deleteContainer(charUuid: string, containerUuid : string) {
    if(this.client.connected) {
      const message = {
        type : 'CONTAINER_DELETE',
        charUuid,
        containerUuid
      };

      console.log("Sending container deletion request message...", message);
      this.client.publish({
        destination : '/app/container/delete',
        body: JSON.stringify(message)
      });
    } else {
      console.error('Cannot send message - WebSocket not connected');
    }


  }

    get isConnected$(): Observable<boolean> {
      return this.connected.asObservable();
    }
  
    get containerUpdates$(): Observable<WebSocketResponse | null> {
      return this.containerUpdates.asObservable();
    }
  
    get containerBroadcast$(): Observable<WebSocketResponse<ContainerView[]> | null> {
      return this.containerBroadcast.asObservable();
    }

}
