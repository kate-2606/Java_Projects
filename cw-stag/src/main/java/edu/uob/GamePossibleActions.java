package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GamePossibleActions {
    public GamePossibleActions(){
        this.allActions = new HashMap<>();
    }

    HashMap<String, HashSet<GameAction>> allActions;

    public void addAction(GameAction action){
        ArrayList<String> triggers = action.getTriggers();
        for(String trigger : triggers){
            if(allActions.get(trigger)!=null){
                allActions.get(trigger).add(action);
            }
            else{
                HashSet<GameAction> actionSet = new HashSet<>();
                actionSet.add(action);
                allActions.put(trigger, actionSet);
            }
        }
    }


}
