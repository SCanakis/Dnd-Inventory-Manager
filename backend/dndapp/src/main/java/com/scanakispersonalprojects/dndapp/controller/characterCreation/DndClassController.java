package com.scanakispersonalprojects.dndapp.controller.characterCreation;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DndClass;
import com.scanakispersonalprojects.dndapp.service.characterCreation.ClassService;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * REST controller for managing dnd DndClasses. Provides endpoints for retrieveing
 * Dnd Classes. Mainly used during the character creation proccess.
 */
@Controller
@RequestMapping("classes")
public class DndClassController {
    
    /** Logger for this controller */
    private final static Logger LOG = Logger.getLogger(DndClassController.class.getName());
    
    private final static String GET_PATH = "GET /classes/";

    /** Service for background operations */
    private ClassService classService;

    /**
     * Constructs a new DndClassController with the requires service dependencies
     * 
     * @param classService
     */
    public DndClassController(ClassService classService) {
        this.classService = classService;
    }
    
    /**
     * Retrieves all DndClasses
     * 
     * @return - 200 Response eneity containing a list of {@link DndClass}
     *         - 404 NOT_FOUND if null
     *         - 500 INTERNAL_SERVER_ERROR if exception occurs
     */
    @GetMapping
    public ResponseEntity<List<DndClass>> getAll() {
        LOG.info(GET_PATH);
        try {
            List<DndClass> classes = classService.getAll();
            if(classes == null || classes.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(classes, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

}
