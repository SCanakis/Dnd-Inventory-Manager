package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Subclass;

@Repository
public interface SubClassRepo extends JpaRepository<Subclass, UUID>{
    

    List<Subclass> findByClassSource(UUID classSource);

}
