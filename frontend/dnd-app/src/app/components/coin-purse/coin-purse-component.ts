import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { WebsocketServiceCoinPurse } from '../../../service/websocket/coin-purse/websocket-service-coin-purse';
import { ActivatedRoute } from '@angular/router';
import { WebSocketResponse } from '../../../interface/websocket-interface';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CoinPurse } from '../../../interface/coin-purse-interface';


@Component({
  selector: 'app-coin-purse-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './coin-purse-component.html',
  styleUrl: './coin-purse-component.scss',
})
export class CoinPurseComponent implements OnInit, OnDestroy{


  private subscriptions: Subscription[] = [];
  charUuid : string | null = null;
  isConnected = false;
  lastMessage: WebSocketResponse | null = null;

  currentCoinPurse : CoinPurse | null = null;

  constructor(
    private coinPurseWebSocketService : WebsocketServiceCoinPurse,
    private route : ActivatedRoute
  ) {
    this.charUuid = route.snapshot.paramMap.get('charUuid');
  }

  ngOnInit(): void {
    if(!this.charUuid) {
      console.error('No character UUID provided in route');
      return;
    }
    this.init();
  }

  private init() : void {
    if(!this.charUuid) return;

    this.coinPurseWebSocketService.init(this.charUuid);
    this.coinPurseWebSocketService.connect();

    this.subscriptions.push(
      this.coinPurseWebSocketService.isConnected$.subscribe(connected => {
        this.isConnected = connected;
        if(connected && this.charUuid) {
          this.coinPurseWebSocketService.subscribeToCoinPurse(this.charUuid);
        }
      })
    );


    this.subscriptions.push(
      this.coinPurseWebSocketService.coinPurseUpdates$.subscribe(response => {
        if(response) {
          this.lastMessage = response;

          if(response.data) {
            this.currentCoinPurse = response.data;
          }
        } else {
          console.log('Response is null/undefined');
        }
      })
    );

    this.subscriptions.push(
      this.coinPurseWebSocketService.coinPurseBroadcasts$.subscribe(response => {
        if(response?.data) {
          console.log("Broadcast recieved : ", response);
          this.currentCoinPurse = response.data;
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.coinPurseWebSocketService.disconnect();
  }





}
