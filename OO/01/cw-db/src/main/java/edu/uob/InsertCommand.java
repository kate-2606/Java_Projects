package edu.uob;

import java.io.IOException;
import java.util.ArrayList;

import static edu.uob.TokenType.SEMI_COL;

public class InsertCommand extends Interpreter{

    public InsertCommand(InterpContext context, ArrayList<Token> tokens) throws InterpreterException, IOException {
        super(context, tokens);
    }
    public void interpretCommand() throws InterpreterException, IOException {
        String tableName = getName();
        Table table = findTable(tableName);
        ArrayList<String> values = new ArrayList<>();
        while (!accept(SEMI_COL)){
            getNextToken();
            if(getCurrentToken().getValue()!=null){
                values.add(getCurrentToken().getValue());
            }
        }

        table.addRowFromCommand(false, values);
        exportTable(table);
    }
}
