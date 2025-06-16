package com.scanakispersonalprojects.dndapp.model.basicCharInfo;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "background")
public class Background {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "background_uuid", columnDefinition = "UUID")
    private UUID backgroundUuid;

    @Column(name = "name", length = 50, nullable = false, unique = true)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "starting_gold")
    private Short startingGold;

    public Background() {}

    public Background(String name) {
        this.name = name;
    }

    public Background(String name, String description, Short startingGold) {
        this.name = name;
        this.description = description;
        this.startingGold = startingGold;
    }

    public Background(UUID backgroundUuid, String name, String description, Short startingGold) {
        this.backgroundUuid = backgroundUuid;
        this.name = name;
        this.description = description;
        this.startingGold = startingGold;
    }


    public UUID getBackgroundUuid() {
        return backgroundUuid;
    }

    public void setBackgroundUuid(UUID backgroundUuid) {
        this.backgroundUuid = backgroundUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Short getStartingGold() {
        return startingGold;
    }

    public void setStartingGold(Short startingGold) {
        this.startingGold = startingGold;
    }

    public boolean hasStartingGold() {
        return startingGold != null && startingGold > 0;
    }

    
    public int getStartingGoldValue() {
        return startingGold != null ? startingGold : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Background)) return false;
        Background that = (Background) o;
        return backgroundUuid != null && backgroundUuid.equals(that.backgroundUuid);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Background{" +
                "backgroundUuid=" + backgroundUuid +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startingGold=" + startingGold +
                '}';
    }
}
