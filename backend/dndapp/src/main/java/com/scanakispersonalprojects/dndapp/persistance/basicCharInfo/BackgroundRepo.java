package com.scanakispersonalprojects.dndapp.persistance.basicCharInfo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.basicCharInfo.Background;
import java.util.List;


@Repository
public interface BackgroundRepo extends JpaRepository<Background, UUID>{

    List<Background> findByName(String name);
}
