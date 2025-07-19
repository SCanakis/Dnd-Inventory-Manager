package com.scanakispersonalprojects.dndapp.service.characterCreation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DndClass;
import com.scanakispersonalprojects.dndapp.persistance.basicCharInfo.DndClassRepo;

@Service
public class ClassService {
    
    private DndClassRepo dndClassRepo;

    public ClassService(DndClassRepo dndClassRepo) {
        this.dndClassRepo = dndClassRepo;
    }

    public List<DndClass> getAll() {
        return dndClassRepo.findAll();
    }

}
