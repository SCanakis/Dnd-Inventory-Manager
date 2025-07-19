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


@Controller
@RequestMapping("classes")
public class DndClassController {
    
    private final static Logger LOG = Logger.getLogger(DndClassController.class.getName());
    
    private final static String GET_PATH = "GET /classes/";

    private ClassService classService;

    public DndClassController(ClassService classService) {
        this.classService = classService;
    }
    
    @GetMapping()
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
