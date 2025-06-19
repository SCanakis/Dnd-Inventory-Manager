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




@Controller
@RequestMapping("containers/{charUuid}")
public class ContainerController {
    
    private final Logger LOG = Logger.getLogger(ContainerController.class.getName());
    

    private final static String getPath =  "GET /containers/";
    private final static String postPath =  "POST /containers/";
    private final static String deletePath =  "DELETE /containers/";
    private final static String putPath =  "PUT /containers/";

    private final ContainerService containerService;
    private final CustomUserDetailsService userService;    

    public ContainerController(ContainerService containerService, CustomUserDetailsService userService) {
        this.containerService = containerService;
        this.userService = userService;
    }



    @GetMapping()
    public ResponseEntity<List<ContainerView>> getContainers(@PathVariable("charUuid") UUID charUuid) {
        LOG.info(getPath + charUuid);
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

    @PostMapping
    public ResponseEntity<Container> createContainer(Authentication authentication, @PathVariable("charUuid") UUID charUuid, @RequestBody Container container) {
        LOG.info(postPath + charUuid);
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


    @DeleteMapping("/containerId={containerId}")
    public ResponseEntity<Boolean> deleteContainer(Authentication authentication, @PathVariable("charUuid") UUID charUuid, @PathVariable("containerId") UUID containerUuid) {
        LOG.info(deletePath + charUuid +"/containerId=" + containerUuid);

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

    @PutMapping("/containerId={containerId}")
    public ResponseEntity<Container> updateMaxCapacityOfContainer(Authentication authentication, @PathVariable("charUuid") UUID charUuid, @PathVariable("containerId") UUID containerUuid, @RequestParam int maxCapacity) {
        
        LOG.info(putPath + charUuid +"/containerId=" + containerUuid);

        List<UUID> UUID = userService.getUsersCharacters(authentication);
        if(!UUID.contains(charUuid)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            return null;
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    
    }
    
    


}
