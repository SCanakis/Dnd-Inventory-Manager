import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { BasicCharacterInfoComponent } from '../basic-character-info/basic-character-info-component';
import { Subscription } from 'rxjs';
import { CharacterHasItemProjection } from '../../../interface/inventory.types';
import { WebSocketResponse } from '../../../interface/websocket-interface';
import { WebSocketService } from '../../../service/websocket/websocket-service';
import { ActivatedRoute, Router } from '@angular/router';
import { ContainerService } from '../../../service/container/container';
import { ContainerView } from '../../../interface/container-interface';
import { HttpErrorResponse } from '@angular/common/http';
import { NavComponent } from '../nav/nav-component';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [CommonModule, BasicCharacterInfoComponent, NavComponent],
  templateUrl: './inventory.html',
  styleUrl: './inventory.scss'
})
export class Inventory implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  
  isConnected = false;
  currentContainer: ContainerView[] = [];
  currentInventory: CharacterHasItemProjection[] = [];
  lastMessage: WebSocketResponse | null = null;

  charUuid: string | null = '';
  
  constructor(
    private webSocketService: WebSocketService,
    private containerService: ContainerService,
    private route: ActivatedRoute, 
  ) {
    this.charUuid = this.route.snapshot.paramMap.get('charUuid');
  
  } 

  ngOnInit(): void {
    if (!this.charUuid) {
      console.error('No character UUID provided in route');
      return;
    }
    this.containerService.getContainers(this.charUuid).subscribe(
      (response : ContainerView[]) => {
        this.currentContainer = response;
      },
      (error : HttpErrorResponse) => {
        console.log("Error loading container", error.message);
      }
    )

    this.webSocketService.init(this.charUuid);
    this.webSocketService.connect();

    this.subscriptions.push(
      this.webSocketService.isConnected$.subscribe(connected => {
        this.isConnected = connected;
        if (connected && this.charUuid) {
          this.webSocketService.subscribeToCharacterInventory(this.charUuid);
        }
      })
    );

    this.subscriptions.push(
      this.webSocketService.inventoryUpdates$.subscribe(response => {
        if (response) {
          this.lastMessage = response;
          
          if (response.type === 'INVENTORY_INITIAL_LOAD' && response.data) {
            this.currentInventory = response.data;
          }
        } else {
          console.log('🔄 Response is null/undefined');
        }
      })
    );

    this.subscriptions.push(
      this.webSocketService.inventoryBroadcasts$.subscribe(response => {
        if (response && response.data) {
          this.currentInventory = response.data;
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

}