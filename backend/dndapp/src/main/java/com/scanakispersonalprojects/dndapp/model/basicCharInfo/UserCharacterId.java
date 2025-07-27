package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserCharacterId implements Serializable{
    
    @Column(name = "user_uuid")
    private UUID userUuid;

    @Column(name = "character_uuid")
    private UUID charUuid;

    public UserCharacterId() {}

    public UserCharacterId(UUID userUuid, UUID charUuid) {
        this.userUuid = userUuid;
        this.charUuid = charUuid;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public UUID getCharUuid() {
        return charUuid;
    }

    public void setCharUuid(UUID charUuid) {
        this.charUuid = charUuid;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCharacterId other = (UserCharacterId) o;
        return Objects.equals(userUuid, other.userUuid) && 
               Objects.equals(charUuid, other.charUuid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userUuid, charUuid);
    }
}
