package edu.uob;

import java.util.ArrayList;

public class Attributes {

    private ArrayList<String> attributes = new ArrayList<>();
    public Attributes(String attributesStr){
        String[] elements = attributesStr.split("\t");
        int i=0;
        while (i<elements.length){
            attributes.add(elements[i]);
            i++;
        }
      }

    public String getAttributeByNumber(int column) { return attributes.get(column); }

    public int getNumberOfAttributes() { return attributes.size(); }

    public void addAttribute(int position, String attributeName) { attributes.add(position, attributeName); }
}
