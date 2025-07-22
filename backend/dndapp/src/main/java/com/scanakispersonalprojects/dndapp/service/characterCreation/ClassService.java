package com.scanakispersonalprojects.dndapp.service.characterCreation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DndClass;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.DndClassRepo;

/**
 * This service is for retriving all DndClasses.
 * 
 * Used specifically in character creation.
 */
@Service
public class ClassService {
    
    /** DndClass Repository */
    private DndClassRepo dndClassRepo;

    /**
     * Constructor for dependency injuection of the DndClass repo,
     * 
     * @param dndClassRepo
     */
    public ClassService(DndClassRepo dndClassRepo) {
        this.dndClassRepo = dndClassRepo;
    }

    /**
     * Retrieves all {@link DndClass}
     */
    public List<DndClass> getAll() {
        return dndClassRepo.findAll();
    }

}
