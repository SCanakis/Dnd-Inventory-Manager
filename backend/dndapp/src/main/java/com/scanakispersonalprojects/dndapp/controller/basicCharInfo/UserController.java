package com.scanakispersonalprojects.dndapp.controller.basicCharInfo;


import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CreateUserDTO;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.CustomUserPrincipal;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;




@Controller
@RequestMapping("api")
public class UserController {
    private static final Logger LOG = Logger.getLogger(BasicCharInfoController.class.getName());
    private CustomUserDetailsService detailsService;


    /**
     * Contructor instantiates the {@link CustomUserDetailsService} needed for
     * authentication
     * 
     * @param detailsService Service to retrieve user information 
     */
    public UserController(CustomUserDetailsService detailsService) {
        this.detailsService = detailsService;
    }

    /**
     * Retrieves all character from authentication details
     * 
     * 
     * @param authentication    the endpoint requires authentication to retrieve 
     * characters
     * 
     * @return                  200 retieves characters
     *                          401 if user did not authenticate
     *                          404 when the characters are not found
     *                          500 on any unexpected server error
     */

    @GetMapping("/characters/")
    @ResponseBody
    public ResponseEntity<List<CharacterBasicInfoView>> home(Authentication authentication) {
        LOG.info("GET /character/");
        try {
            CustomUserPrincipal user = (CustomUserPrincipal) detailsService.loadUserByUsername(authentication.getName());
            if(user == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<List<CharacterBasicInfoView>> (user.getCharacters(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }   

    @PostMapping("/auth/create-user")
    public ResponseEntity<Boolean> createUser(@RequestBody CreateUserDTO createUserDTO) {
        LOG.info("POST /user");

        try {
            if(detailsService.createUser(createUserDTO)) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            }
            
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/auth/user-deletion")
    public ResponseEntity<Boolean> deleteUser(Authentication authentication) {
        LOG.info("DELETE /user-deletion");

        try {
            if(detailsService.deleteUser(authentication)) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    
    

}
