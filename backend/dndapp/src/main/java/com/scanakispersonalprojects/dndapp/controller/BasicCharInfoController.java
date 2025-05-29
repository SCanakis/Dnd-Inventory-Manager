package com.scanakispersonalprojects.dndapp.controller;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.scanakispersonalprojects.dndapp.model.CharViewPatch;
import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.service.CharacterService;
import com.scanakispersonalprojects.dndapp.service.CustomUserDetailsService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
@RequestMapping("character")
public class BasicCharInfoController {
    
    private static final Logger LOG = Logger.getLogger(BasicCharInfoController.class.getName());
    private CharacterService charService;
    private CustomUserDetailsService userService;

    /**
     * Constructs the controller with the persistence gateway injected.
     *
     * @param basicCharDao DAO that knows how to fetch {@link CharacterBasicInfoView}
     *                     from the data source
     */

    public BasicCharInfoController(CharacterService charService, CustomUserDetailsService userService) {
        this.charService = charService;
        this.userService = userService;
    }

    /**
     * Retrieves the immutable snapshot used by the UI’s character banner.
     *
     * @param uuid unique identifier of the character
     * @return      200 with an instance of a {@link CharacterBasicInfoView} retrived
     *              404 when the character does not exist
     *              500 on any unexpected server error
     */

    @GetMapping("/{uuid}")
    public ResponseEntity<CharacterBasicInfoView> getCharacterBasicView(@PathVariable UUID uuid) {
        LOG.info("GET /character/" + uuid);
        try {
            CharacterBasicInfoView charInfoView = charService.getCharInfo(uuid);
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

    /**
     * 
     * Update some of the values that will frequently be changed in the BasicCharInfo
     * 
     * @param uuid          unique idneitifer of the character
     * @param patch         The filed that are to be updated
     * @return              200 with an instance of the updated {@link CharacterBasicInfoView}
     *                      401 if user not authorized
     *                      404 when the character does not exist
     *                      500 on any unexpected server error
     */


    @PutMapping("/{uuid}")
    public ResponseEntity<CharacterBasicInfoView> updateCharacterBasicView(Authentication authentication, @PathVariable UUID uuid , @RequestBody CharViewPatch patch) {
        LOG.info("PUT /character/" + uuid);
        List<UUID> characters = userService.getUsersCharacters(authentication);
        if(!characters.contains(uuid)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            CharacterBasicInfoView charInfoView = charService.updateCharInfo(uuid, patch);
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

   /**
    * 
    * Deletes a character and it's association with a User
    *
    * @param authentication     this endpoint requires that the user authenticating is the owner of the character
    * @param uuid               the UUID of the character
    * @return                   200 if deleted
    *                           401 if user not authorized
    *                           404 when the character not found
    *                           500 on any unexpected server error
    */

    @DeleteMapping("/{uuid}")
    public ResponseEntity<CharacterBasicInfoView> deleteCharacter(Authentication authentication ,@PathVariable UUID uuid) {
        
        List<UUID> characters = userService.getUsersCharacters(authentication);
        if(!characters.contains(uuid)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
 
        UUID userUuid = userService.getUsersUuid(authentication);

        try {
            boolean result = charService.deleteCharacter(userUuid, uuid);
            if(result != false) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

         
    }


}
