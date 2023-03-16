package edu.uob;

import java.util.ArrayList;


public class Table {
//use file separator instead
    public Table(String name) {
        tableName = name;
    }

    private Attributes attributes;

    private ArrayList<Row> dataRows;

    private String tableName;

    public String getTableName(){ return tableName; }

    public String getCellDataByNumber(int columnNumber, int rowNumber){
        Row currRow = dataRows.get(rowNumber);
        return currRow.getCellDataByNumber(columnNumber);
    }

    public String getAttributeByNumber(int column){ return attributes.getAttributeByNumber(column); }

    public int getNumberOfDataRows() { return dataRows.size(); }

    public int getNumberOfAttributes() { return attributes.getNumberOfAttributes(); }

    public void addAttribute(int position, String attributeName) {
        attributes.addAttribute(position, attributeName);
    }

    public void addCellData(int rowNumber, int position, String dataStr) {
        dataRows.get(rowNumber).addCellData(position, dataStr);
    }

    public void setAttributes(boolean fromFile, String attributesStr, ArrayList<String> attributesList) {
        attributes = new Attributes(fromFile, attributesStr, attributesList);
    }

    public void addRow(boolean fromFile, String rowStr, ArrayList<String> rowList){
        Row row = new Row(fromFile, rowStr, rowList);
        dataRows.add(row);
    }

    public void primaryKey(){
        this.addAttribute(0, "id");
        for(int i=0; i<this.getNumberOfDataRows(); i++){
            this.addCellData(i, 0, Integer.toString(i+1));
        }
    }


    public String getRowAsString(int rowNumber){
        int numberOfColumns = getNumberOfAttributes();
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

}
















