package com.scanakispersonalprojects.dndapp.service.characterCreation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Race;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.RaceRepo;

/**
 * This service is for retriving all Race.
 * 
 * Used specifically in character creation.
 */
@Service
public class RaceService {

    /** Race Repository */
    private RaceRepo raceRepo;

    /**
     * Construcotr for dependency injection of the Race repo.
     * 
     * @param raceRepo
     */
    public RaceService(RaceRepo raceRepo) {
        this.raceRepo = raceRepo;
    }

    /**
     * Retrieves all {@link Race}
     */
    public List<Race> getAll() {
        return raceRepo.findAll();
    }

}
