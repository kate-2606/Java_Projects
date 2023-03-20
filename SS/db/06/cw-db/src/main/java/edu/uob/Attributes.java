package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.lang.String;

public class Attributes {

    //name these with capitals?

    public Attributes(ArrayList<String> attributes) { this.attributes=attributes; }

    private ArrayList<String> attributes ;

    public String getAttributeByNumber(int column) { return attributes.get(column); }

    public int getNumberOfAttributes() { return attributes.size(); }


    public String getAttributesAsString(){
        int numberOfColumns = getNumberOfAttributes();
        String attributesStr = getAttributeByNumber(0);
        for (int j = 1; j < numberOfColumns; j++) {
            attributesStr = attributesStr.concat("\t" + getAttributeByNumber(j));
        }
        return attributesStr;
    }

    public void addAttribute (String attributeName){
        if(attributes!=null){
            attributes.add(attributeName);
        }
    }

    public void removeAttribute(int position) { attributes.remove(position); }
    public int getAttributePosition(String attributeName){
        int i=0;
        for(String name : attributes) {
            if (name.equals(attributeName)) { return i; }
            i++;
        }
        return -1;
    }

}
