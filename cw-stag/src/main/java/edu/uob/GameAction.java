package edu.uob;

import java.util.ArrayList;
import java.util.HashSet;

public class GameAction extends HashSet<GameAction> {
    public GameAction(){
        this.triggers = new ArrayList<>();
        this.subjects = new ArrayList<>();
        this.consumed = new ArrayList<>();
        this.produced = new ArrayList<>();
    }
    ArrayList<String> triggers;

    ArrayList<String> subjects;

    ArrayList<String> consumed;

    ArrayList<String> produced;

    String narration;

    public void addTrigger(String phrase){ triggers.add(phrase);}

    public ArrayList<String> getTriggers(){ return triggers; }

    public void addSubject(String phrase){ subjects.add(phrase); }

    public ArrayList<String> getSubjects(){ return subjects; }

    public void addConsumed(String phrase){ consumed.add(phrase); }

    public ArrayList<String> getConsumed(){ return consumed; }

    public void addProduced(String phrase){ produced.add(phrase); }

    public ArrayList<String> getProduced(){ return produced; }

    public void addNarration(String phrase){ narration = phrase; }

    public String getNarration(){ return narration; }

}
