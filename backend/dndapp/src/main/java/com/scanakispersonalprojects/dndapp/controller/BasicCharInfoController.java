package com.scanakispersonalprojects.dndapp.controller;

import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.scanakispersonalprojects.dndapp.model.CharacterBasicInfoView;
import com.scanakispersonalprojects.dndapp.persistance.BasicCharInfoPersistance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("character")
public class BasicCharInfoController {
    
    private static final Logger LOG = Logger.getLogger(BasicCharInfoController.class.getName());
    private BasicCharInfoPersistance basicCharDao;

    public BasicCharInfoController(BasicCharInfoPersistance basicCharDao) {
        this.basicCharDao = basicCharDao;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<CharacterBasicInfoView> getMethodName(@PathVariable String uuid) {
        LOG.info("GET /character/" + uuid);
        try {
            CharacterBasicInfoView charInfoView = basicCharDao.getCharInfo(uuid);
            if (charInfoView != null) {
                return new ResponseEntity<CharacterBasicInfoView>(charInfoView, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } 

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

}
