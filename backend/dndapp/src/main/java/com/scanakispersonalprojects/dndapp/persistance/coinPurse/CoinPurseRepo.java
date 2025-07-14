package com.scanakispersonalprojects.dndapp.persistance.coinPurse;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scanakispersonalprojects.dndapp.model.coinPurse.CoinPurse;

@Repository
public interface CoinPurseRepo extends JpaRepository<CoinPurse, UUID> {

    
}
