package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.*;


public class Table {
//use file separator instead
    public Table(String name) {
        tableName = name;
    }

    private Attributes attributes;

    private ArrayList<Row> dataRows;

    private String tableName;

    private long nextPrimaryKey;

    public String getName(){ return tableName; }

    public String getCellDataByNumber(int columnNumber, int rowNumber){
        Row currRow = dataRows.get(rowNumber);
        return currRow.getCellDataByNumber(columnNumber);
    }

    public String getAttributeByNumber(int column){ return attributes.getAttributeByNumber(column); }

    public int getNumberOfDataRows() {
        if (dataRows!=null) {
            return dataRows.size();
        }
        else{
            return 0;
        }
    }


    public int getNumberOfAttributes() { return attributes.getNumberOfAttributes(); }

    //public void appendAttribute(String attributeName) { attributes.appendAttribute(attributeName); }

    public void addCellData(int rowNumber, int position, String dataStr) {
        dataRows.get(rowNumber).addCellData(position, dataStr);
    }

    public void addRowFromCommand(boolean isAttributes, ArrayList<String> valuesList){
        if(!isAttributes) {
            if (dataRows == null) {
                dataRows = new ArrayList<>();
            }
            Row row = new Row(valuesList, nextPrimaryKey);
            nextPrimaryKey++;
            dataRows.add(row);
        }
        if(isAttributes) { attributes= new Attributes(valuesList);}
    }

    public void addRowFromFile(Boolean isAttributes, String rowStr) throws IOException{

        String separator;
        if (rowStr.contains("\t")) {
            separator = "\t";
        } else {
            throw new IOException("Attribute separators in table are invalid, expected TABS");
        }

        String[] values = rowStr.split(separator);
        ArrayList<String> valueList = new ArrayList<>();
        //try this
        // ArrayList<String> row= new ArrayList<>( Arrays.asList(rowStr.split("\t")));

        int i = 0;
        while (i < values.length) {
            valueList.add(values[i].trim());
            i++;
        }
        if(isAttributes){
            attributes = new Attributes(valueList);
        }
        if(!isAttributes){
            addRowFromCommand(true, valueList);
        }
    }
/*
    public void primaryKey(){
        this.addAttribute(0, "id");
        for(int i=0; i<this.getNumberOfDataRows(); i++){
            this.addCellData(i, 0, Integer.toString(i+1));
        }
    }

 */

    public String getRowAsString(int rowNumber){
        String rowStr=dataRows.get(rowNumber).getRowAsString();

        return rowStr;
    }

    public String getRowsAsString(){
        String output = getRowAsString(0);
        for(int i=1; i<dataRows.size(); i++){
            output = output + "\n" + getRowAsString(i);
        }
        return output;
    }

    public String getAttributesAsString(){ return attributes.getAttributesAsString(); }

    public Attributes getAttributes() { return attributes; }

    public void removeAttribute(String attributeName) throws InterpreterException.CannotDeletePrimaryKey,
            InterpreterException.AttributeDoesNotExist {
        int position = attributes.getAttributePosition(attributeName);
        if(position>0){
            attributes.removeAttribute(position);
            if(dataRows!=null) {
                for (Row r : dataRows) {
                    r.removeCell(position);
                }
            }
        }
        if(position==0) { throw new InterpreterException.CannotDeletePrimaryKey();}

        if(position==-1) { throw new InterpreterException.AttributeDoesNotExist(attributeName);}
    }


}
















