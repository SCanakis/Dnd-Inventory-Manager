package com.scanakispersonalprojects.dndapp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents a partial update (patch) to a character's viewable fields.
 * 
 * Any fields left at its defautl indicator (null for objects, -1 for ints)
 * will be ignored by the serive layer when applying updates.
 * 
 */

public class CharViewPatch {
    private String name = null;
    private int currentHP = -1;
    private int tempHP = -1;
    private Map<UUID, Integer> hitDice = new HashMap<>();
    private Boolean inspiration = null;
    private Map<AbilityScore, Integer> abilityScore = new HashMap<>();
    private int success = -1;
    private int failure = -1;

    public CharViewPatch() {}

    /**
     * Constructs a patch from the given values, overriding only the non-null 
     * (and non-default) parameters
     * 
     * @param name          the new name, or null to leave unchanged
     * @param currentHP     the new current HP or null -> -1 to leave unchanged
     * @param tempHP        the new temp HP or null -> -1 to leave unchanged
     * @param hitDice       map of class UUID -> hit-dice count, or null to leave unchanged
     * @param inspiration   the new inspiration flag, or null to leae unchanged
     * @param abilityScore  map of {@link AbilityScore} -> new score, or null to leav unchanged
     * @param success       new death-save success count, or null -> -1 to leave unchanged
     * @param failure       new death-save failure count, or null -> -1 to leav unchanged
     */

    public CharViewPatch(
        @JsonProperty("name") String name,
        @JsonProperty("currentHP") Integer currentHP,
        @JsonProperty("tempHP") Integer tempHP,
        @JsonProperty("hitDice") Map<UUID, Integer> hitDice,
        @JsonProperty("inspiration") Boolean inspiration,
        @JsonProperty("abilityScore") Map<AbilityScore, Integer> abilityScore,
        @JsonProperty("success") Integer success,
        @JsonProperty("failure") Integer failure
    ) {
        if(name!=null) {this.name = name;}
        if(currentHP !=null) {this.currentHP = currentHP;}
        if(tempHP!=null) {this.tempHP = tempHP;}
        if(hitDice!=null) {this.hitDice = hitDice;}
        if(inspiration!=null) {this.inspiration = inspiration;}
        if(abilityScore!=null) {this.abilityScore = abilityScore;}
        if(success!=null) {this.success = success;}
        if(failure!=null) {this.failure = failure;}
    }

    public String name() {
        return name;
    }

    public int currentHP() {
        return currentHP;
    }

    public int tempHP() {
        return tempHP;
    }

    public Map<UUID, Integer> hitDice() {
        return hitDice;
    }

    public Boolean inspiration() {
        return inspiration;
    }

    public Map<AbilityScore, Integer> abilityScore() {
        return abilityScore;
    }

    public int success() {
        return success;
    }

    public int failure() {
        return failure;
    }

    



}