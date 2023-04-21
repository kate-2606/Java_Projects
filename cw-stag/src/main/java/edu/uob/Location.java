package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;

public class Location extends GameEntity{
    public Location(String name, String description) {
        super(name, description);
        this.locationFurniture = new HashMap<>();
        this.locationArtefacts = new HashMap<>();
        this.locationCharacters = new HashMap<>();
    }

    HashMap<String, Artefact> locationArtefacts;

    HashMap<String, Furniture> locationFurniture;

    HashMap<String, Character> locationCharacters;

    ArrayList<Location> paths;

    public void addArtifact(Artefact newArtifact){
        this.locationArtefacts.put(newArtifact.getName(), newArtifact);
    }

    public void addFurniture(Furniture newFurniture){
        this.locationFurniture.put(newFurniture.getName(), newFurniture);
    }

    public void addCharacter(Character newCharacter){
        this.locationCharacters.put(newCharacter.getName(), newCharacter);
    }

    public void addPath(Location pathEnd){ paths.add(pathEnd); }
}
