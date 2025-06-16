package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CharacterHasClassId implements Serializable {
    
    @Column(name = "char_info_uuid", nullable = false, columnDefinition = "UUID")
    private UUID charInfoUuid;

    @Column(name = "class_uuid", nullable = false, columnDefinition = "UUID")
    private UUID classUuid;

    public CharacterHasClassId() {}

    public CharacterHasClassId(UUID charInfoUuid, UUID classUuid) {
        this.charInfoUuid = charInfoUuid;
        this.classUuid = classUuid;
    }

    public UUID getCharInfoUuid() {
        return charInfoUuid;
    }

    public void setCharInfoUuid(UUID charInfoUuid) {
        this.charInfoUuid = charInfoUuid;
    }

    public UUID getClassUuid() {
        return classUuid;
    }

    public void setClassUuid(UUID classUuid) {
        this.classUuid = classUuid;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CharacterHasClassId) {
            CharacterHasClassId other = (CharacterHasClassId) obj;
              return Objects.equals(charInfoUuid, other.charInfoUuid) &&
                   Objects.equals(classUuid, other.classUuid); 
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(charInfoUuid, classUuid);
    }

    

}
