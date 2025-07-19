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


@Controller
@RequestMapping("race")
public class RaceController {
    
    private final static Logger LOG = Logger.getLogger(RaceController.class.getName());
    
    private final static String GET_PATH = "GET /race/";

    private RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

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
