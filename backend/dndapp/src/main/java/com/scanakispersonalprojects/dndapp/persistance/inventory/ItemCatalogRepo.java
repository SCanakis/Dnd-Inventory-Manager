package com.scanakispersonalprojects.dndapp.persistance.inventory;

import java.util.List;
import java.util.UUID;

import com.scanakispersonalprojects.dndapp.model.inventory.Item;
import com.scanakispersonalprojects.dndapp.model.inventory.ItemPreview;

public interface ItemCatalogRepo {

    public Item getItem(UUID itemUuid); 

    public List<ItemPreview> searchByName(String name);

    public List<ItemPreview> getAll();
}
