package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameCharacter extends GameEntity{

    public GameCharacter(String name, String description, GameLocation location) {
        super(name, description, location);
        this.inventory = new HashMap<>();
        this.health=3;
    }

    boolean isPlayer=false;

    private HashMap<String, GameEntity> inventory;

    int health;


    public void addToInventory(GameArtefact newArtefact) { inventory.put(newArtefact.getName(), newArtefact); }


    public void setAsPlayer() { isPlayer = true; }


    public boolean removeFromInventory(GameArtefact usedArtefact) {

        boolean res = inventory.containsKey(usedArtefact.getName());

        inventory.remove(usedArtefact.getName());

        return res;
    }


    public GameArtefact getArtefact(String name) { return (GameArtefact) inventory.get(name); }


    public void reincarnatePlayer(GameLocation startLocation) {

        ArrayList<String> inventoryItems = new ArrayList<>();

        for(String key : inventory.keySet()){
            this.getLocation().addEntity(inventory.get(key));
            inventoryItems.add(key);
        }

        emptyInventory(inventoryItems);

        health=3;
    }

    private void emptyInventory(ArrayList<String> inventoryItems) {
        for(String key : inventoryItems){
            inventory.remove(key);
        }
    }

    public String getInventoryAsString() {
        String result = "";
        for (Map.Entry<String, GameEntity> artefact : inventory.entrySet()) {
            result = result + artefact.getValue().getDescription() + "\n";
        }
        return result;
    }

    public int getInventorySize() { return inventory.size(); }

    public boolean removeHealth() {
        if (health > 0) {
            health = health - 1;
            return true;
        }
        return false;
    }

    public void addHealth(){
        if(health<3)
            health++;
    }

    public int getHealth(){ return health; }

}
