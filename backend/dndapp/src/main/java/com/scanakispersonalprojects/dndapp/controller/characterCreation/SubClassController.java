package com.scanakispersonalprojects.dndapp.controller.characterCreation;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Subclass;
import com.scanakispersonalprojects.dndapp.service.characterCreation.SubClassService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * REST controller for managing dnd SubClasses. Provides endpoints for retrieveing
 * Dnd SubClasses. Mainly used during the character creation proccess.
 */
@Controller
@RequestMapping("api/subclasses")
public class SubClassController {

    /** Logger for this controller */
    private final static Logger LOG = Logger.getLogger(SubClassController.class.getName());
    
    private final static String GET_PATH = "GET /subclasses/";

    /** Service for subclass operations */
    private SubClassService subClassService;

    /**
     * Constructs a new SubClassController with the requires service dependencies
     * 
     * @param subClassService
     */
    public SubClassController(SubClassService subClassService) {
        this.subClassService = subClassService;
    }
    
    /**
     * Retrieves all SubClass
     * 
     * @return - 200 Response eneity containing a list of {@link Subclass}
     *         - 404 NOT_FOUND if null
     *         - 500 INTERNAL_SERVER_ERROR if exception occurs
     */
    @GetMapping
    public ResponseEntity<List<Subclass>> getAll() {
        LOG.info(GET_PATH);
        try {
            List<Subclass> subclasses = subClassService.getAll();
            if(subclasses == null || subclasses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(subclasses, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrives all subclass for a specific class.
     * 
     * @param classUuid - source class uuid
     * @return - 200 Response eneity containing a list of {@link Subclass}
     *         - 404 NOT_FOUND if null
     *         - 500 INTERNAL_SERVER_ERROR if exception occurs
     */
    @GetMapping("/id={uuid}")
    public ResponseEntity<List<Subclass>> getSubClassesForClass(@PathVariable("uuid") UUID classUuid) { 
        LOG.info(GET_PATH + classUuid);
        try {
            List<Subclass> subclasses = subClassService.getAllForClass(classUuid);
            if(subclasses == null || subclasses.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(subclasses, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
