package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Subclass;



/**
 * Basic JPA repo for access to the subclasses
 * table. Used for it's findById() method
 * to get full information about a subclasses
 */
@Repository
public interface SubClassRepo extends JpaRepository<Subclass, UUID>{
    

    List<Subclass> findByClassSource(UUID classSource);

}
