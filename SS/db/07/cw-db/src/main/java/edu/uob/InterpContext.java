package edu.uob;

import java.io.File;

public class InterpContext {

    public void InterpContext(String path){
        storageFolderPath = path;
    }
    private String storageFolderPath;

    private String databasePath;

    private Database workingDatabase;

    private String result;

    public void setResult(String result){ this.result=result; }

    public String getResult() {return result; }

    public String getStorageFolderPath() { return storageFolderPath; }

    public void  setDatabasePath(String databaseName) {
        databasePath = storageFolderPath + File.separator + databaseName ;
    }

    public  String getDatabasePath(){ return databasePath; }

    public void setWorkingDatabase(Database workingDatabase) { this.workingDatabase = workingDatabase; }

    public Database getWorkingDatabase() { return workingDatabase; }

}