package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import static edu.uob.TokenType.*;
import static edu.uob.TokenType.EQUALS;

public class UpdateCommand extends Interpreter {


    public UpdateCommand(InterpContext context, ArrayList<Token> tokens) throws InterpreterException, IOException {
        super(context, tokens);
    }

    public void interpretCommand() throws InterpreterException, IOException {
        String tableName=getName();
        Table table =findTable(tableName);
        ArrayList<String[]> valuePairs = new ArrayList<>();
        getNextToken();
        System.out.println(getCurrentToken().getType() + " " + getCurrentToken().getValue());
        accept(SET);
        System.out.println(getCurrentToken().getType() + " " + getCurrentToken().getValue());
        int i=0;
        while(!accept(WHERE)){
            accept(COMMA);
            String[] valuePair = {"",""};
            if(isCurrentToken(PLAIN_TXT) || isCurrentToken(ATTRIB_NAME)){
                valuePair[0] = getCurrentToken().getValue();
                getNextToken();
            }

            accept(EQUALS);
            if(isValue()){
                valuePair[1] = getCurrentToken().getValue();
                getNextToken();
            }
            valuePairs.add(valuePair);
        }
        System.out.println(getCurrentToken().getType() + " " + getCurrentToken().getValue());
        HashSet<Long> conditionalSet = new HashSet<>();
        conditionalSet=conditionCommand(table, conditionalSet);
        table.updateTableData(valuePairs, conditionalSet);
        exportTable(table);
    }
}
