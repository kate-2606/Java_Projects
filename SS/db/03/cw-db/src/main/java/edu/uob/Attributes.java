package edu.uob;

import java.util.ArrayList;
import java.lang.String;

public class Attributes {

    //name these with capitals?

    public Attributes(String attributesStr){
        String[] elements = attributesStr.split("\t");
        int i=0;
        while (i<elements.length){
            attributes.add(elements[i]);
            i++;
        }
      }

    private ArrayList<String> attributes = new ArrayList<>();

    public String getAttributeByNumber(int column) { return attributes.get(column); }

    public int getNumberOfAttributes() { return attributes.size(); }

    public void addAttribute(int position, String attributeName) { attributes.add(position, attributeName); }

    public String attributesToString(){
        int numberOfColumns = getNumberOfAttributes();
        String attributesStr = getAttributeByNumber(0);
        for (int j = 1; j < numberOfColumns; j++) {
            attributesStr = attributesStr.concat("\t" + getAttributeByNumber(j));
        }
        return attributesStr;
    }
}
