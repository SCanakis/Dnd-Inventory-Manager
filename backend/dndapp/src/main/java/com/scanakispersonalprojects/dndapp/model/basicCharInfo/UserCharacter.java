package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import java.util.UUID;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users_characters")
public class UserCharacter {
    
    @EmbeddedId
    private UserCharacterId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "char_info_uuid", insertable = false, updatable = false)
    private CharacterInfo character;;

    public UserCharacter(UUID userUuid, UUID characterUuid) {
        this.id = new UserCharacterId(userUuid, characterUuid);
    }

    public UserCharacterId getId() {
        return id;
    }

    public void setId(UserCharacterId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CharacterInfo getCharacter() {
        return character;
    }

    public void setCharacter(CharacterInfo character) {
        this.character = character;
    }


}
