package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.DndClass;


/**
 * Basic JPA repo for access to the classes
 * table. Used for it's findById() method
 * to get full information about a class
 */
@Repository
public interface DndClassRepo extends JpaRepository<DndClass, UUID>{
    
}
