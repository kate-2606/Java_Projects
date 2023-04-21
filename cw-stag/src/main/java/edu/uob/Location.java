package edu.uob;

import java.util.HashMap;

public class Location extends GameEntity{
    public Location(String name, String description) {
        super(name, description);
    }

    HashMap<String, Artifact> locationArtifacts;

    public void setArtifacts(HashMap<String, Artifact> localArtifacts){
        this.locationArtifacts = localArtifacts;
    }
}
