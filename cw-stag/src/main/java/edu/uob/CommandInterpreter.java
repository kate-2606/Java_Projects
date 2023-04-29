package edu.uob;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CommandInterpreter {
    public CommandInterpreter(GameMap map, GameCharacter currentPlayer, ActionLibrary customActions) {
        this.map = map;
        this.currentPlayer = currentPlayer;
        this.customActions = customActions;
        map.setCurrentPlayer(this.currentPlayer);
    }

    GameMap map;
    GameCharacter currentPlayer;
    GameLocation currentLocation;
    ActionLibrary customActions;


    public String handleCommand (String inpCommand){

        this.currentLocation = map.getCurrentLocation();

        String response = "";

        String command = " "+inpCommand.toLowerCase()+" ";

        try {
            if (command.contains("inv")) {
                command = command.replaceAll("\\binv\\b", "inventory");
            }
            if (command.contains("goto")) {
                command = command.replaceAll("\\bgoto\\b", "_goto");
            }

            BasicCommand trigger = containsBasicCommand(command);

            if (trigger != null) {
                response = handleBasicCommand(trigger, command);
            } else {
                response = handleCustomCommand(command);
            }
        }catch(GameExceptions e){
            response = String.valueOf(e);
        }
        return response;
    }


    public String handleBasicCommand(BasicCommand trigger, String command) throws GameExceptions {

        this.currentLocation = map.getCurrentLocation();

        String response = "";

        switch (trigger) {
            case inventory:
                response = respondToInventory();
                break;
            case get:
                response = respondToGet(command);
                break;
            case drop:
                response = respondToDrop(command);
                break;
            case _goto:
                response = respondToGoto(command);
                break;
            case look:
                response = respondToLook();
                break;
            default:
                break;
        }
        return response;
    }

    private BasicCommand containsBasicCommand(String command){

        BasicCommand res = null;

        for(BasicCommand trigger : BasicCommand.values()) {

            if(command.contains(String.valueOf(trigger))){

                res=trigger;
            }
        }
        return res;
    }

    private GameArtefact findArtefact(boolean inventory, String command) throws GameExceptions {

        String[] words = command.split(" ");
        GameArtefact res=null;
        int count = 0;

        for(String word : words){

            if(currentLocation.getEntity(word) != null && !inventory){
                count++;

                if(currentLocation.getEntity(word) instanceof GameArtefact){
                    res = (GameArtefact) currentLocation.getEntity(word);
                }
                else{ throw new GameExceptions.CannotGetOrDropItem(word); }
            }
            if(currentPlayer.getArtefact(word) != null && inventory){

                count++;
                res = currentPlayer.getArtefact(word);
            }
        }
        if (count > 1){ throw new GameExceptions.CannotGetOrDropMultiple(); }

        if(count == 0){ throw new GameExceptions.CannotGetOrDropNothing(); }

        return res;
    }

    private String respondToInventory(){

        if(currentPlayer.getInventorySize() >0 ) {
            return "In your inventory, you have:\n" + currentPlayer.getInventoryAsString();
        }
        else{
            return  "There is nothing in your inventory.";
        }
    }

    private String respondToGet(String command) throws GameExceptions {

         GameArtefact foundArtefact = findArtefact(false, command);
         if( foundArtefact != null) {
             map.getCurrentLocation().removeEntity(foundArtefact);
             map.getCurrentPlayer().addToInventory(foundArtefact);
             return "You picked up a " + foundArtefact.getName();
         }
        return "";
    }

    private String respondToDrop(String command) throws GameExceptions {

        GameArtefact foundArtefact = findArtefact(true, command);
        if( foundArtefact != null) {
            map.getCurrentLocation().addEntity(foundArtefact);
            map.getCurrentPlayer().removeFromInventory(foundArtefact);
            return "You dropped up a " + foundArtefact.getName();
        }
        return "";
    }

    private String respondToGoto(String command){
        String[] words = command.split(" ");
        String locationName = "";

        int count = 0;
        for(String word : words) {

            if (currentLocation.isPath(word)) {
                count++;
                locationName = word;
            }
        }
        if(count ==1){
            GameLocation nextLocation = map.getLocation(locationName);
            map.setCurrentLocation(nextLocation);
            currentLocation = nextLocation;
            return respondToLook();
        }
        return "You enter the "+currentLocation;
    }

    private String respondToLook(){

        String response = "You are in " + currentLocation.getDescription() + ". You can see:\n";

        response = response + currentLocation.getAllEntitiesAsString(null) + "You can access from here:\n";

        response = response + currentLocation.getAllPathsAsString();

        return response;
    }

    private String handleCustomCommand(String command) throws GameExceptions.ActionIsNull, GameExceptions {
        int count = 0;

        GameAction action = null;

        for(String key :customActions.library.keySet()){
            if (command.contains(key)) {
                count++;
                if(findCustomAction(key, command)!=null) {
                    action=findCustomAction(key, command);
                }
            }
        }
        if(count >1){ throw new GameExceptions.MultipleTriggerWords(); }
        if (count == 1 && action !=null) {
            consumeEntities(action);
            produceEntities(action);
        }
        return action ==null? "No action performed." : action.getNarration();
    }

    private void consumeEntities(GameAction action){

        ArrayList<GameEntity> consumables = action.getConsumed();
        for(GameEntity consume : consumables) {

            map.removeFromAllLocations(consume);
            if(consume instanceof GameArtefact) {
                currentPlayer.removeFromInventory((GameArtefact) consume);
            }
            if(consume.getName().equals("health")){
                currentPlayer.removeHealth();
                //check if player dies
            }
            if(consume instanceof GameLocation){
                currentLocation.removePath(consume.getName());
            }
        }
    }

    private void produceEntities(GameAction action){

        ArrayList<GameEntity> products = action.getProduced();
        for(GameEntity produce : products) {
            map.getStoreroom().removeEntity(produce);
            map.addEntity(produce, currentLocation);
            if(produce instanceof GameLocation){

                currentLocation.addPath(produce.getName());
            }
        }
    }

    private GameAction findCustomAction(String word, String command) throws GameExceptions {
    GameAction res=null;
        Set<GameAction> actions = customActions.getActions(word);
        int count = 0;
        for (GameAction action : actions) {
            if (containsSubjects(action, command)) {
                count++;
                res=action;
            }
        }
        return count==1? res : null;
    }

    //change this so the command can not contain other entities which aren't in the action
    private boolean containsSubjects(GameAction action, String command) throws GameExceptions {

        ArrayList<GameEntity> subjects = action.getSubjects();
        int subjectCount=0;
        int entityCount=0;

        String[] words = command.split(" ");
        for(String word : words){
            if(subjects.contains(word)){
                subjectCount++;
            }
            if(map.getEntity(word)!=null){
                entityCount++;
            }
            boolean entityInLocation = currentLocation.getEntity(word)==null ? false : true;
            boolean artefactInInventory = currentPlayer.getArtefact(word)==null ? false : true;

            if((!entityInLocation && !artefactInInventory) || entityCount>subjectCount) {
                throw new GameExceptions.SubjectsNotInVicinity();
            }
        }

        return subjectCount>0? true : false;
    }

}
