import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { BasicCharacterInfoComponent } from '../basic-character-info/basic-character-info-component';
import { retry, Subscription } from 'rxjs';
import { CharacterHasItemProjection } from '../../../interface/inventory.types';
import { WebSocketResponse } from '../../../interface/websocket-interface';
import { WebSocketServiceInventory } from '../../../service/websocket/websocket-service-inventory';
import { ActivatedRoute, Router } from '@angular/router';
import { ContainerService } from '../../../service/container/container';
import { ContainerView } from '../../../interface/container-interface';
import { HttpErrorResponse } from '@angular/common/http';
import { NavComponent } from '../nav/nav-component';
import { FormsModule } from '@angular/forms';
import { ItemCatalogInterface } from '../../../interface/item-catalog-interface';
import { ItemCatalogHttp } from '../../../service/item-catalog-http/item-catalog-http';
import { WebsocketServiceContainer } from '../../../service/websocket/websocket-service-container';


@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [CommonModule, FormsModule, BasicCharacterInfoComponent, NavComponent],
  templateUrl: './inventory.html',
  styleUrl: './inventory.scss'
})
export class Inventory implements OnInit, OnDestroy {

  private subscriptionsInventory: Subscription[] = [];
  private subscriptionsContainer: Subscription[] = [];
  
  isConnectedInventory = false;
  isConnectedContainer = false;
  currentContainer: ContainerView[] = [];
  currentInventory: CharacterHasItemProjection[] = [];
  lastMessage: WebSocketResponse | null = null;

  selectedItem: ItemCatalogInterface | null = null;
  selectedProject : CharacterHasItemProjection | null = null;
  searchTerm : string = '';
  charUuid: string | null = '';
  containerUuid: string | null = '';
  quantity : number = 1;


  constructor(
    private inventoryWebSocketService: WebSocketServiceInventory,
    private containerWebSocketService: WebsocketServiceContainer,
    private containerService: ContainerService,
    private itemCatalogService : ItemCatalogHttp,
    private route: ActivatedRoute, 
  ) {
    this.charUuid = this.route.snapshot.paramMap.get('charUuid');
  
  } 

  ngOnInit(): void {
    if (!this.charUuid) {
      console.error('No character UUID provided in route');
      return;
    }
    
    this.initializeContainerWebSocket();
    this.initializeInventoryWebSocket();
  }

  private initializeContainerWebSocket(): void {
    if (!this.charUuid) return;

    this.containerWebSocketService.init(this.charUuid);
    this.containerWebSocketService.connect();

    this.subscriptionsContainer.push(
      this.containerWebSocketService.isConnected$.subscribe(connected => {
        this.isConnectedContainer = connected;
        if (connected && this.charUuid) {
          this.containerWebSocketService.subscribeToCharacterContainers(this.charUuid);
        } 
      })
    );

    this.subscriptionsContainer.push(
      this.containerWebSocketService.containerUpdates$.subscribe(response => {
        if (response) {
          this.lastMessage = response;
          
          if (response.type === 'CONTAINER_REQUEST_RESPONSE' && response.data) {
            this.currentContainer = response.data;
          }
        } else {
          console.log('🔄 Container response is null/undefined');
        }
      })
    );

    this.subscriptionsContainer.push(
      this.containerWebSocketService.containerBroadcast$.subscribe(response => {
        if (response && response.data) {
          console.log('Container broadcast received:', response);
          this.currentContainer = response.data;
        }
      })
    );
  }

  private initializeInventoryWebSocket(): void {
    if (!this.charUuid) return;

    this.inventoryWebSocketService.init(this.charUuid);
    this.inventoryWebSocketService.connect();

    this.subscriptionsInventory.push(
      this.inventoryWebSocketService.isConnected$.subscribe(connected => {
        this.isConnectedInventory = connected;
        if (connected && this.charUuid) {
          this.inventoryWebSocketService.subscribeToCharacterInventory(this.charUuid);
        }
      })
    );

    this.subscriptionsInventory.push(
      this.inventoryWebSocketService.inventoryUpdates$.subscribe(response => {
        if (response) {
          this.lastMessage = response;
          
          if (response.type === 'INVENTORY_INITIAL_LOAD' && response.data) {
            this.currentInventory = response.data;
          }
        } else {
          console.log('Inventory response is null/undefined');
        }
      })
    );

    this.subscriptionsInventory.push(
      this.inventoryWebSocketService.inventoryBroadcasts$.subscribe(response => {
        if (response && response.data) {
          this.currentInventory = response.data;
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptionsInventory.forEach(sub => sub.unsubscribe());
    this.subscriptionsContainer.forEach(sub => sub.unsubscribe());
    
    // Clean up WebSocket connections
    this.inventoryWebSocketService.disconnect();
    this.containerWebSocketService.disconnect();
  }

  onSearchTerm() {
    if(this.charUuid) {
      if(this.containerUuid) {
        this.inventoryWebSocketService.requestInventory(this.charUuid, this.containerUuid, this.searchTerm);
      } else {
        this.inventoryWebSocketService.requestInventory(this.charUuid, undefined, this.searchTerm);
      }
    }
  }

  getContainerItems(containerUuid: string) {
    if(this.charUuid) {
      this.containerUuid = containerUuid;
      if(this.searchTerm) {
        this.inventoryWebSocketService.requestInventory(this.charUuid, this.containerUuid, this.searchTerm)
      } else {
        this.inventoryWebSocketService.requestInventory(this.charUuid, this.containerUuid);
      }
    } else {
      console.error('Cannot request container items - no character UUID available');
    }
  }

  getAllItems() {
    
    if(this.charUuid) {
      this.containerUuid = '';
      if(this.searchTerm) {
        this.inventoryWebSocketService.requestInventory(this.charUuid, this.containerUuid, this.searchTerm);
      } else {
        this.inventoryWebSocketService.requestInventory(this.charUuid);
      }
    } else {
      console.error('Cannot request container items - no character UUID available');
    }
  }

  openItemModal(item : CharacterHasItemProjection) : void {
    this.itemCatalogService.getItem(item.itemUuid).subscribe(
      (response : ItemCatalogInterface) => {
        this.selectedItem = response;
        this.selectedProject = item;
        this.quantity = 1;
      },
      (error : HttpErrorResponse) => {
        console.log("Error loading item: ", error.message);
      }
    );
    document.body.style.overflow = 'hidden';
  }

  closeItemModal(): void {
    this.selectedItem = null;
    this.selectedProject = null;
    this.quantity = 1;
    document.body.style.overflow = 'auto'
  }

  getMapEntries(map: Map<any, any> ) : Array<{key : any, value : any}> {
    if(!map) return [];
    return Array.from(map.entries()).map(([key, value]) => ({key,value}));
  }

  deleteItem() {
    if(this.charUuid && this.selectedItem) {
      this.inventoryWebSocketService.deleteInventoryItem(this.charUuid, this.selectedItem?.itemUuid, this.selectedProject?.containerUuid);
      this.closeItemModal();
    } else {
      console.log("Missing requirments to delete item");
    }
  }



}