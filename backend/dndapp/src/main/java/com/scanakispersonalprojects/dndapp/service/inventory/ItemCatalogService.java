package com.scanakispersonalprojects.dndapp.service.inventory;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.scanakispersonalprojects.dndapp.model.inventory.Item;
import com.scanakispersonalprojects.dndapp.model.inventory.ItemPreview;
import com.scanakispersonalprojects.dndapp.persistance.inventory.ItemCatalogRepo;

public class ItemCatalogService {
    

    @Autowired
    private ItemCatalogRepo repo;

    public ItemCatalogService(ItemCatalogRepo repo) {
        this.repo = repo;
    }

    public Item getItemWithUUID(UUID itemUuid) {
        return repo.getItem(itemUuid);
    }

    public List<ItemPreview> getAll() {
        return repo.getAll();
    }

    public List<ItemPreview> searchByName(String string) {
        return repo.searchByName(string);
    }

}
