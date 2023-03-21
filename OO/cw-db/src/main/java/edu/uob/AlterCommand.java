package edu.uob;

import java.io.IOException;

public class AlterCommand extends Interpreter{
    public void AlterCommand() throws InterpreterException, IOException {
        String tableName = getName();
        Table table = findTable(tableName);

        getNextToken();
        if(accept(Parser.TokenType.ADD)){
            table.addAttribute(getCurrentToken().getValue());
        }
        if(accept(Parser.TokenType.DROP)){
            table.removeAttribute(getCurrentToken().getValue());
        }
        exportTable(table);
    }

}
