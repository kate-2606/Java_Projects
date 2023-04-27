package edu.uob;

import com.sun.jdi.ClassType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.Class;

public class GameLocation extends GameEntity{
    public GameLocation(String name, String description) {
        super(name, description, null);
        /*
        this.locationFurniture = new HashMap<>();
        this.locationArtefacts = new HashMap<>();
        this.locationCharacters = new HashMap<>();

         */
        this.locationEntities = new HashMap<>();
        this.paths = new ArrayList<>();
    }

/*
    HashMap<String, GameArtefact> locationArtefacts;

    HashMap<String, GameFurniture> locationFurniture;

    HashMap<String, GameCharacter> locationCharacters;
 */
    HashMap<String, GameEntity> locationEntities;
    ArrayList<String> paths;


    public void addEntity(GameEntity newArtifact){ this.locationEntities.put(newArtifact.getName(), newArtifact); }


    public GameEntity getEntity(String artefact) { return locationEntities.get(artefact); }

    public boolean removeEntity (GameEntity entity){
        return locationEntities.remove(entity.getName()) ==null? false : true;
    }
/*
    public boolean removeFurniture(GameFurniture furniture){
        return locationFurniture.remove(furniture.getName()) == null ? false : true;
    }

    public boolean removeCharacter(GameCharacter character){
        return locationCharacters.remove(character.getName()) == null ? false : true;
    }

    public HashMap<String, GameArtefact> getAllArtefacts() { return locationArtefacts; }

 */
    public String getAllEntitiesAsString(String entityType) {
        String result = "";

        for (Map.Entry<String, GameEntity> entity : locationEntities.entrySet()) {
            if((entityType==null || entityType.equals("GameArtifact")) && entity.getValue() instanceof GameArtefact)
            result = result + entity.getValue().getDescription() + "\n";

            if((entityType==null || entityType.equals("GameFurniture")) && entity.getValue() instanceof GameFurniture)
                result = result + entity.getValue().getDescription() + "\n";

            if((entityType==null || entityType.equals("GameCharacter")) && entity.getValue() instanceof GameCharacter)
                result = result + entity.getValue().getDescription() + "\n";
        }
        return result;
    }

    /*
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

     */

    public void addPath(String pathEnd){ paths.add(pathEnd); }

    public String getAllPathsAsString() {

        String result = "";
        for (String path : paths) {
            result = result + path + "\n";
        }
        return result;
    }

    public boolean isPath(String path) { return paths.contains(path); }
}
