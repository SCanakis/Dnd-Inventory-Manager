package com.scanakispersonalprojects.dndapp.controller.characterCreation;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Background;
import com.scanakispersonalprojects.dndapp.service.characterCreation.BackgroundService;

/**
 * REST controller for managing dnd backgrounds. Provides endpoints for retrieveing
 * backgrounds. Mainly used during the character creation proccess.
 */
@Controller
@RequestMapping("background")
public class BackgroundController {
    
    /** Logger for this controller */
    private final static Logger LOG = Logger.getLogger(BackgroundController.class.getName());
    
    private final static String GET_PATH = "GET /background/";
    
    /** Service ffor background operations */
    private BackgroundService backgroundService;

    /**
     * Constructs a new BackgroundController with the requires service dependencies
     * 
     * @param backgroundService
     */
    public BackgroundController(BackgroundService backgroundService) {
        this.backgroundService = backgroundService;
    }

    /**
     * Retrieves all Background
     * 
     * @return - 200 Response eneity containing a list of {@link Background}
     *         - 404 NOT_FOUND if null
     *         - 500 INTERNAL_SERVER_ERROR if exception occurs
     */
    @GetMapping
    public ResponseEntity<List<Background>> getAll() {
        LOG.info(GET_PATH);
        try {
            List<Background> bgs = backgroundService.getAll();
            if(bgs == null || bgs.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(bgs, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

}
