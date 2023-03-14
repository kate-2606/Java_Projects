package edu.uob;

import java.io.*;
import java.util.ArrayList;


public class Table {
//use file separator instead
    public Table(String fileName, BufferedReader buffReader) throws IOException{

        if(fileName.lastIndexOf(File.separator)!=-1){
            this.tableName = fileName.substring(fileName.lastIndexOf(File.separator)+1, fileName.lastIndexOf("."));
        }
        else {
            this.tableName = fileName.substring(0, fileName.lastIndexOf("."));
        }

        try {
            this.attributes = new Attributes(buffReader.readLine());
            this.data = new ArrayList<Row>();
            String data = null;
            int i=1;
            while ((data = buffReader.readLine()) != null) {
                Row dataRow = new Row(data);
                this.data.add(dataRow);
                i++;
            }
            primaryKey();

        }catch (IOException e) {
            throw new IOException();
        }
    }

    private Attributes attributes;

    private ArrayList<Row> data;

    private String tableName;

    public String getTableName(){ return tableName; }

    public String getCellDataByNumber(int columnNumber, int rowNumber){
        Row currRow = data.get(rowNumber);
        return currRow.getCellDataByNumber(columnNumber);
    }

    public String getAttributeByNumber(int column){ return attributes.getAttributeByNumber(column); }

    public int getNumberOfDataRows() { return data.size(); }

    public int getNumberOfAttributes() { return attributes.getNumberOfAttributes(); }

    public void addAttribute(int position, String attributeName) {
        attributes.addAttribute(position, attributeName);
    }
    public void addCellData(int rowNumber, int position, String dataStr) {
        data.get(rowNumber).addCellData(position, dataStr);
    }

    public void primaryKey(){
        this.addAttribute(0, "id");
        for(int i=0; i<this.getNumberOfDataRows(); i++){
            this.addCellData(i, 0, Integer.toString(i+1));
        }
    }


    public String rowToString(int rowNumber){
        int numberOfColumns = getNumberOfAttributes();
        String rowStr=data.get(rowNumber).rowToString(numberOfColumns);

        return rowStr;
    }

    public String attributesToString(){ return attributes.attributesToString(); }

}