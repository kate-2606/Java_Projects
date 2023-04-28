package edu.uob;

import java.io.IOException;
import java.util.*;

public class CommandInterpreter {
    public CommandInterpreter(GameMap map, GameCharacter currentPlayer, ActionLibrary customActions) {
        this.map = map;
        this.currentPlayer = currentPlayer;
        this.customActions = customActions;
        map.setCurrentPlayer(this.currentPlayer);
    }

    GameMap map;
    String command;
    GameCharacter currentPlayer;
    GameLocation currentLocation;
    ActionLibrary customActions;


    public String handleCommand (String inpCommand){

        this.currentLocation = map.getCurrentLocation();

        String response = "";

        this.command = inpCommand.toLowerCase();

        try {
            if (this.command.contains("inv")) {
                this.command = this.command.replaceAll("\\binv\\b", "inventory");
            }
            if (this.command.contains("goto")) {
                this.command = this.command.replaceAll("\\bgoto\\b", "_goto");
            }

            BasicCommand trigger = containsBasicCommand(command);

            if (trigger != null) {
                response = handleBasicCommand(trigger);
            } else {
                response = handleCustomCommand();
            }
        }catch(GameExceptions e){
            response = String.valueOf(e);
        }
        return response;
    }


    private String handleBasicCommand(BasicCommand trigger) throws GameExceptions {
        String response = "";

        switch (trigger) {
            case inventory:
                response = respondToInventory();
                break;
            case get:
                response = respondToGet();
                break;
            case drop:
                response = respondToDrop();
                break;
            case _goto:
                response = respondToGoto();
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

    private GameArtefact findArtefact(boolean inventory) throws GameExceptions {

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
        if (count != 1){ throw new GameExceptions.CannotGetOrDropMultiple(); }

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

    private String respondToGet() throws GameExceptions {

         GameArtefact foundArtefact = findArtefact(false);
         if( foundArtefact != null) {
             map.getCurrentLocation().removeEntity(foundArtefact);
             map.getCurrentPlayer().addToInventory(foundArtefact);
             return "You picked up a " + foundArtefact.getName();
         }
        return "";
    }

    private String respondToDrop() throws GameExceptions {

        GameArtefact foundArtefact = findArtefact(true);
        if( foundArtefact != null) {
            map.getCurrentLocation().addEntity(foundArtefact);
            map.getCurrentPlayer().removeFromInventory(foundArtefact);
            return "You dropped up a " + foundArtefact.getName();
        }
        return "";
    }

    private String respondToGoto(){
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

    private String handleCustomCommand() throws GameExceptions.ActionIsNull, GameExceptions {
        int count = 0;

        GameAction action = null;
        String words[] = command.split(" ");
        for (String word : words) {

            if (customActions.matchingTrigger(word)) {
                System.out.println(word);
                count++;
                action = findCustomAction(word, words);
            }
        }
        if(count>1){ throw new GameExceptions.MultipleTriggerWords(); }
        if (count == 1 && action!=null) {
            consumeEntities(action);
            produceEntities(action);
        }
        return action==null? "No action performed." : action.getNarration();
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

    private GameAction findCustomAction(String word, String[] words) throws GameExceptions {
    GameAction res=null;
        Set<GameAction> actions = customActions.getActions(word);
        int count = 0;
        for (GameAction action : actions) {
            if (containsSubjects(action, words)) {
                count++;
                res=action;
            }
        }
        return count==1? res : null;
    }

    //change this so the command can not contain other entities which aren't in the action
    private boolean containsSubjects(GameAction action, String[] words) throws GameExceptions {

        ArrayList<GameEntity> subjects = action.getSubjects();
        int count=0;
        for(GameEntity subject : subjects){
            if(Arrays.asList(words).contains(subject.getName())){
                count++;
            }
            boolean entityInLocation = currentLocation.getEntity(subject.getName())==null ? false : true;
            boolean artefactInInventory = currentPlayer.getArtefact(subject.getName())==null ? false : true;

            if(!entityInLocation && !artefactInInventory){ throw new GameExceptions.SubjectsNotInVicinity(); }
        }
        System.out.println(action.getActionAsString("subjects") +" "+ count);
        return count>0? true : false;
    }

}
