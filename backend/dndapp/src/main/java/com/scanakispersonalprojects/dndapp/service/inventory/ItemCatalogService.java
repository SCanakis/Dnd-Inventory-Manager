package com.scanakispersonalprojects.dndapp.service.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ClassNameIdPair;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemCatalog;
import com.scanakispersonalprojects.dndapp.model.inventory.itemCatalog.ItemProjection;
import com.scanakispersonalprojects.dndapp.persistance.inventory.ItemCatalogJPARepo;
import com.scanakispersonalprojects.dndapp.persistance.inventory.ItemClassEligibilityRepository;

@Service
public class ItemCatalogService {
    
    
    private ItemCatalogJPARepo repo;

    private ItemClassEligibilityRepository classRepo;

    public ItemCatalogService(ItemCatalogJPARepo repo, ItemClassEligibilityRepository classRepo) {
        this.repo = repo;
        this.classRepo = classRepo;
    }

    public ItemCatalog getItemWithUUID(UUID itemUuid) {
        Optional<ItemCatalog> itemCatalog = repo.findByItemUuid(itemUuid);
        if(itemCatalog.isEmpty()) {
            return null;
        } else {
            ItemCatalog item = itemCatalog.get();
            List<Object[]> classes = classRepo.findClassesByItemUuid(itemUuid);
            List<ClassNameIdPair> pairs = new ArrayList<>();
            for(Object[] row : classes) {
                pairs.add(new ClassNameIdPair((UUID) row[0], (String) row[1]));
            }
            item.setClassNameIdPair(pairs);
            return item;
        }
    }

    public List<ItemProjection> getAll() {
        return repo.findAllBy();
    }

    public List<ItemProjection> searchByName(String string) {
        return repo.findByNameSimilarity(string);
    }

    public ItemCatalog createItem(ItemCatalog item) {
        return repo.save(item);
    }

}
