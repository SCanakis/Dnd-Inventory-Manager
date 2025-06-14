package com.scanakispersonalprojects.dndapp.service.inventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.inventory.ItemCatalog;
import com.scanakispersonalprojects.dndapp.model.inventory.ItemProjection;
import com.scanakispersonalprojects.dndapp.persistance.inventory.ItemCatalogJPARepo;

@Service
public class ItemCatalogService {
    

    @Autowired
    private ItemCatalogJPARepo repo;

    public ItemCatalogService(ItemCatalogJPARepo repo) {
        this.repo = repo;
    }

    public Optional<ItemCatalog> getItemWithUUID(UUID itemUuid) {
        return repo.findByItemUuid(itemUuid);
    }

    public List<ItemProjection> getAll() {
        return repo.findAllBy();
    }

    public List<ItemProjection> searchByName(String string) {
        return repo.findByNameSimilarity(string);
    }

}
