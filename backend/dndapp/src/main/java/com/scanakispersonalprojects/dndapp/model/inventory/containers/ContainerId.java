package com.scanakispersonalprojects.dndapp.model.inventory.containers;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Embeddable
public class ContainerId implements Serializable{
    
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "container_uuid")
    private UUID containerUuid;

    @Column(name = "char_uuid")
    private UUID charUuid;

    public ContainerId() {}

    public ContainerId(UUID containUuid,  UUID charUuid) {
        this.containerUuid = containUuid;
        this.charUuid = charUuid;
    }

    public UUID getContainerUuid() {
        return containerUuid;
    }

    public void setContainerUuid(UUID containerUuid) {
        this.containerUuid = containerUuid;
    }

    public UUID getCharUuid() {
        return charUuid;
    }

    public void setCharUuid(UUID charUuid) {
        this.charUuid = charUuid;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ContainerId) {
            ContainerId that = (ContainerId) obj;
            return Objects.equals(that.containerUuid, this.containerUuid) &&
                Objects.equals(that.charUuid, this.charUuid);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.containerUuid, this.containerUuid);
    }
}
