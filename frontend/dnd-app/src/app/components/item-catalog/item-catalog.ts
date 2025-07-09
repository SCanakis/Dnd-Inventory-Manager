import { Component, OnDestroy, OnInit } from '@angular/core';
import { BasicCharacterInfoComponent } from '../basic-character-info/basic-character-info-component';
import { NavComponent } from '../nav/nav-component';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { WebsocketServiceItemCatalog } from '../../../service/websocket/websocket-service-item-catalog';
import { ItemProjection } from '../../../interface/item-projection-interface';
import { Subscription } from 'rxjs';
import { WebSocketResponse } from '../../../interface/websocket-interface';
import { FormsModule } from '@angular/forms';
import { ItemCatalogInterface } from '../../../interface/item-catalog-interface';
import { ItemCatalogHttp } from '../../../service/item-catalog-http/item-catalog-http';
import { response } from 'express';
import { error } from 'console';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-item-catalog',
  imports: [BasicCharacterInfoComponent, NavComponent, CommonModule, FormsModule],
  templateUrl: './item-catalog.html',
  styleUrl: './item-catalog.scss'
})
export class ItemCatalog implements OnInit, OnDestroy{

    private subscriptions: Subscription[] = [];
    
    isConnected = false;
    items : ItemProjection[] = [];
    lastMessage: WebSocketResponse | null = null;

    selectedItem : ItemCatalogInterface | null = null;

    searchTerm : string = '';
    charUuid : string | null = null;
    quantity : number = 1;
    
    constructor(
        private route: ActivatedRoute,
        private webSocketService : WebsocketServiceItemCatalog,
        private itemCatalogService : ItemCatalogHttp        
    ){
        this.charUuid = route.snapshot.paramMap.get('charUuid');
    }

    ngOnInit(): void {
        if(!this.charUuid) {
        console.error('No character UUID provided in route');
        return;
        }
        this.webSocketService.init(this.charUuid);
        this.webSocketService.connect();

        this.subscriptions.push(
            this.webSocketService.isConnected$.subscribe(connected => {
                this.isConnected = connected;
                if(connected && this.charUuid) {
                    this.webSocketService.requestItemCatalog(this.charUuid);
                }
            })
        );

        this.subscriptions.push(
            this.webSocketService.itemCatalogUpdates$.subscribe(response => {
                if(response) {
                    this.lastMessage = response;
                    this.items = response.data;
                }
            })

        )

    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(sub => sub.unsubscribe());
        this.webSocketService.disconnect();
    }

    onSearchTerm() : void {
        if(this.charUuid) {
            if(this.searchTerm) {
                this.webSocketService.requestItemCatalog(this.charUuid, this.searchTerm);
            } else {
                this.webSocketService.requestItemCatalog(this.charUuid);
            }
        }
    }

    openItemModal(item : ItemProjection) : void {
        this.itemCatalogService.getItem(item.itemUuid).subscribe(
            (response : ItemCatalogInterface) => {
                this.selectedItem = response;
                console.log("I AM HERE!!!!! - " + this.selectedItem.isContainer);
                this.quantity = 1;
            },
            (error : HttpErrorResponse) => {
                console.log("Error loading item:", error.message)
            }
        );
        document.body.style.overflow = 'hidden';
    }

    closeItemModal() : void {
        this.selectedItem = null;
        this.quantity = 1;
        document.body.style.overflow = 'auto';
    }

    getMapEntries(map: Map<any, any>): Array<{key: any, value: any}> {
        if (!map) return [];
        return Array.from(map.entries()).map(([key, value]) => ({ key, value }));
    }

    addItem() {
        if(this.selectedItem && this.quantity > 0 && this.charUuid) {
            console.log('Adding item with quantity:', this.quantity);
            console.log('Type of quantity:', typeof this.quantity);
            
            const qty = this.selectedItem.isContainer ? 1 : this.quantity;

            this.itemCatalogService.addItem(this.selectedItem.itemUuid, this.charUuid, qty).subscribe(
                (response) => {
                    console.log("Item added successfully: ", response);
                    this.closeItemModal();
                },
                (error : HttpErrorResponse) => {
                    console.log("Error adding item: ", error);
                }
            );
        } else {
            console.log("Missing requirement to add item");
        }
    }

    get canChooseQuantity() : boolean {
        return !!this.selectedItem && !this.selectedItem.isContainer;
    }

    get canAddItem() : boolean {
        if(!this.selectedItem) return false;
        if(this.selectedItem.isContainer) return true;

        return this.quantity > 0;
    }

    onQuantityChange(value : string) : void {
        const numValue = parseInt(value, 10);
        this.quantity = isNaN(numValue) || numValue < 1 ? 1 : numValue;
    }






}
