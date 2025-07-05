package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

public class ItemCatalogRequestMessage extends WebSocketMessage{
    
    private String searchTerm = null;

    public ItemCatalogRequestMessage() {
        super("ITEM_CATALOG_SEARCH_REQUEST", null);
    }
    
    public ItemCatalogRequestMessage(UUID charUuid, String searchTerm) {
        super("ITEM_CATALOG_SEARCH_REQUEST", charUuid);
        this.searchTerm = searchTerm;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

}
