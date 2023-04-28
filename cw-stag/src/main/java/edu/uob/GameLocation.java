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
        newArtifact.setLocation(this);
    }


    public GameEntity getEntity(String artefact) { return locationEntities.get(artefact); }


    public boolean removeEntity (GameEntity entity){
        return locationEntities.remove(entity.getName()) ==null? false : true;
    }

    public String getAllEntitiesAsString(String entityType) {
        String result = "";

        for (Map.Entry<String, GameEntity> entity : locationEntities.entrySet()) {
            if((entityType==null || entityType.equals("artefacts")) && entity.getValue() instanceof GameArtefact)
            result = result + entity.getValue().getDescription() + "\n";

            if((entityType==null || entityType.equals("furniture")) && entity.getValue() instanceof GameFurniture)
                result = result + entity.getValue().getDescription() + "\n";

            if((entityType==null || entityType.equals("characters")) && entity.getValue() instanceof GameCharacter)
                result = result + entity.getValue().getDescription() + "\n";
        }
        return result;
    }


    public void addPath(String pathEnd){ paths.add(pathEnd); }

    public void removePath(String pathEnd){ paths.remove(pathEnd); }


    public String getAllPathsAsString() {

        String result = "";
        for (String path : paths) {
            result = result + path + "\n";
        }
        return result;
    }

    public boolean isPath(String path) { return paths.contains(path); }
}
