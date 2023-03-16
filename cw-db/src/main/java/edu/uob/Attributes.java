package edu.uob;

import java.util.ArrayList;
import java.lang.String;

public class Attributes {

    //name these with capitals?

    public Attributes(Boolean fromFile, String attributesStr, ArrayList<String> attributesList){
        if(fromFile && attributesStr!=null){
            setAttributesFromFile(attributesStr);
        }
        if(!fromFile){

        }
    }

    private ArrayList<String> attributes ;

    public void setAttributesFromFile(String inpAttributes){
        String separator=null;
        if(inpAttributes.contains(",")){
            separator = ",";
        }
        if(inpAttributes.contains("\t")){
            separator = "\t";
        }
        else{
            //throw an exception
        }

        String[] attributeArray = inpAttributes.split(separator);

        int i=0;
        while (i<attributeArray.length){
            attributes.add(attributeArray[i].trim());
            i++;
        }
    }

    public String getAttributeByNumber(int column) { return attributes.get(column); }

    public int getNumberOfAttributes() { return attributes.size(); }

    public void addAttribute(int position, String attributeName) { attributes.add(position, attributeName); }

    public String getAttributesAsString(){
        int numberOfColumns = getNumberOfAttributes();
        String attributesStr = getAttributeByNumber(0);
        for (int j = 1; j < numberOfColumns; j++) {
            attributesStr = attributesStr.concat("\t" + getAttributeByNumber(j));
        }
        return attributesStr;
    }
}
