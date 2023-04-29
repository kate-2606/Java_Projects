package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ActionLibrary {
    public ActionLibrary(){
        this.library = new HashMap<>();
    }

    public HashMap<String, Set<GameAction>> library;

    public void addAction(GameAction action){
        ArrayList<String> triggers = action.getTriggers();

        for (String trigger : triggers){
            if(library.get(trigger)==null){
                HashSet<GameAction> set = new HashSet<>();
                library.put(trigger, set);
            }
            Set actions = library.get(trigger);
            actions.add(action);
        }
    }

    public Set<GameAction> getActions(String trigger) { return library.get(trigger); }

    public boolean matchingTrigger(String trigger) { return library.containsKey(trigger); }
}
