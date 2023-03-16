package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

public class Database {

    public Database(String name){ databaseName = name; }
    private ArrayList<Table> database = new ArrayList<>();

    private String databaseName;

    public String getName() { return databaseName;}

    public void addTable(Table newTable){
        database.add(newTable);
    }

    public Table tableExists(String tableName){
        for(int i=0; i<database.size(); i++){
            Table table = database.get(i);
            if(Objects.equals(table.getTableName(), tableName)){
                return table;
            }
        }
        return null;
    }



}















