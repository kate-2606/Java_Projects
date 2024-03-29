package edu.uob;

import java.io.File;

public class InterpContext {

    public void initiateContext(String path){
        storageFolderPath = path;
    }
    private String storageFolderPath;

    private String databasePath;

    private Database workingDatabase;

    private String result = "";

    public void setResult(String result){ this.result=result; }

    public void prependResult(String result){
        String temp = this.result;
        this.result=result+temp; }

    public String getResult() {return result; }

    public String getStorageFolderPath() { return storageFolderPath; }

    public void  setDatabasePath(String databaseName) {
        databasePath = storageFolderPath + File.separator + databaseName ;
    }

    public  String getDatabasePath(){ return databasePath; }

    public void setWorkingDatabase(Database workingDatabase) { this.workingDatabase = workingDatabase; }

    public Database getWorkingDatabase() throws InterpreterException {
        if(workingDatabase!=null){
            return workingDatabase;
        }
        throw new InterpreterException.WorkingDatabaseIsNull();
    }

}
