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

    public void addSubject(GameEntity entity){
        if(entity!=null)
            subjects.add(entity);
    }

    public ArrayList<GameEntity> getSubjects(){ return subjects; }

    public ArrayList<String> getActionAsString(String elementType){

        ArrayList<String> elementsAsString = new ArrayList<>();
        switch(elementType){
            case "subjects":
            for(GameEntity subject : subjects){
                elementsAsString.add(subject.getName());
            }
            break;
            case"consumed":
                for(GameEntity consume: consumed){
                    elementsAsString.add(consume.getName());
                }
                break;
            case "produced":
                for(GameEntity product: produced){
                    elementsAsString.add(product.getName());
                }
                break;
        }
        return elementsAsString;
    }

    public void addConsumed(GameEntity entity){
        if(entity!=null)
            consumed.add(entity);
    }

   public ArrayList<GameEntity> getConsumed(){ return consumed; }

    public void addProduced(GameEntity entity){
        if(entity!=null)
            produced.add(entity);
    }

    public ArrayList<GameEntity> getProduced(){ return produced; }

    public void addNarration(String phrase){ narration = phrase; }

    public String getNarration(){ return narration; }

}
