package edu.uob;

import java.util.HashMap;
import java.util.Map;

public class GameCharacter extends GameEntity{

    public GameCharacter(String name, String description) {
        super(name, description);
        this.inventory = new HashMap<>();
    }

    GameLocation characterLocation;

    HashMap<String, GameArtefact> inventory;

    public void addToInventory(GameArtefact newArtefact) { inventory.put(newArtefact.getName(), newArtefact); }

    public void removeFromInventory(GameArtefact usedArtefact) { inventory.remove(usedArtefact.getName()); }

    public void setCharacterLocation(GameLocation aLocation) { this.characterLocation = aLocation; }

    public GameArtefact getArtefact(String name) { return inventory.get(name); }

    public String getInventoryAsString() {
        String result = "";
        for (Map.Entry<String, GameArtefact> artefact : inventory.entrySet()) {
            result = result + artefact.getValue().getDescription() + "\n";
        }
        return result;
    }

    public int getInventorySize() { return inventory.size(); }

}
