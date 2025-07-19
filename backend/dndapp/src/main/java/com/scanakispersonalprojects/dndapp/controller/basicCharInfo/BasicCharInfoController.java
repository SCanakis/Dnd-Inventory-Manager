package com.scanakispersonalprojects.dndapp.controller.basicCharInfo;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.BasicCharInfoCreationDTO;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterInfoUpdateDTO;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CharacterInfoService;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;





@Controller
@RequestMapping("character")
public class BasicCharInfoController {
    
    private static final Logger LOG = Logger.getLogger(BasicCharInfoController.class.getName());
    private CharacterInfoService characterInfoService;
    private CustomUserDetailsService userService;
    private final static String GET_PATH = "GET /characters/";
    private final static String PUT_PATH = "PUT /characters/";
    private final static String DELETE_PATH = "DELETE /characters/";
    private final static String POST_PATH = "POST /characters/";
   
    /**
     * Contructor instantiates the {@link CustomUserDetailsService} and 
     * {@link CharacterService} with are required for authentication and
     * character modification
     * 
     * @param charService       {} used for character retrival, update, and delition
     * 
     * @param userService       {@link CustomUserDetailsService} used for
     * authentication
     */


    public BasicCharInfoController(CharacterInfoService characterInfoService, CustomUserDetailsService userService) {
        this.characterInfoService = characterInfoService;
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
        LOG.info(GET_PATH + uuid);
        try {
            CharacterBasicInfoView charInfoView = characterInfoService.getCharacterBasicInfoView(uuid);
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
     *                      500 on any unexpected server error
     */


    @PutMapping("/{uuid}")
    public ResponseEntity<CharacterBasicInfoView> updateCharacterBasicView(Authentication authentication, @PathVariable UUID uuid , @RequestBody CharacterInfoUpdateDTO patch) {
        LOG.info(PUT_PATH + uuid);
        List<UUID> characters = userService.getUsersCharacters(authentication);
        if(!characters.contains(uuid)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            CharacterBasicInfoView charInfoView = characterInfoService.updateUsingPatch(uuid, patch);
            if (charInfoView != null) {
                return new ResponseEntity<CharacterBasicInfoView>(charInfoView, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } 
        } catch (Exception e) {
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
    public ResponseEntity<CharacterBasicInfoView> deleteCharacter(Authentication authentication, @PathVariable UUID uuid) {
        LOG.info(DELETE_PATH + uuid);

        List<UUID> characters = userService.getUsersCharacters(authentication);
        if(!characters.contains(uuid)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            
            UUID userUuid = userService.getUsersUuid(authentication);
            boolean result = characterInfoService.deleteCharacter(uuid, userUuid);

            if(result != false) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
            
    }

    @PostMapping()
    public ResponseEntity<Boolean> createCharacter(Authentication authentication, @RequestBody BasicCharInfoCreationDTO dto) {

        UUID userUuid = userService.getUsersUuid(authentication);
        LOG.info(POST_PATH + " for user: "+ userUuid);

        try {
            Boolean result = characterInfoService.createCharacter(userUuid, dto);
            if(result) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }



    }
    


}
