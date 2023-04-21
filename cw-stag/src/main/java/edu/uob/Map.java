package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;

public class Map {
    public Map(){
        this.locations = new HashMap<>();
    }

    HashMap<String, Location> locations;

    Location storeroom;

    public void addLocation(Location newLocation) {
        if(newLocation.getName().equals("storeroom")){
            this.storeroom = newLocation;
        }
        else {
            locations.put(newLocation.getName(), newLocation);
        }
    }

    public Location getLocation(String name){ return locations.get(name); }

}
