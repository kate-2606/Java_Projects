package edu.uob;

/* -------------------------NOTES----------------------------*/
//if command contains two valid actions perform neither
//pass characters (this) as a reference to their location?
//when doing look you can see other players, but not yourself--?
//currently "looko" works like look
//look at exceptions throw in actions file tests
//are command case-sensitive? --no right?
//crashes when you mispell words


import javax.xml.stream.Location;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameMap {
    public GameMap(){
        this.locations = new HashMap<>();
        this.players = new HashMap<>();
    }

    private HashMap<String, GameLocation> locations;

    private GameLocation storeroom;

    private GameLocation currentLocation;

    private GameLocation startLocation;

    private GameCharacter currentPlayer;


    private HashMap<String, GameArtefact> allArtefacts;


    private HashMap<String, GameFurniture> allFurniture;


    private HashMap<String, GameCharacter> allCharacters;

    private HashMap<String, GameCharacter> players;

    public void addLocation(GameLocation newGameLocation) {
        if(newGameLocation.getName().equals("storeroom")){
            this.storeroom = newGameLocation;
        }
        else {
            locations.put(newGameLocation.getName(), newGameLocation);
        }
    }

    public GameLocation getLocation(String name){ return locations.get(name); }


    public GameLocation getStoreroom(){ return storeroom; }


    public int getNumberOfLocations() { return locations.size(); }


    public void setCurrentLocation(GameLocation currentLocation) { this.currentLocation = currentLocation; }


    public void setStartLocation(GameLocation startLocation) { this.startLocation = startLocation; }


    public GameLocation getStartLocation() { return this.startLocation; }

    public void setStoreroom (GameLocation storeroomLocation) { this.storeroom = storeroomLocation; }


    public GameLocation getCurrentLocation() {return currentLocation; }


    public GameCharacter findPlayer(String name) {

        GameCharacter player = players.get(name);
        if(player != null){
            this.currentPlayer = player;
        }
        return player;
    }

    public GameCharacter getCurrentPlayer() { return this.currentPlayer; }

    public void setCurrentPlayer(GameCharacter player) { this.currentPlayer = player; }


    public void addPlayer(String name) {

        GameCharacter player = new GameCharacter(name, "A player called " + name, startLocation);
        players.put(player.getName(), player);
    }

    public void removeFromAllLocations(GameArtefact artefact, GameFurniture furniture, GameCharacter character) {

        for (Map.Entry<String, GameLocation> location : locations.entrySet()) {

            if (artefact != null &&location.getValue().removeArtefact(artefact)) {
                return;
            }
            if (furniture != null && location.getValue().removeFurniture(furniture)) {
                return;
            }
            if (artefact != null &&location.getValue().removeArtefact(artefact)) {
                return;
            }
        }
    }

    public void removeFurnitureFromAllLocations(GameArtefact artefact) {

        for (Map.Entry<String, GameLocation> location : locations.entrySet()) {

            if (location.getValue().removeArtefact(artefact)) {
                return;
            }
        }
    }


}
