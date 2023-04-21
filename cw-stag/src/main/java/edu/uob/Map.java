package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;

public class Map {
    public Map(){
        this.locations = new HashMap<>();
    }

    HashMap<String, Location> locations;

    public void addLocation(Location newLocation) { locations.put(newLocation.getName(), newLocation); }


}
