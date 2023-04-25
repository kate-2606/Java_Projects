package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;

public class GameLocation extends GameEntity{
    public GameLocation(String name, String description) {
        super(name, description);
        this.locationFurniture = new HashMap<>();
        this.locationArtefacts = new HashMap<>();
        this.locationCharacters = new HashMap<>();
        this.paths = new ArrayList<>();
    }

    HashMap<String, GameArtefact> locationArtefacts;

    HashMap<String, GameFurniture> locationFurniture;

    HashMap<String, GameCharacter> locationCharacters;

    ArrayList<GameLocation> paths;

    public void addArtifact(GameArtefact newArtifact){
        this.locationArtefacts.put(newArtifact.getName(), newArtifact);
    }

    public void addFurniture(GameFurniture newGameFurniture){
        this.locationFurniture.put(newGameFurniture.getName(), newGameFurniture);
    }

    public void addCharacter(GameCharacter newGameCharacter){
        this.locationCharacters.put(newGameCharacter.getName(), newGameCharacter);
    }

    public void addPath(GameLocation pathEnd){ paths.add(pathEnd); }
}
