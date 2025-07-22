package com.scanakispersonalprojects.dndapp.service.characterCreation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Subclass;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.SubClassRepo;

/**
 * This service is for retriving all dnd SubClasses.
 * 
 * Used specifically in character creation.
 */
@Service
public class SubClassService {
    
    /** SubClass Repository */
    private SubClassRepo subClassRepo;

    /**
     * Construcotr for dependency injection of the SubClass repo.
     * 
     * @param subClassRepo
     */
    public SubClassService(SubClassRepo subClassRepo) {
        this.subClassRepo = subClassRepo;
    }

    /**
     * Retrieves all {@link SubClass}
     */
    public List<Subclass> getAll() {
        return subClassRepo.findAll();
    }

    /**
     * 
     * Retrieves all subclasse associated with a source class
     * 
     * @param classUuid - unique identifier of source class
     * @return - Retrieves a list of {@link SubClass}
     */
    public List<Subclass> getAllForClass(UUID classUuid) {
        return subClassRepo.findByClassSource(classUuid);
    }

}
