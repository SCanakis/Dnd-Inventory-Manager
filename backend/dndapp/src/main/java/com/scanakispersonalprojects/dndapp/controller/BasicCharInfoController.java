package com.scanakispersonalprojects.dndapp.controller;

import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.scanakispersonalprojects.dndapp.model.CharViewPatch;
import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.persistance.CharacterService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequestMapping("character")
public class BasicCharInfoController {
    
    private static final Logger LOG = Logger.getLogger(BasicCharInfoController.class.getName());
    private CharacterService service;

    /**
     * Constructs the controller with the persistence gateway injected.
     *
     * @param basicCharDao DAO that knows how to fetch {@link CharacterBasicInfoView}
     *                     from the data source
     */

    public BasicCharInfoController(CharacterService service) {
        this.service = service;
    }

   /**
     * Retrieves the immutable snapshot used by the UI’s character banner.
     *
     * @param uuid unique identifier of the character
     * @return 200 with the body when found;
     *         404 when the character does not exist;
     *         500 on any unexpected server error
     */

    @GetMapping("/{uuid}")
    public ResponseEntity<CharacterBasicInfoView> getCharacterBasicView(@PathVariable UUID uuid) {
        LOG.info("GET /character/" + uuid);
        try {
            CharacterBasicInfoView charInfoView = service.getCharInfo(uuid);
            if (charInfoView != null) {
                return new ResponseEntity<CharacterBasicInfoView>(charInfoView, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } 

        } catch (Exception e) {
            LOG.severe(e::getMessage);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

// What I actually want to update: health, name, hitDice, Inspiration, AbilityScore, and DeathSaving throw

   @PutMapping("/{uuid}")
   public ResponseEntity<CharacterBasicInfoView> updateCharacterBasicView(@PathVariable UUID uuid , @RequestBody CharViewPatch updatedView) {
            LOG.info("PUT /character/" + uuid);
        try {
            CharacterBasicInfoView charInfoView = service.updateCharInfo(uuid, updatedView);
            if (charInfoView != null) {
                return new ResponseEntity<CharacterBasicInfoView>(charInfoView, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } 

        } catch (Exception e) {
            LOG.severe(e::getMessage);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
   }

}
