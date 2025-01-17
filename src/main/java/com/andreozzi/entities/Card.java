package com.andreozzi.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "allcards")
public class Card {

    private String name;
    @Id
    private int blueprintID;

    

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBlueprintID() {
        return this.blueprintID;
    }

    public void setBlueprintID(int BlueprintID) {
        this.blueprintID = BlueprintID;
    }

    
    
}
