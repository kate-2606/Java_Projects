package edu.uob;

import java.util.HashMap;

public class GameMap {
    public GameMap(){
        this.locations = new HashMap<>();
    }

    HashMap<String, GameLocation> locations;

    GameLocation storeroom;

    public void addLocation(GameLocation newGameLocation) {
        if(newGameLocation.getName().equals("storeroom")){
            this.storeroom = newGameLocation;
        }
        else {
            locations.put(newGameLocation.getName(), newGameLocation);
        }
    }

    public GameLocation getLocation(String name){ return locations.get(name); }

    public int getNumberOfLocations() { return locations.size(); }

    public boolean locationExists(String name) {
        boolean res;
        res = getLocation(name) == null? false : true;
        return res;
    }

}
