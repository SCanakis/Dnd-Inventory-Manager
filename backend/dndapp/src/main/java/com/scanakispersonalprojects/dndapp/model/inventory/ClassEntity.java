package com.scanakispersonalprojects.dndapp.model.inventory;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "class")
public class ClassEntity {
    @Id
    @Column(name = "class_uuid")
    private UUID classUuid;

    @Column(name = "class_name")
    private String className;

    @Column(name = "hit_dice_value")
    private Integer hitDiceValue;

    @Column(name = "description")
    private String description;

    public ClassEntity(UUID classUuid, String className, Integer hitDiceValue, String description) {
        this.classUuid = classUuid;
        this.className = className;
        this.hitDiceValue = hitDiceValue;
        this.description = description;
    }

    public ClassEntity() {}

    public UUID getClassUuid() {
        return classUuid;
    }

    public void setClassUuid(UUID classUuid) {
        this.classUuid = classUuid;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getHitDiceValue() {
        return hitDiceValue;
    }

    public void setHitDiceValue(Integer hitDiceValue) {
        this.hitDiceValue = hitDiceValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    

}
