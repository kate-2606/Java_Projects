package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class UseCommand extends Interpreter{

    public void UseCommand(){
        if(debugging){
            System.out.println("in interpret use");
        }
        Token token = getNextToken();
        String databaseName = token.getValue();

        Database workingDatabase = new Database(databaseName);
        ic.setWorkingDatabase(workingDatabase);
        ic.setDatabasePath(databaseName);
    }

}
