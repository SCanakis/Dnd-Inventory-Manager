package com.scanakispersonalprojects.dndapp.controller.inventory;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.scanakispersonalprojects.dndapp.model.inventory.containers.Container;
import com.scanakispersonalprojects.dndapp.model.inventory.containers.ContainerView;
import com.scanakispersonalprojects.dndapp.service.basicCharInfo.CustomUserDetailsService;
import com.scanakispersonalprojects.dndapp.service.inventory.ContainerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



/**
 * REST controller for managing character container operations in the D&D application.
 * Handles creating, retrieving, updating, and deleting containers for specific characters.
 * All operations require the authenticated user to own the specified character.
 * 
 * Base path: /containers/{charUuid}
 */
@Controller
@RequestMapping("containers/{charUuid}")
public class ContainerController {
    
    /** Logger for this controller */
    private final Logger LOG = Logger.getLogger(ContainerController.class.getName());
    
    
    private final static String GET_PATH =  "GET /containers/";
    private final static String POST_PATH =  "POST /containers/";
    private final static String DELETE_PATH =  "DELETE /containers/";
    private final static String PUT_PATH =  "PUT /containers/";

    /** Service for container management operations */
    private final ContainerService containerService;

    /** Service for user authentication and authorization */
    private final CustomUserDetailsService userService;    

    /**
     * Constructs a new ContainerController with the required service dependencies.
     *
     * @param containerService service for container management operations
     * @param userService service for user authentication and authorization
     */
    public ContainerController(ContainerService containerService, CustomUserDetailsService userService) {
        this.containerService = containerService;
        this.userService = userService;
    }


    /**
     * Retrieves all containers belonging to a specific character.
     *
     * @param charUuid the unique identifier of the character
     * @return ResponseEntity containing list of ContainerView if found (200 OK),
     *         404 NOT_FOUND if no containers exist for the character,
     *         or 500 INTERNAL_SERVER_ERROR on error
     */
    @GetMapping
    public ResponseEntity<List<ContainerView>> getContainers(@PathVariable("charUuid") UUID charUuid) {
        LOG.info(GET_PATH + charUuid);
        try {
            List<ContainerView> result = containerService.getCharactersContainers(charUuid);
            if(result != null && !result.isEmpty()) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a new container for the specified character.
     * Requires the authenticated user to own the character.
     *
     * @param authentication the authentication context of the current user
     * @param charUuid the unique identifier of the character
     * @param container the container data to create
     * @return ResponseEntity containing the created Container (200 OK),
     *         401 UNAUTHORIZED if user doesn't own the character,
     *         404 NOT_FOUND if creation failed,
     *         or 500 INTERNAL_SERVER_ERROR on error
     */
    @PostMapping
    public ResponseEntity<Container> createContainer(Authentication authentication, @PathVariable("charUuid") UUID charUuid, @RequestBody Container container) {
        LOG.info(POST_PATH + charUuid);
        List<UUID> characters = userService.getUsersCharacters(authentication);
        if(!characters.contains(charUuid)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            Container result = containerService.createContainer(charUuid, container);
            if(result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
    }

/**
     * Deletes a specific container belonging to a character.
     * Requires the authenticated user to own the character.
     *
     * @param authentication the authentication context of the current user
     * @param charUuid the unique identifier of the character
     * @param containerUuid the unique identifier of the container to delete
     * @return ResponseEntity with 200 OK if deletion successful,
     *         401 UNAUTHORIZED if user doesn't own the character,
     *         404 NOT_FOUND if container doesn't exist,
     *         or 500 INTERNAL_SERVER_ERROR on error
     */
    @DeleteMapping("/containerId={containerId}")
    public ResponseEntity<Boolean> deleteContainer(Authentication authentication, @PathVariable("charUuid") UUID charUuid, @PathVariable("containerId") UUID containerUuid) {
        LOG.info(DELETE_PATH + charUuid +"/containerId=" + containerUuid);

        List<UUID> UUID = userService.getUsersCharacters(authentication);
        if(!UUID.contains(charUuid)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            boolean result = containerService.deleteContainer(charUuid, containerUuid);
            if(result) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates the maximum capacity of a specific container.
     * Requires the authenticated user to own the character.
     *
     * @param authentication the authentication context of the current user
     * @param charUuid the unique identifier of the character
     * @param containerUuid the unique identifier of the container to update
     * @param maxCapacity the new maximum capacity value for the container
     * @return ResponseEntity containing the updated Container (200 OK),
     *         401 UNAUTHORIZED if user doesn't own the character,
     *         404 NOT_FOUND if container doesn't exist,
     *         or 500 INTERNAL_SERVER_ERROR on error
     */
    @PutMapping("/containerId={containerId}")
    public ResponseEntity<Container> updateMaxCapacityOfContainer(Authentication authentication, @PathVariable("charUuid") UUID charUuid, @PathVariable("containerId") UUID containerUuid, @RequestParam int maxCapacity) {
        
        LOG.info(PUT_PATH + charUuid +"/containerId=" + containerUuid);

        List<UUID> UUID = userService.getUsersCharacters(authentication);
        if(!UUID.contains(charUuid)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            Container result = containerService.updateMaxCapacityOfContainer(charUuid, containerUuid, maxCapacity);

            if(result != null) {
                return new ResponseEntity<>(result, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    
    }
    
    


}
