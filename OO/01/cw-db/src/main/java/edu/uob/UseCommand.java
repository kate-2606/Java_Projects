package edu.uob;

import java.io.IOException;
import java.util.ArrayList;

public class UseCommand extends Interpreter{

    public UseCommand(InterpContext context, ArrayList<Token> tokens) throws InterpreterException, IOException {
        super(context, tokens);
    }


    public void interpretCommand(){
        /*
        if(debugging){
            System.out.println("in interpret use");
        }
         */
        Token token = getNextToken();
        String databaseName = token.getValue();

        Database workingDatabase = new Database(databaseName);
        context.setWorkingDatabase(workingDatabase);
        context.setDatabasePath(databaseName);
    }

}
