package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GameAction extends HashSet<GameAction> {
    public GameAction(){
        this.triggers = new ArrayList<>();
        this.subjects = new ArrayList<>();
        this.consumed = new ArrayList<>();
        this.produced = new ArrayList<>();
    }
    ArrayList<String> triggers;

    ArrayList<GameEntity> subjects;

    ArrayList<GameEntity> consumed;

    ArrayList<GameEntity> produced;

    String narration;

    public void addTrigger(String phrase){ triggers.add(phrase);}

    public ArrayList<String> getTriggers(){ return triggers; }

    public void addSubject(GameEntity entity){ subjects.add(entity); }

    public ArrayList<GameEntity> getSubjects(){ return subjects; }

    public void addConsumed(GameEntity entity){ subjects.add(entity); }

   public ArrayList<GameEntity> getConsumed(){ return consumed; }

    public void addProduced(GameEntity entity){ subjects.add(entity); }

    public ArrayList<GameEntity> getProduced(){ return produced; }

    public void addNarration(String phrase){ narration = phrase; }

    public String getNarration(){ return narration; }

}
