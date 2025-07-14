package com.scanakispersonalprojects.dndapp.service.coinPurse;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.scanakispersonalprojects.dndapp.model.coinPurse.CoinPurse;
import com.scanakispersonalprojects.dndapp.model.coinPurse.CoinPurseDTO;
import com.scanakispersonalprojects.dndapp.persistance.coinPurse.CoinPurseRepo;

@Service
public class CoinPurseService {

    private CoinPurseRepo repo;

    public CoinPurseService(CoinPurseRepo repo) {
        this.repo = repo;
    }

    public CoinPurse getCoinPurse(UUID charUuid) {
        if(charUuid != null) {
            return repo.getReferenceById(charUuid);
        } else {
            return null;
        }
    }

    public boolean updateCoinPurse(UUID charUuid, CoinPurseDTO update) {
        if(charUuid == null || update == null) {
            return false;
        }

        CoinPurse coinPurse = getCoinPurse(charUuid);

        if(update.getPlatinum() > -1) {
            coinPurse.setPlatinum(update.getPlatinum());
        }
        if(update.getGold() > -1) {
            coinPurse.setGold(update.getGold());
        }
        if(update.getElectrum() > -1) {
            coinPurse.setElectrum(update.getElectrum());
        }
        if(update.getSilver() > -1) {
            coinPurse.setSilver(update.getSilver());
        }

        repo.save(coinPurse);

        return repo.existsById(charUuid);
    }

    public void createFreshCoinPurse(UUID charUuid) {
        CoinPurse coinPurse = new CoinPurse(charUuid, 0, 0, 0, 0, 0);
        repo.save(coinPurse);
    }

}


