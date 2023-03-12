package edu.uob;
import java.io.*;

import java.util.ArrayList;

public class PrimaryKey {

    private ArrayList<Integer> primaryKey = new ArrayList<>();
    public PrimaryKey(Table dataTable){
        dataTable.addAttribute(0, "id");
        for(int i=0; i<dataTable.getNumberOfDataRows(); i++){
            dataTable.addCellData(i, 0, Integer.toString(i+1));
        }

    }
}
