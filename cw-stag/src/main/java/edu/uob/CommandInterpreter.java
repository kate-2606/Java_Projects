package edu.uob;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static edu.uob.BasicCommand._goto;

public class CommandInterpreter {
    public CommandInterpreter(GameMap map, GameCharacter currentPlayer, ActionLibrary customActions) {
        this.map = map;
        this.customActions = customActions;
    }

    GameMap map;

    ActionLibrary customActions;


    public String handleCommand (String inpCommand) throws GameExceptions {

        String response = "";
        String command = formatCommand(inpCommand);

        BasicCommand trigger = containsBasicCommand(command);

        if (trigger != null) {

            if(!containsCustomTrigger(command))
                response = handleBasicCommand(trigger, command);

            if(containsCustomTrigger(command)){
                throw new GameExceptions.MultipleTriggerWords();
            }
        }
        else{
            response = handleCustomCommand(command);
        }

        return response;
    }

    private String formatCommand(String command){

        command = " "+command.toLowerCase()+" ";

        if (command.contains(" inv ")) {
            command = command.replaceAll("\\binv\\b", "inventory");
        }
        if (command.contains(" goto ")) {
            command = command.replaceAll("\\bgoto\\b", "_goto");
        }
        return command;
    }

    private boolean containsCustomTrigger(String command){

        String[] words = command.trim().split(" ");
        for(String word : words){
            if(customActions.getActions(word)!=null){
                return true;
            }
        }
        return false;
    }

