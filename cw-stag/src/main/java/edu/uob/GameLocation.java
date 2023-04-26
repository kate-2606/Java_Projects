package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameLocation extends GameEntity{
    public GameLocation(String name, String description) {
        super(name, description);
        this.locationFurniture = new HashMap<>();
        this.locationArtefacts = new HashMap<>();
        this.locationCharacters = new HashMap<>();
        this.paths = new ArrayList<>();
    }

    String description;

    HashMap<String, GameArtefact> locationArtefacts;

    HashMap<String, GameFurniture> locationFurniture;

    HashMap<String, GameCharacter> locationCharacters;

    ArrayList<String> paths;


    public void addArtefact(GameArtefact newArtifact){
        this.locationArtefacts.put(newArtifact.getName(), newArtifact);
    }

    public int getNumberOfArtefacts() { return locationArtefacts.size(); }

    public GameArtefact getArtefact(String artefact) { return locationArtefacts.get(artefact); }

    public void removeArtefact(GameArtefact artefact){ locationArtefacts.remove(artefact.getName()); }

    public HashMap<String, GameArtefact> getAllArtefacts() { return locationArtefacts; }

    public String getAllArtefactsAsString() {
        String result = "";
        for (Map.Entry<String, GameArtefact> artefact : locationArtefacts.entrySet()) {
            result = result + artefact.getValue().getDescription() + "\n";
        }
        return result;
    }

    public void addFurniture(GameFurniture newGameFurniture){
        this.locationFurniture.put(newGameFurniture.getName(), newGameFurniture);
    }

    public int getNumberOfFurniture() { return locationFurniture.size(); }

    public GameFurniture getFurniture(String furniture) { return locationFurniture.get(furniture); }

    public String getAllFurnitureAsString() {
        String result = "";
        for (Map.Entry<String, GameFurniture> furniture : locationFurniture.entrySet()) {
            result = result + furniture.getValue().getDescription() + "\n";
        }
        return result;
    }

    public void addCharacter(GameCharacter newGameCharacter){
        this.locationCharacters.put(newGameCharacter.getName(), newGameCharacter);
    }

    public int getNumberOfCharacters() { return locationCharacters.size(); }

    public String getAllCharactersAsString() {
        String result = "";
        for (Map.Entry<String, GameCharacter> character : locationCharacters.entrySet()) {
            result = result + character.getValue().getDescription() + "\n";
        }
        return result;
    }

    public GameCharacter getCharacter(String character) { return locationCharacters.get(character); }

    public void addPath(String pathEnd){ paths.add(pathEnd); }

    public String getAllPathsAsString() {

        String result = "";
        for (String path : paths) {
            result = result + path + "\n";
        }
        return result;
    }

    public int getNumberOfPaths() { return locationArtefacts.size(); }
}
