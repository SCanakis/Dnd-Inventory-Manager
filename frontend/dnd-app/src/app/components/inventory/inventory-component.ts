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


@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [CommonModule, FormsModule, BasicCharacterInfoComponent, NavComponent],
  templateUrl: './inventory.html',
  styleUrl: './inventory.scss'
})
export class Inventory implements OnInit, OnDestroy {

  private subscriptions: Subscription[] = [];
  
  isConnected = false;
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
    private webSocketService: WebSocketServiceInventory,
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

  onSearchTerm() {
    if(this.charUuid) {
      if(this.containerUuid) {
        this.webSocketService.requestInventory(this.charUuid, this.containerUuid, this.searchTerm);
      }
      this.webSocketService.requestInventory(this.charUuid, undefined, this.searchTerm);
    }
  }

  getContainerItems(containerUuid: string) {
    if(this.charUuid) {
      this.containerUuid = containerUuid;
      if(this.searchTerm) {
        this.webSocketService.requestInventory(this.charUuid, this.containerUuid, this.searchTerm)
      } else {
        this.webSocketService.requestInventory(this.charUuid, this.containerUuid);
      }
    } else {
      console.error('Cannot request container items - no character UUID available');
    }
  }

  getAllItems() {
    
    if(this.charUuid) {
      this.containerUuid = ''
      if(this.searchTerm) {
        this.webSocketService.requestInventory(this.charUuid, this.containerUuid, this.searchTerm);
      } else {
        this.webSocketService.requestInventory(this.charUuid);
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
      this.webSocketService.deleteInventoryItem(this.charUuid, this.selectedItem?.itemUuid, this.selectedProject?.containerUuid);
      this.closeItemModal();
    } else {
      console.log("Missing requirments to delete item");
    }
  }



}