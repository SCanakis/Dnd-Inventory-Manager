package com.scanakispersonalprojects.dndapp.service.characterCreation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Race;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.RaceRepo;

@Service
public class RaceService {
    

    private RaceRepo raceRepo;

    public RaceService(RaceRepo raceRepo) {
        this.raceRepo = raceRepo;
    }

    public List<Race> getAll() {
        return raceRepo.findAll();
    }

}
