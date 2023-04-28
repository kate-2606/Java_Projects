package edu.uob;

import java.util.HashMap;
import java.util.Map;

public class GameCharacter extends GameEntity{

    public GameCharacter(String name, String description, GameLocation location) {
        super(name, description, location);
        this.inventory = new HashMap<>();
        this.health=3;
    }

    GameLocation characterLocation;

    HashMap<String, GameEntity> inventory;

    int health;

    public void addToInventory(GameArtefact newArtefact) { inventory.put(newArtefact.getName(), newArtefact); }

    public void removeFromInventory(GameArtefact usedArtefact) { inventory.remove(usedArtefact.getName()); }

    public void setCharacterLocation(GameLocation aLocation) { this.characterLocation = aLocation; }

    public GameArtefact getArtefact(String name) { return (GameArtefact) inventory.get(name); }

    public boolean hasArtefact(String name) { return inventory.containsKey(name); }

    public String getInventoryAsString() {
        String result = "";
        for (Map.Entry<String, GameEntity> artefact : inventory.entrySet()) {
            result = result + artefact.getValue().getDescription() + "\n";
        }
        return result;
    }

    public int getInventorySize() { return inventory.size(); }

    public void removeHealth() {
        if (health > 0)
            health = health-1;
        //if (health == 0)

    }

    public void addHealth(){
        if(health<3)
            health++;
    }

}
