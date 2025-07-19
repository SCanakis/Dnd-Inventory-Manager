package com.scanakispersonalprojects.dndapp.service.characterCreation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Subclass;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.SubClassRepo;

@Service
public class SubClassService {
    
    private SubClassRepo subClassRepo;

    public SubClassService(SubClassRepo subClassRepo) {
        this.subClassRepo = subClassRepo;
    }

    public List<Subclass> getAll() {
        return subClassRepo.findAll();
    }

    public List<Subclass> getAllForClass(UUID classUuid) {
        return subClassRepo.findByClassSource(classUuid);
    }

}
