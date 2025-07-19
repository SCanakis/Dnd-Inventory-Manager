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


@Controller
@RequestMapping("background")
public class BackgroundController {
    
    private final static Logger LOG = Logger.getLogger(BackgroundController.class.getName());
    
    private final static String GET_PATH = "GET /background/";
    
    private BackgroundService backgroundService;

    public BackgroundController(BackgroundService backgroundService) {
        this.backgroundService = backgroundService;
    }

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
