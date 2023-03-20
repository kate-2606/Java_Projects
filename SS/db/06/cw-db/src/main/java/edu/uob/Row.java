package edu.uob;

import java.util.ArrayList;

public class Row {

    private ArrayList<String> row;

    private long primaryKey;

    public Row(ArrayList<String> row, long primaryKey){ this.row=row;  this.primaryKey=primaryKey;}

    public String getCellDataByNumber (int columnNumber) {
        return row.get(columnNumber);
    }


    public void addCellData(int position, String dataStr) {
        row.add(position, dataStr);
    }

    public void removeCell(int position) { row.remove(position); }

    public String getRowAsString(){
        int numberOfColumns = row.size();
        String rowStr=getCellDataByNumber(0);
        for (int j = 1; j < numberOfColumns; j++) {
            rowStr = rowStr.concat("\t" + getCellDataByNumber(j));
        }
        return rowStr;
    }

    public void setPrimaryKey(long primaryKey) { this.primaryKey = primaryKey; }

    public long getPrimaryKey() { return primaryKey; }
}


















