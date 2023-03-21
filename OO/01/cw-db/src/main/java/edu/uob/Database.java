package edu.uob;

import java.util.ArrayList;
import java.util.Objects;

public class Database {

    public Database(String name){
        databaseName = name;
        database = new ArrayList<>();
    }
    private ArrayList<Table> database ;

    private String databaseName;

    public String getName() { return databaseName;}

    public void addTable(Table newTable){
        String name="";
        if(newTable!=null){
            name=newTable.getName();
        }
        database.add(newTable);
    }

    public boolean tableExists(String tableName){
        if(database!=null) {
            for (Table t : database) {
                if (t!=null && t.getName().equals(tableName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Table getTableByName(String name){
        for(Table table : database) {
            if (table.getName().equals(name)) {
                return table;
            }
        }
        return null;
    }

    public Table getTableByIndex(int index) { return database.get(index); }

    public void removeTable (String name){
        database.remove(getTableByName(name));
    }

    public int getNumberOfTables() {
        return database.size();
    }


}















