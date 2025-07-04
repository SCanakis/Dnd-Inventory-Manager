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
            },
            (error : HttpErrorResponse) => {
                console.log("Error loading item:", error.message)
            }
        );
        document.body.style.overflow = 'hidden';
    }

    closeItemModal() : void {
        this.selectedItem = null;
        document.body.style.overflow = 'auto';
    }

    getMapEntries(map: Map<any, any>): Array<{key: any, value: any}> {
        if (!map) return [];
        return Array.from(map.entries()).map(([key, value]) => ({ key, value }));
    }



}
