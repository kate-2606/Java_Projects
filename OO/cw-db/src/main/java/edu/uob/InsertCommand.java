package edu.uob;

import java.io.IOException;
import java.util.ArrayList;

public class InsertCommand extends Interpreter{
    public void InsertCommand() throws InterpreterException, IOException {
        String tableName = getName();
        Table table = findTable(tableName);
        ArrayList<String> values = new ArrayList<>();
        while (!accept(Parser.TokenType.SEMI_COL)){
            getNextToken();
            if(getCurrentToken().getValue()!=null){
                values.add(getCurrentToken().getValue());
            }
        }

        table.addRowFromCommand(false, values);
        exportTable(table);
    }
}
