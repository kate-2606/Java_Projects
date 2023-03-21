package edu.uob;

import java.io.IOException;
import java.util.ArrayList;

import static edu.uob.TokenType.ADD;
import static edu.uob.TokenType.DROP;

public class AlterCommand extends Interpreter{

    public AlterCommand(InterpContext context, ArrayList<Token> tokens) throws InterpreterException, IOException {
        super(context, tokens);
        interpretCommand();
        /*
        try{
        }
        } catch (InterpreterException | IOException e) {
            System.out.println(e.getMessage());
        }
        */
    }
    public void interpretCommand() throws InterpreterException, IOException {
        String tableName = getName();
        Table table = findTable(tableName);

        getNextToken();
        if(accept(ADD)){
            table.addAttribute(getCurrentToken().getValue());
        }
        if(accept(DROP)){
            table.removeAttribute(getCurrentToken().getValue());
        }
        exportTable(table);
    }

}
