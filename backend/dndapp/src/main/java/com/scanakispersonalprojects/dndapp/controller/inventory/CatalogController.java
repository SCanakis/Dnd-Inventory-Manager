package com.scanakispersonalprojects.dndapp.controller.inventory;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.scanakispersonalprojects.dndapp.model.inventory.ItemCatalog;
import com.scanakispersonalprojects.dndapp.model.inventory.ItemProjection;
import com.scanakispersonalprojects.dndapp.service.inventory.ItemCatalogService;



@Controller
@RequestMapping("itemCatalog")
public class CatalogController {
    
    private final static Logger LOG = Logger.getLogger(CatalogController.class.getName());

    private final ItemCatalogService service;

    public CatalogController(ItemCatalogService service) {
        this.service = service;
    }


    @GetMapping("/id={uuid}")
    public ResponseEntity<ItemCatalog> getItem(@PathVariable UUID uuid) {
        LOG.info("GET /itemCatalog/id=" + uuid);
        try {
            ItemCatalog item = service.getItemWithUUID(uuid);
            if(item == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(item, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("")
    public ResponseEntity<List<ItemProjection>> getAll() {
        LOG.info("GET /itemCatalog/");
        try {
            List<ItemProjection> items = service.getAll();
            if(items == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/searchTerm={searchTerm}")
    public ResponseEntity<List<ItemProjection>> searchByName(@PathVariable String searchTerm) {
        LOG.info("GET /itemCatalog/serachTerm=" + searchTerm);
        try {
            List<ItemProjection> items = service.searchByName(searchTerm);
            if(items == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(items, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    

}
