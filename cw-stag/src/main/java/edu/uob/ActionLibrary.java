package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ActionLibrary {
    public ActionLibrary(){
        this.library = new HashMap<>();
    }

    HashMap<String, HashSet<GameAction>> library;

    public void addAction(GameAction action){
        ArrayList<String> triggers = action.getTriggers();
        for (String trigger : triggers){
            if(library.get(trigger)==null){
                HashSet<GameAction> set = new HashSet<>();
                library.put(trigger, set);
            }
            HashSet set = library.get(trigger);
            set.add(action);
        }
    }
}
