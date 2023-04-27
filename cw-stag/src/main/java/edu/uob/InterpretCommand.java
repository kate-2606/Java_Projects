package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class InterpretCommand {
    public InterpretCommand(GameMap map, GameCharacter currentPlayer, ActionLibrary customActions) {
        this.map = map;
        this.currentPlayer = currentPlayer;
        this.customActions = customActions;
    }

    GameMap map;

    String command;

    GameCharacter currentPlayer;

    GameLocation currentLocation;

    ActionLibrary customActions;

    public String handleCommand(String inpCommand){

        this.currentLocation = map.getCurrentLocation();

        System.out.println("All Paths: " + currentLocation.getAllPathsAsString());

        String response = "";

        this.command = inpCommand.toLowerCase();

        if(this.command.contains("inv")) {
            this.command = this.command.replaceAll("\\binv\\b","inventory");
        }
        if(this.command.contains("goto")) {
            this.command = this.command.replaceAll("\\bgoto\\b","_goto");
            System.out.println("got here: "+this.command);
        }

        BasicCommand trigger = containsBasicCommand(command);

        if(trigger != null){
            response = handleBasicCommand(trigger);
        }
        else{
            response = handleCustomCommand();
        }

        return response;
    }


    private String handleBasicCommand(BasicCommand trigger) {
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
            System.out.println(trigger + " " + command);
            if(command.contains(String.valueOf(trigger))){
                System.out.println(trigger);
                res=trigger;
            }
        }
        return res;
    }

    private GameArtefact findArtefact(boolean basic, boolean inventory){

        String[] words = command.split(" ");
        String artefactName = "";

        int count = 0;
        for(String word : words){

            if(currentLocation.getArtefact(word) != null && !inventory){
                count++;
                artefactName = word;
            }
            if(currentPlayer.getArtefact(word) != null && inventory){
                count++;
                artefactName = word;
            }
        }
        if(count==1 && basic) {
            return inventory ? currentPlayer.getArtefact(artefactName) : currentLocation.getArtefact(artefactName);
        }
        return null;
    }

    private String respondToInventory(){

        String response = "";

        if(currentPlayer.getInventorySize() >0 ) {
            response = "In your inventory, you have:\n" + currentPlayer.getInventoryAsString();
        }
        else{
            response = "There is nothing in your inventory.";
        }
        return response;
    }

    private String respondToGet(){

         GameArtefact foundArtefact = findArtefact(true, false);
         if( foundArtefact != null) {
             map.getCurrentLocation().removeArtefact(foundArtefact);
             map.getCurrentPlayer().addToInventory(foundArtefact);
             return "You picked up a " + foundArtefact.getName();
         }
        return "";
    }

    private String respondToDrop(){

        GameArtefact foundArtefact = findArtefact(true, true);
        if( foundArtefact != null) {
            map.getCurrentLocation().addArtefact(foundArtefact);
            map.getCurrentPlayer().removeFromInventory(foundArtefact);
            return "You dropped up a " + foundArtefact.getName();
        }
        return "";
    }

    private String respondToGoto(){
        String[] words = command.split(" ");
        String locationName = "";
        System.out.println("in goto");

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
        return "";
    }

    private String respondToLook(){

        String response = "You are in " + currentLocation.getDescription() + ". You can see:\n";

        response = response + currentLocation.getAllArtefactsAsString();

        response = response + currentLocation.getAllFurnitureAsString();

        response = response + currentLocation.getAllCharactersAsString() + "You can access from here:\n";

        response = response + currentLocation.getAllPathsAsString();

        return response;
    }

    private String handleCustomCommand(){
        int count=0;
        GameAction action = null;
        String words[] = command.split(" ");
        for(String word : words){
            if(customActions.matchingTrigger(word)){
                count++;
                action = findCustomAction(word, words);
                }
            }
        if(count==1){
            consumeAndProduceAction(action);
        }
        return "";
    }

    private void consumeAndProduceAction(GameAction action){

        GameArtefact artefact = null;
        ArrayList<String> consumables = action.getConsumed();
        for(String consume : consumables) {

            artefact = currentPlayer.hasArtefact(consume)? currentPlayer.getArtefact(consume) :currentLocation.getArtefact(consume);

            if(artefact!=null){
                map.getStoreroom().addArtefact(artefact);
                currentLocation.removeArtefact(artefact);
                currentPlayer.removeFromInventory(artefact);
            }
        }
        ArrayList<String> products = action.getProduced();

        for(String produce : products) {

            artefact = map.getStoreroom().getArtefact(produce);

            if(artefact!=null){
                map.getStoreroom().removeArtefact(artefact);
                currentLocation.addArtefact(artefact);
            }
        }
    }

    private GameAction findCustomAction(String word, String[] words){
    GameAction res=null;
        HashSet<GameAction> actions = customActions.getActions(word);
        GameAction action = actions.iterator().next();
        int count = 0;
        while(action!=null) {
            if (containsSubjects(action, words)) {
                count++;
                res=action;
            }
        }
        return count==1? res : null;
    }

    private boolean containsSubjects(GameAction action, String[] words) {
        ArrayList<String> subjects = action.getSubjects();
        for(String subject : subjects){
            if(!Arrays.asList(words).contains(subject)){
                return false;
            }
            if(currentLocation.getArtefact(subject) ==null && currentPlayer.getArtefact(subject)==null){
                return false;
            }
        }
        return true;
    }

}
