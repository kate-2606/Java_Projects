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
        database.add(newTable);
    }

    public Table tableExists(String tableName){
        for(int i=0; i<database.size(); i++){
            Table table = database.get(i);
            if(Objects.equals(table.getName(), tableName)){
                return table;
            }
        }
        return null;
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















