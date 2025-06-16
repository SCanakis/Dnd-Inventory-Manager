package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DndClass;

@Repository
public interface DndClassRepo extends JpaRepository<DndClass, UUID>{
    
}
