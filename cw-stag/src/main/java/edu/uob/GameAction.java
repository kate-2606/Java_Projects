package edu.uob;

import java.util.ArrayList;
import java.util.HashSet;

public class GameAction
{
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

    public void addTrigger(String triggerWord){ triggers.add(triggerWord); }

    public ArrayList<String> getTriggers(){ return triggers; }

    public void addSubject(String subjectWord){ subjects.add(subjectWord); }

    public void addConsumed(String consumedWord){ consumed.add(consumedWord); }

    public void addProduces(String producesWord){ produced.add(producesWord); }

    public void addNarration(String narrationWords){ narration = narrationWords; }



}
