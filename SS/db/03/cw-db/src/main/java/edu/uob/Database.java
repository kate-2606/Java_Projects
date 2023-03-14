package edu.uob;

import java.util.ArrayList;

public class Database {
    private ArrayList<Table> database = new ArrayList<>();

    public Database(){

    }

    public void addTable(Table newTable){
        database.add(newTable);
    }
}
