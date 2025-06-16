package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.*;

@Entity
@Table(name = "characters_info")
public class CharacterInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "char_info_uuid", columnDefinition = "UUID")
    private UUID charInfoUuid;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "inspiration", nullable = false)
    private Boolean inspiration = false;

    @Column(name = "race_uuid", nullable = false, columnDefinition = "UUID")
    private UUID raceUuid;

    @Column(name = "background_uuid", nullable = false, columnDefinition = "UUID")
    private UUID backgroundUuid;

    // Store ability scores as JSON
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "ability_scores", columnDefinition = "json")
    private Map<AbilityScore, Integer> abilityScores;

    // Store HP handler as JSON
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "hp_handler", columnDefinition = "json")
    private HPHandler hpHandler;

    // Store death saving throws as JSON
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "death_saving_throws", columnDefinition = "json")
    private DeathSavingThrowsHelper deathSavingThrowsHelper;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private List<CharacterClass> classes;


    // Default constructor
    public CharacterInfo() {}

    // Constructor with required fields
    public CharacterInfo(String name, UUID raceUuid, UUID backgroundUuid) {
        this.name = name;
        this.raceUuid = raceUuid;
        this.backgroundUuid = backgroundUuid;
    }

    // Full constructor
    public CharacterInfo(UUID charInfoUuid, String name, Boolean inspiration, UUID raceUuid, UUID backgroundUuid,
            Map<AbilityScore, Integer> abilityScores, HPHandler hpHandler, 
            DeathSavingThrowsHelper deathSavingThrowsHelper) {
        this.charInfoUuid = charInfoUuid;
        this.name = name;
        this.inspiration = inspiration;
        this.raceUuid = raceUuid;
        this.backgroundUuid = backgroundUuid;
        this.abilityScores = abilityScores;
        this.hpHandler = hpHandler;
        this.deathSavingThrowsHelper = deathSavingThrowsHelper;
    }

    // Getters and Setters
    public UUID getCharInfoUuid() {
        return charInfoUuid;
    }

    public void setCharInfoUuid(UUID charInfoUuid) {
        this.charInfoUuid = charInfoUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getInspiration() {
        return inspiration;
    }

    public void setInspiration(Boolean inspiration) {
        this.inspiration = inspiration;
    }

    public UUID getRaceUuid() {
        return raceUuid;
    }

    public void setRaceUuid(UUID raceUuid) {
        this.raceUuid = raceUuid;
    }

    public UUID getBackgroundUuid() {
        return backgroundUuid;
    }

    public void setBackgroundUuid(UUID backgroundUuid) {
        this.backgroundUuid = backgroundUuid;
    }

    public Map<AbilityScore, Integer> getAbilityScores() {
        return abilityScores;
    }

    public void setAbilityScores(Map<AbilityScore, Integer> abilityScores) {
        this.abilityScores = abilityScores;
    }

    public HPHandler getHpHandler() {
        return hpHandler;
    }

    public void setHpHandler(HPHandler hpHandler) {
        this.hpHandler = hpHandler;
    }

    public DeathSavingThrowsHelper getDeathSavingThrowsHelper() {
        return deathSavingThrowsHelper;
    }

    public void setDeathSavingThrowsHelper(DeathSavingThrowsHelper deathSavingThrowsHelper) {
        this.deathSavingThrowsHelper = deathSavingThrowsHelper;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharacterInfo)) return false;
        CharacterInfo that = (CharacterInfo) o;
        return charInfoUuid != null && charInfoUuid.equals(that.charInfoUuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "CharacterInfo{" +
                "charInfoUuid=" + charInfoUuid +
                ", name='" + name + '\'' +
                ", inspiration=" + inspiration +
                ", raceUuid=" + raceUuid +
                ", backgroundUuid=" + backgroundUuid +
                ", abilityScores=" + abilityScores +
                ", hpHandler=" + hpHandler +
                ", deathSavingThrowsHelper=" + deathSavingThrowsHelper +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}