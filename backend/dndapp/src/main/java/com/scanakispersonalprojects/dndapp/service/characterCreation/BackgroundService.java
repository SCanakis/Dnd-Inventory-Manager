package com.scanakispersonalprojects.dndapp.service.characterCreation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Background;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.BackgroundRepo;

/**
 * This service is for retriving all backgrounds.
 * 
 * Used specifically in character creation.
 */
@Service
public class BackgroundService {
    
    /** Backgrounds Repository */
    private BackgroundRepo backgroundRepo;

    /**
     * Contructor for dependency injection of the background repo.
     * 
     * @param backgroundRepo
     */
    public BackgroundService(BackgroundRepo backgroundRepo) {
        this.backgroundRepo = backgroundRepo;
    }

    /**
     * Retrives all {@link Background}
     */
    public List<Background> getAll() {
        return backgroundRepo.findAll();
    }

}
