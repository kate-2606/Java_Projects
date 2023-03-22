package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.lang.String;
import java.util.Collections;
import java.util.stream.Collectors;

public class Attributes {

    //name these with capitals?

    public Attributes(ArrayList<String> attributes) throws InterpreterException {
        this.attributes=attributes;
        if(!attributes.get(0).equals("id")){
            this.attributes.add(0, "id");
        }
        checkAttributes();
    }

    private ArrayList<String> attributes ;

    public String getAttributeByNumber(int column) { return attributes.get(column); }

    public int getNumberOfAttributes() { return attributes.size(); }



    private void checkAttributes() throws InterpreterException {
        ArrayList<String> lowercase = attributes;

        for(String s: attributes){
            int cnt=0;
            for(String d : attributes)
            if(s.equalsIgnoreCase(d)) {
                cnt++;
            }
            if(cnt>1){  throw new InterpreterException.ExistingAttribute(s);  }
        }
    }

    public String getAttributesAsString(){
        String attributesStr = "";
        for (String s: attributes) {
            attributesStr = attributesStr +  s + "\t";
        }
        return attributesStr;
    }

    public ArrayList getAttributesAsList(){ return attributes; }

    public void addAttribute (String attributeName) throws InterpreterException {
        if(attributeExists(attributeName)){
            throw new InterpreterException.ExistingAttribute(attributeName);
        }
        if(attributes!=null){
            attributes.add(attributeName);
        }
    }

    public void removeAttribute(int position) { attributes.remove(position); }

    public int getAttributePosition(String attributeName) throws InterpreterException{
        int i=0;
        for(String name : attributes) {
            if (name.equalsIgnoreCase(attributeName)) { return i; }
            i++;
        }
        return -1;
    }

    private boolean attributeExists(String attributeName){
        for (String s : attributes){
            if(s.equals(attributeName)){
                return true;
            }
        }
        return false;
    }

}
