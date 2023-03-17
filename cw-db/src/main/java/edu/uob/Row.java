package edu.uob;

import java.util.ArrayList;

public class Row {

    private ArrayList<String> row;

    public Row(ArrayList<String> row){ this.row=row; }

    public String getCellDataByNumber (int columnNumber) {
        return row.get(columnNumber);
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


















