package edu.uob;

import com.sun.jdi.ClassType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.Class;

public class GameLocation extends GameEntity{
    public GameLocation(String name, String description, GameLocation location) {
        super(name, description, null);
        this.locationEntities = new HashMap<>();
        this.paths = new ArrayList<>();
    }

    HashMap<String, GameEntity> locationEntities;
    ArrayList<String> paths;


    public void addEntity(GameEntity newArtifact){
        this.locationEntities.put(newArtifact.getName(), newArtifact);
    }


    public GameEntity getEntity(String artefact) { return locationEntities.get(artefact); }

    public void removeEntity(String entity) { locationEntities.remove(entity); }

    public String getAllEntitiesAsString(GameEntity currentPlayer) {

        String artefacts = "";
        String furniture = "";
        String characters= "";

        for (Map.Entry<String, GameEntity> entity : locationEntities.entrySet()) {
            if(entity.getValue() instanceof GameArtefact)
                artefacts = artefacts + entity.getValue().getDescription() + "\n";

            if(entity.getValue() instanceof GameFurniture)
                furniture = furniture + entity.getValue().getDescription() + "\n";

            if(entity.getValue() instanceof GameCharacter && entity.getValue()!=currentPlayer)
                characters = characters + entity.getValue().getDescription() + "\n";
        }
        return artefacts + furniture + characters;
    }


    public void addPath(String pathEnd){ paths.add(pathEnd); }

    public boolean removePath(String pathEnd){ return paths.remove(pathEnd); }


    public String getAllPathsAsString() {

        String result = "";
        for (String path : paths) {
            result = result + path + "\n";
        }
        return result;
    }

    public boolean isPath(String path) { return paths.contains(path); }
}
