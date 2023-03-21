package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.lang.String;
import java.util.Objects;


public class Attributes {

    //name these with capitals?

    public Attributes(ArrayList<String> attributes) {
        this.attributes=attributes;
        if(!attributes.get(0).equals("id")){
            this.attributes.add(0, "id");
        }
    }

    private ArrayList<String> attributes ;

    public String getAttributeByNumber(int column) { return attributes.get(column); }

    public int getNumberOfAttributes() { return attributes.size(); }


    public String getAttributesAsString(){
        String attributesStr = "";
        for (String s: attributes) {
            attributesStr = attributesStr +  s + "\t";
        }
        //if(!forFile) return attributesStr.substring(attributesStr.indexOf("\t")+1);
        return attributesStr;
    }

    public ArrayList getAttributesAsList(){ return attributes; }

    public void addAttribute (String attributeName){
        if(attributes!=null){
            attributes.add(attributeName);
        }
    }

    public void removeAttribute(int position) { attributes.remove(position); }

    public int getAttributePosition(String attributeName) throws InterpreterException{
        int i=0;
        for(String name : attributes) {
            if (name.equals(attributeName)) { return i; }
            i++;
        }
        throw new InterpreterException.AccessingNonExistentAttribute(attributeName);
    }

    private boolean checkIfAttributeExists(String attributeName){
        for(String name : attributes){
            if(attributeName.equalsIgnoreCase(name)){
                return false;
            }
        }
        return true;
    }

}
