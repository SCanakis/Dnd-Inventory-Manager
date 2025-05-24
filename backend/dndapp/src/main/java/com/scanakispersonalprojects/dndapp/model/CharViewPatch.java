package com.scanakispersonalprojects.dndapp.model;

import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CharViewPatch(
    String name,
    @JsonProperty(defaultValue = "-1") int currentHP,
    @JsonProperty(defaultValue = "-1") int tempHP,
    Map<UUID, Integer> hitDice,
    Boolean inspiration,
    Map<AbilityScore, Integer> abilityScore,
    @JsonProperty(defaultValue = "-1") int success,
    @JsonProperty(defaultValue = "-1") int failure
) {}
