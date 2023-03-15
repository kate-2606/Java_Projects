package edu.uob;

import java.util.ArrayList;

public class Database {

    public Database(String name){ databaseName = name; }
    private ArrayList<Table> database = new ArrayList<>();

    private String databaseName;

    public String getName() { return databaseName;}

    public void addTable(Table newTable){
        database.add(newTable);
    }


}
