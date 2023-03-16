package edu.uob;

import java.util.ArrayList;

public class Row {

    private ArrayList<String> row = new ArrayList<>();

    public Row(Boolean fromFile, String attributesStr, ArrayList<String> attributesList){
        if(fromFile && attributesStr!=null){
            setRowFromFile(attributesStr);
        }
        if(!fromFile){

        }
    }

    public String getCellDataByNumber (int columnNumber) {
        return row.get(columnNumber);
    }

    private void setRowFromFile(String inpRow){
        String separator=null;
        if(inpRow.contains(",")){
            separator = ",";
        }
        if(inpRow.contains("\t")){
            separator = "\t";
        }
        else{
            //throw an exception
        }

        String[] dataArray = inpRow.split(separator);

        int i=0;
        while (i<dataArray.length){
            row.add(dataArray[i].trim());
            i++;
        }
    }

    public void addCellData(int position, String dataStr) {
        row.add(position, dataStr);
    }

    public String getRowAsString(){
        int numberOfColumns = row.size();
        String rowStr=getCellDataByNumber(0);
        for (int j = 1; j < numberOfColumns; j++) {
            rowStr = rowStr.concat("\t" + getCellDataByNumber(j));
        }
        return rowStr;
    }

}


















