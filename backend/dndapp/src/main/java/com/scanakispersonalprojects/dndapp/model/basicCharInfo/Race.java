package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "race")
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "race_uuid", columnDefinition = "UUID")
    private UUID raceUuid;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "stat_increases", columnDefinition = "json")
    private Map<AbilityScore, Integer> statIncreases;

    public Race() {}

    public Race(String name, Map<AbilityScore, Integer> statIncreases) {
        this.name = name;
        this.statIncreases = statIncreases;
    }

    public Race(UUID raceUuid, String name, Map<AbilityScore, Integer> statIncreases) {
        this.raceUuid = raceUuid;
        this.name = name;
        this.statIncreases = statIncreases;
    }

    public UUID getRaceUuid() {
        return raceUuid;
    }

    public void setRaceUuid(UUID raceUuid) {
        this.raceUuid = raceUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<AbilityScore, Integer> getStatIncreases() {
        return statIncreases;
    }

    public void setStatIncreases(Map<AbilityScore, Integer> statIncreases) {
        this.statIncreases = statIncreases;
    }

    
    public Integer getStatIncrease(AbilityScore abilityScore) {
        return statIncreases != null ? statIncreases.getOrDefault(abilityScore, 0) : 0;
    }

   
    public boolean hasStatIncreases() {
        return statIncreases != null && !statIncreases.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Race)) return false;
        Race race = (Race) o;
        return raceUuid != null && raceUuid.equals(race.raceUuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Race{" +
                "raceUuid=" + raceUuid +
                ", name='" + name + '\'' +
                ", statIncreases=" + statIncreases +
                '}';
    }
}