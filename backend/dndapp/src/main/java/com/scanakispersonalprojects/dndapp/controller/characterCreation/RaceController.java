package com.scanakispersonalprojects.dndapp.controller.characterCreation;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Race;
import com.scanakispersonalprojects.dndapp.service.characterCreation.RaceService;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * REST controller for managing dnd Races. Provides endpoints for retrieveing
 * Dnd Races. Mainly used during the character creation proccess.
 */
@Controller
@RequestMapping("race")
public class RaceController {
    
    /** Logger for this controller */
    private final static Logger LOG = Logger.getLogger(RaceController.class.getName());
    
    private final static String GET_PATH = "GET /race/";

    /** Service for race operations */
    private RaceService raceService;

    /**
     * Constructs a new RaceController with the requires service dependencies
     * 
     * @param raceService
     */
    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    /**
     * Retrieves all Races
     * 
     * @return - 200 Response eneity containing a list of {@link Race}
     *         - 404 NOT_FOUND if null
     *         - 500 INTERNAL_SERVER_ERROR if exception occurs
     */
    @GetMapping
    public ResponseEntity<List<Race>> getAll() {
        LOG.info(GET_PATH);
        try {
            List<Race> races = raceService.getAll();
            if(races == null || races.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(races, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
