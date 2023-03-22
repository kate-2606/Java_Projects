package edu.uob;

import java.util.ArrayList;

public class Row {

    private ArrayList<String> row;

    public Row(ArrayList<String> row){ this.row=row; }

    public String getCellDataByNumber (int columnNumber) { return row.get(columnNumber); }

    public void changeCellData (int column, String data) { row.set(column, data);}


    public void addCell(int position, String dataStr) {
        row.add(position, dataStr);
    }

    public void removeCell(int position) { row.remove(position); }


    public String getRowAsString(Long key){
        if (row!=null){
            String rowStr="";
            rowStr=key.toString() + "\t";
            for(String s : row){
                rowStr = rowStr + s +"\t";
            }
            return rowStr;
        }
        return null;
    }

}


