    public String handleBasicCommand(BasicCommand trigger, String command) throws GameExceptions {

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

    private BasicCommand containsBasicCommand(String command) throws GameExceptions {

        int count=0;
        BasicCommand res=null;

        for (BasicCommand basic : BasicCommand.values()){
            if (command.contains(" " + basic.name() + " ")) {
                count++;
                res = basic;
            }
    }
    if(count >1) { throw new GameExceptions.MultipleTriggerWords(); }

    return res;
}

    private GameArtefact findArtefact(boolean inventory, String command) throws GameExceptions {

        String[] words = command.split(" ");
        GameArtefact res=null;
        int count = 0;

        for(String word : words){

            if(map.getCurrentLocation().getEntity(word) instanceof GameArtefact && !inventory){
                count++;
                res = (GameArtefact) map.getCurrentLocation().getEntity(word);
            }
            if(map.getCurrentPlayer().getArtefact(word) != null && inventory){

                count++;
                res = map.getCurrentPlayer().getArtefact(word);
            }
        }
        if (count > 1){ throw new GameExceptions.CannotGetOrDropMultiple(); }

        if(count == 0){ throw new GameExceptions.CannotGetOrDropItem(""); }

        return res;
    }

    private String respondToInventory(){

        if(map.getCurrentPlayer().getInventorySize() >0 ) {
            return "In your inventory, you have:\n" + map.getCurrentPlayer().getInventoryAsString();
        }
        else{
            return  "There is nothing in your inventory.";
        }
    }

    private String respondToGet(String command) throws GameExceptions {

         GameArtefact foundArtefact = findArtefact(false, command);

         if(foundArtefact != null) {
             map.moveEntity(foundArtefact, null);
             map.getCurrentPlayer().addToInventory(foundArtefact);
             return "You picked up a " + foundArtefact.getName();
         }
        return "";
    }

    private String respondToDrop(String command) throws GameExceptions {

        GameArtefact foundArtefact = findArtefact(true, command);

        if(foundArtefact != null) {
            map.getCurrentLocation().addEntity(foundArtefact);
            map.getCurrentPlayer().removeFromInventory(foundArtefact);
            return "You dropped up a " + foundArtefact.getName();
        }
        return "";
    }

    private String respondToGoto(String command) throws GameExceptions {

        String[] words = command.replace(" "+_goto.name()+" ", "").split(" ");
        String locationName = "";

        int count = 0;
        for(String word : words) {

            if (map.getCurrentLocation().isPath(word)) {
                count++;
                locationName = word;
            }
            if(map.getEntity(word) != null && !(map.getEntity(word) instanceof  GameLocation)) {
                throw new GameExceptions.ExeraneousEntities();
            }

            if(map.getEntity(word) instanceof  GameLocation && count!=1) {
                throw new GameExceptions.CannotAccessThisLocation(map.getCurrentLocation(), map.getLocation(word));
            }
        }

        if(count ==1){
            GameLocation nextLocation = map.getLocation(locationName);
            map.setCurrentLocation(nextLocation);
            map.moveEntity(map.getCurrentPlayer(), nextLocation);
        }
        return "You enter the "+map.getCurrentLocation();
    }

    private String respondToLook(){

        GameLocation currentLocation = map.getCurrentLocation();

        String response = "You are in " + currentLocation.getDescription() + ". You can see:\n";

        response = response + currentLocation.getAllEntitiesAsString(map.getCurrentPlayer()) + "You can access from here:\n";

        response = response + currentLocation.getAllPathsAsString();

        return response;
    }

    private String handleCustomCommand(String command) throws GameExceptions {
        int countTriggers = 0;

        GameAction action = null;

        for(String key :customActions.library.keySet()){
            if (command.contains(key)) {

                if(findCustomAction(key, command)!=null && findCustomAction(key, command)!=action) {
                    countTriggers++;
                    action=findCustomAction(key, command);
                }
            }
        }
        if(countTriggers >1){ throw new GameExceptions.MultipleTriggerWords(); }

        if (countTriggers == 1 && action !=null) {
           updateEntities(action);
        }
        return action ==null? "No action performed." : action.getNarration();
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
        //else throw multiple possible actions in command
        return count==1? res : null;
    }


    private boolean containsSubjects(GameAction action, String command) throws GameExceptions {

        int subjectCount=0;
        int entityCount=0;

        ArrayList<String> subjects = action.getSubjectsAsStrings();
        String[] words = command.trim().split(" ");

        for(String word : words){

            if(subjects.contains(word))
                subjectCount++;

            if(map.getEntity(word)!=null)
                entityCount++;

            boolean entityExists = map.getEntity(word) == null ? false : true;
            boolean entityInLocation = map.getCurrentLocation().getEntity(word)==null ? false : true;
            boolean artefactInInventory = map.getCurrentPlayer().getArtefact(word)==null ? false : true;

            if(!entityInLocation && !artefactInInventory && entityExists) {
                throw new GameExceptions.SubjectsNotInVicinity();
            }
        }

        if(entityCount>subjectCount) { throw  new GameExceptions.ExeraneousEntities(); }

        return (subjectCount>0 && subjectCount==entityCount) ? true : false;
    }


    private void updateEntities(GameAction action) throws GameExceptions {

        ArrayList<GameEntity> products = action.getProduced();

        for(GameEntity produce : products) {
            produceEntity(produce);
        }

        ArrayList<GameEntity> consumables = action.getConsumed();

        for(GameEntity consume : consumables) {
            consumeEntity(consume);
        }
    }

    private void consumeEntity(GameEntity consume) throws GameExceptions {

        GameCharacter currentPlayer = map.getCurrentPlayer();
        if (consume instanceof GameArtefact && consume.getLocation()==null) {
            if(!currentPlayer.removeFromInventory((GameArtefact) consume) && !consume.getName().equals("health")){
                throw  new GameExceptions.CannotGetOrDropItem(consume.getName());
            }
        }
        map.moveEntity(consume, map.getStoreroom());

        if (consume.getName().equals("health")) {

            if (!currentPlayer.removeHealth()) {

                currentPlayer.reincarnatePlayer(map.getStartLocation());

                map.moveEntity(currentPlayer, map.getStartLocation());
                map.setCurrentLocation(map.getStartLocation());
            }
        }

        if (consume instanceof GameLocation) {
            if (!map.getCurrentLocation().removePath(consume.getName())) {
                throw new GameExceptions.CannotPerformActionInLocation(map.getCurrentLocation());
            }
        }
    }


    private void produceEntity(GameEntity produce) {

        map.moveEntity(produce, map.getCurrentLocation());

        if (produce instanceof GameLocation) {
            map.getCurrentLocation().addPath(produce.getName());
        } else {
            map.addEntity(produce, map.getCurrentLocation());
        }
    }

}
