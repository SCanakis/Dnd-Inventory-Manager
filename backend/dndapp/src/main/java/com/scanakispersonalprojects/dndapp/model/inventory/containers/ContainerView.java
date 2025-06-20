package com.scanakispersonalprojects.dndapp.model.inventory.containers;

import java.util.List;

import com.scanakispersonalprojects.dndapp.model.inventory.characterHasItem.CharacterHasItemProjection;



/**
 * View object that combines a container with its current contents.
 * Provides a complete representation of a container including both
 * its metadata (capacity, owner, etc.) and the items it currently holds.
 * 
 * This class is typically used for displaying container information
 * in the user interface where both container details and contents
 * need to be shown together.
 */
public class ContainerView {

    private Container container;
    
    private List<CharacterHasItemProjection> items;

    public ContainerView() {
    }

    public ContainerView(Container container, List<CharacterHasItemProjection> items) {
        this.container = container;
        this.items = items;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public List<CharacterHasItemProjection> getItems() {
        return items;
    }

    public void setItems(List<CharacterHasItemProjection> items) {
        this.items = items;
    }

    
    
}
