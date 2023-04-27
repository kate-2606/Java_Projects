package edu.uob;

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
        }catch(GameExceptions.ActionIsNull e){
            System.out.println(e);
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

            if(command.contains(String.valueOf(trigger))){

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

    private String handleCustomCommand() throws GameExceptions.ActionIsNull{
        int count=0;
        GameAction action = null;
        String words[] = command.split(" ");
        for(String word : words){

            if(customActions.matchingTrigger(word)){
                System.out.println(word);
                count++;
                action = findCustomAction(word, words);
            }
        }
        if(count==1){
            consumeEntities(action);
            produceEntities(action);
        }
        return "";
    }

    private void consumeEntities(GameAction action){

        GameArtefact artefact;
        GameFurniture furniture;
        ArrayList<String> consumables = action.getConsumed();
        for(String consume : consumables) {

            artefact = currentPlayer.hasArtefact(consume)? currentPlayer.getArtefact(consume) :currentLocation.getArtefact(consume);

            if(artefact!=null){
                map.getStoreroom().addArtefact(artefact);
                map.removeFromAllLocations(artefact, null, null);
                currentPlayer.removeFromInventory(artefact);
            }

            furniture = currentLocation.getFurniture(consume);

            if(furniture!=null){
                map.removeFromAllLocations(null, furniture, null);
                map.getStoreroom().addFurniture(furniture);
            }

            if(furniture!=null){
                map.removeFromAllLocations(null, furniture, null);
                map.getStoreroom().addFurniture(furniture);
            }
        }
    }

    private void produceEntities(GameAction action){

        GameArtefact artefact;
        GameFurniture furniture;

        ArrayList<String> products = action.getProduced();

        for(String produce : products) {

            artefact = map.getStoreroom().getArtefact(produce);

            if(artefact!=null){
                map.getStoreroom().removeArtefact(artefact);
                currentLocation.addArtefact(artefact);
            }

            furniture = map.getStoreroom().getFurniture(produce);

            if(furniture!=null){
                currentLocation.addFurniture(furniture);
                map.getStoreroom().removeFurniture(furniture);
            }
        }
    }

    private GameAction findCustomAction(String word, String[] words){
    GameAction res=null;
        Iterator<GameAction> actions = customActions.getActions(word).iterator();
        int count = 0;
        for (Iterator<GameAction> it = actions; it.hasNext(); ) {
            GameAction action = it.next();
            System.out.print("action name: "+action.getNarration());
            if (containsSubjects(action, words)) {
                System.out.println("contains sub");
                count++;
                res=action;
            }
        }
        return count==1? res : null;
    }

    private boolean containsSubjects(GameAction action, String[] words) {
        System.out.println("looking for subs");
        ArrayList<String> subjects = action.getSubjects();

        for(String subject : subjects){

            if(!Arrays.asList(words).contains(subject)){
                return false;
            }
            boolean artefactInLocation = currentLocation.getArtefact(subject)==null ? false : true;
            boolean artefactInInventory = currentPlayer.getArtefact(subject)==null ? false : true;
            boolean furnitureInLocation = currentLocation.getFurniture(subject)==null ? false : true;

            if(!artefactInLocation && !artefactInInventory && !furnitureInLocation ){
                return false;
            }
        }
        return true;
    }

}
