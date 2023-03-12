package edu.uob;

import java.util.ArrayList;

public class Row {

    private ArrayList<String> row = new ArrayList<>();

    public Row(String dataStr){
        String[] elements = dataStr.split("\t");
        int i=0;
        while (i< elements.length) {
            row.add(elements[i]);
            i++;
        }
    }

    public String getCellDataByNumber (int columnNumber) {
        return row.get(columnNumber);
    }

    public void addCellData(int position, String dataStr) {
        row.add(position, dataStr);
    }
}
