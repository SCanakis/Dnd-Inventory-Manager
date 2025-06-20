package com.scanakispersonalprojects.dndapp.model.basicCharInfo;


import jakarta.persistence.*;
import java.util.UUID;


/**
 * Entity represnt the classes sql table.
 * 
 * This table represents a basic dnd class
 * 
 * The table include the following fields:
 * 
 * pk   class_uuid          UUID
 *      name                VARCHAR(50) UNIQUE
 *      hit_dice_value      String (Enum)
 *      description         TEXT
 */

@Entity
@Table(name = "class")
public class DndClass {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "class_uuid", columnDefinition = "UUID")
    private UUID classUuid;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "hit_dice_value", nullable = false)
    private HitDiceValue hitDiceValue;

    @Column(name = "description", length = 255)
    private String description;

    public DndClass() {}

    public DndClass(String name, HitDiceValue hitDiceValue) {
        this.name = name;
        this.hitDiceValue = hitDiceValue;
    }
    
    public DndClass(UUID classUuid, String name, HitDiceValue hitDiceValue, String description) {
        this.classUuid = classUuid;
        this.name = name;
        this.hitDiceValue = hitDiceValue;
        this.description = description;
    }

    public UUID getClassUuid() {
        return classUuid;
    }

    public void setClassUuid(UUID classUuid) {
        this.classUuid = classUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HitDiceValue getHitDiceValue() {
        return hitDiceValue;
    }

    public void setHitDiceValue(HitDiceValue hitDiceValue) {
        this.hitDiceValue = hitDiceValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DndClass)) return false;
        DndClass dndClass = (DndClass) o;
        return classUuid != null && classUuid.equals(dndClass.classUuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "DndClass{" +
                "classUuid=" + classUuid +
                ", name='" + name + '\'' +
                ", hitDiceValue=" + hitDiceValue +
                ", description='" + description + '\'' +
                '}';
    }
}
