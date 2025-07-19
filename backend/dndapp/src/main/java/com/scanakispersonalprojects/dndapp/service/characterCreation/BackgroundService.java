package com.scanakispersonalprojects.dndapp.service.characterCreation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Background;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.BackgroundRepo;

@Service
public class BackgroundService {
    
    private BackgroundRepo backgroundRepo;

    public BackgroundService(BackgroundRepo backgroundRepo) {
        this.backgroundRepo = backgroundRepo;
    }


    public List<Background> getAll() {
        return backgroundRepo.findAll();
    }

}
