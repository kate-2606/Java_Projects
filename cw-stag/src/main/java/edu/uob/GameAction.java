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

    public void addTrigger(String phrase){ triggers.add(phrase); //System.out.println("adding Trigger: " + phrase);
    }

    public ArrayList<String> getTriggers(){ return triggers; }

    public void addSubject(String phrase){ subjects.add(phrase); //System.out.println("adding Subject: " + phrase);
    }

    public void addConsumed(String phrase){ consumed.add(phrase); //System.out.println("adding Consumed: " + phrase);
    }

    public void addProduced(String phrase){ produced.add(phrase); //System.out.println("adding Produced: " + phrase);
    }

    public void addNarration(String phrase){ narration = phrase; //System.out.println("adding Narration: " + phrase);
    }



}
