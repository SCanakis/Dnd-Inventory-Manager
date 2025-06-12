package com.scanakispersonalprojects.dndapp.controller.inventory;

import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.scanakispersonalprojects.dndapp.model.inventory.Item;


@Controller
@RequestMapping("/itemCatalog/")
public class CatalogController {
    
    private final static Logger LOG = Logger.getLogger(CatalogController.class.getName());

    @GetMapping("{UUID}/")
    public ResponseEntity<Item> getItem(@PathVariable UUID uuid) {
        LOG.info("GET /itemCatalog/{UUID}" + uuid);
        return null;
    }
    


}
