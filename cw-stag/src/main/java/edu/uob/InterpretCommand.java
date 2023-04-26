package edu.uob;

public class InterpretCommand {
    public InterpretCommand(GameMap map, GameCharacter currentPlayer) {
        this.map = map;
        this.currentPlayer = currentPlayer;
    }

    public String handleCommand(String inpCommand){

        this.currentLocation = map.getCurrentLocation();

        System.out.println("All Paths: " + currentLocation.getAllPathsAsString());

        String response = "";

        this.command = inpCommand.toLowerCase();

        if(command.contains("inv")) {
            this.command.replace("inv","inventory");
        }

        BasicCommand trigger = containsBasicCommand(command);

        if(trigger != null){
            response = handleBasicCommand(trigger);
        }

        return response;
    }

    GameMap map;
    String command;

    GameCharacter currentPlayer;

    GameLocation currentLocation;
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
        return "";
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
        return "";
    }

    private String respondToLook(){

        String response="You are in ";

        response = response + currentLocation.getDescription() + ". You can see:\n";

        response = response + currentLocation.getAllArtefactsAsString();

        response = response + currentLocation.getAllFurnitureAsString();

        response = response + currentLocation.getAllCharactersAsString() + "You can access from here:\n";

        response = response + currentLocation.getAllPathsAsString();

        return response;
    }

}
