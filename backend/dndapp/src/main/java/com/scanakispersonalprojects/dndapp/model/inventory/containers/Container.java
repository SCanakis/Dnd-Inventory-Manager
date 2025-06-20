package com.scanakispersonalprojects.dndapp.model.inventory.containers;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "container")
public class Container {
    
    @EmbeddedId
    private ContainerId id;

    @Column(name = "item_uuid", columnDefinition = "UUID")
    private UUID itemUuid;

    @Column(name = "max_capacity", nullable = false) 
    private int maxCapacity;
    
    @Column(name = "current_consumed", nullable =  false) 
    private int currentCapacity = 0;

    public Container() {
    }

    public Container(UUID containerUuid, UUID charUuid, UUID itemUuid, int maxCapacity, int currentCapacity) {
        this.id = new ContainerId(containerUuid, charUuid);
        this.itemUuid = itemUuid;
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
    }

    public ContainerId getId() {
        return id;
    }

    public UUID getCharUuid() {
        if(id == null) {
            return null;
        }
        return id.getCharUuid();
    }

    public void setCharUuid(UUID charUuid) {
        if(this.id == null) {
            this.id = new ContainerId();
        }
        this.id.setCharUuid(charUuid);
    }

    public UUID getContainerUuid() {
        if(id == null) {
            return null;
        }
        return id.getContainerUuid();
    }

    public void setContainerUuid(UUID containerUuid) {
        if(this.id == null) {
            this.id = new ContainerId();
        }
        this.id.setContainerUuid(containerUuid);
    }


    public void setId(ContainerId id) {
        this.id = id;
    }

    public UUID getItemUuid() {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid) {
        this.itemUuid = itemUuid;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getCurrentCapacity() {
        return currentCapacity;
    }

    public void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
    }



}
