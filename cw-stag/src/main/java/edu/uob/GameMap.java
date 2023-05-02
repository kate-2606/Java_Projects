package edu.uob;

/* -------------------------NOTES----------------------------*/
//if command contains two valid actions perform neither -- or if contains trigger word from two actions
//are command case-sensitive? --no right?
//crashes when you misspell words
//can health be a subject?
//is a command is invalid if the action consumes a location but there is not path to it form your current location?
// look into .foreach
//basic commands should have the trigger word first -- not tested
// there are no tests with punctuation



import javax.xml.stream.Location;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GameMap {
    public GameMap(){
        this.locations = new HashMap<>();
        this.players = new HashMap<>();
        this.allEntities = new HashMap<>();
    }

    private HashMap<String, GameLocation> locations;

    private GameLocation storeroom;

    private GameLocation currentLocation;

    private GameLocation startLocation;

    private GameCharacter currentPlayer;

    private HashMap<String, GameEntity> allEntities;

    private HashMap<String, GameCharacter> players;


    public void instantiatePlayer(GameCharacter player){
        player.setAsPlayer();
        moveEntity(player, startLocation);
        currentPlayer=player;
        currentLocation=startLocation;
    }

    public void addLocation(GameLocation newGameLocation) {
        if(newGameLocation.getName().equals("storeroom")){
            this.storeroom = newGameLocation;
        }
        else {
            locations.put(newGameLocation.getName(), newGameLocation);
        }
    }


    public void addEntity(GameEntity entity, GameLocation location) {

        allEntities.put(entity.getName(), entity);

        if(!entity.getName().equals("health"))
            location.addEntity(entity);
    }


    public GameEntity getEntity(String name) { return allEntities.get(name); }


    public GameLocation getLocation(String name){ return locations.get(name); }


    public GameLocation getStoreroom(){ return storeroom; }


    public int getNumberOfLocations() { return locations.size(); }


    public void setCurrentLocation(GameLocation currentLocation) { this.currentLocation = currentLocation; }


    public void setStartLocation(GameLocation startLocation) { this.startLocation = startLocation; System.out.println(startLocation.getName());}


    public GameLocation getStartLocation() { return this.startLocation; }


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



    public void moveEntity (GameEntity entity, GameLocation newLocation) {

        if (!entity.getName().equals("health") && !(entity instanceof GameLocation)) {
            if(entity.getLocation() !=null){
                entity.getLocation().removeEntity(entity.getName());
            }

            entity.setLocation(newLocation);

            if (newLocation != null) {
                newLocation.addEntity(entity);
            }

            if (newLocation == null) {
                currentPlayer.addToInventory((GameArtefact) entity);
            }
        }
    }



}
