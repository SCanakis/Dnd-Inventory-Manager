package com.scanakispersonalprojects.dndapp.model.webSocket;

import java.util.UUID;

/**
 * WebScoket message for requesing itemCatalog 
 */
public class ItemCatalogRequestMessage extends WebSocketMessage{
    
    // string used for filtering
    private String searchTerm = null;



    /**
     * Default constructor for JSON deserialization.
     */
    public ItemCatalogRequestMessage() {
        super("ITEM_CATALOG_SEARCH_REQUEST", null);
    }
    
    /**
     * Creates a new item catalog request message which a specific term.
     * 
     * @param charUuid
     * @param searchTerm
     */
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
