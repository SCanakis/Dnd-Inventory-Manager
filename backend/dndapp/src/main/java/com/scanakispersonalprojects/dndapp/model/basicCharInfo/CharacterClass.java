package com.scanakispersonalprojects.dndapp.model.basicCharInfo;


import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entity represnt a character__class sql table.
 * This table represent the relationship between
 * characters and their classes
 * 
 * The table include the following fields:
 * 
 * pk   char_info_uuidU     UUID
 * pk   class_uuid          UUID
 *      subclass_uuid       UUID
 *      level               SMALLINT
 *      hit_dice_remaining  SMALLINT
 */
@Entity
@Table(name = "character_class")
public class CharacterClass {

    @EmbeddedId
    private CharacterHasClassId id;

    @Column(name = "subclass_uuid", columnDefinition = "UUID")
    private UUID subclassUuid;

    @Column(name = "level", nullable = false)
    private Short level = 1;

    @Column(name = "hit_dice_remaining", nullable = false)
    private Short hitDiceRemaining;

    public CharacterClass() {}

    public CharacterClass(UUID charInfoUuid, UUID classUuid, Short level, Short hitDiceRemaining) {
        this.id = new CharacterHasClassId(charInfoUuid, classUuid);
        this.level = level;
        this.hitDiceRemaining = hitDiceRemaining;
    }

    public CharacterClass(UUID charInfoUuid, UUID classUuid, UUID subclassUuid, 
                         Short level, Short hitDiceRemaining) {
        this.id = new CharacterHasClassId(charInfoUuid, classUuid);
        this.subclassUuid = subclassUuid;
        this.level = level;
        this.hitDiceRemaining = hitDiceRemaining;
    }

    public CharacterHasClassId getId() {
        return id;
    }

    public void setId(CharacterHasClassId id) {
        this.id = id;
    }

    public UUID getCharInfoUuid() {
        return id != null ? id.getCharInfoUuid() : null;
    }

    public void setCharInfoUuid(UUID charInfoUuid) {
        if (this.id == null) {
            this.id = new CharacterHasClassId();
        }
        this.id.setCharInfoUuid(charInfoUuid);
    }

    public UUID getClassUuid() {
        return id != null ? id.getClassUuid() : null;
    }

    public void setClassUuid(UUID classUuid) {
        if (this.id == null) {
            this.id = new CharacterHasClassId();
        }
        this.id.setClassUuid(classUuid);
    }

    public UUID getSubclassUuid() {
        return subclassUuid;
    }

    public void setSubclassUuid(UUID subclassUuid) {
        this.subclassUuid = subclassUuid;
    }

    public Short getLevel() {
        return level;
    }

    public void setLevel(Short level) {
        this.level = level;
    }

    public Short getHitDiceRemaining() {
        return hitDiceRemaining;
    }

    public void setHitDiceRemaining(Short hitDiceRemaining) {
        this.hitDiceRemaining = hitDiceRemaining;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharacterClass)) return false;
        CharacterClass that = (CharacterClass) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CharacterClass{" +
                "charInfoUuid=" + getCharInfoUuid() +
                ", classUuid=" + getClassUuid() +
                ", subclassUuid=" + subclassUuid +
                ", level=" + level +
                ", hitDiceRemaining=" + hitDiceRemaining +
                '}';
    }
}