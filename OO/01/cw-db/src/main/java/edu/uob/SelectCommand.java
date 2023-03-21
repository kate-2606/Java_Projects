package edu.uob;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import static edu.uob.TokenType.*;

public class SelectCommand extends Interpreter{

    public SelectCommand(InterpContext context, ArrayList<Token> tokens) throws InterpreterException, IOException {
        super(context, tokens);
    }

    public void interpretCommand() throws InterpreterException, FileNotFoundException {
        ArrayList<String> selectedAttributes = new ArrayList<>();
        getNextToken();

        while (!accept(FROM)) {
            accept(COMMA);
            if (isCurrentToken(PLAIN_TXT) || isCurrentToken(ATTRIB_NAME)) {
                selectedAttributes.add(getCurrentToken().getValue());
            }
            getNextToken();
        }
        String tableName = getCurrentToken().getValue();
        Table table = findTable(tableName);
        getNextToken();

        if (selectedAttributes.size() == 0) {
            selectedAttributes = table.getAttributesAsList();
        }
        ArrayList<Integer> columns = new ArrayList<>();
        int column;
        String attributes = "";
        for (String s : selectedAttributes) {
            checkAttributeName(s, tableName);
            column = table.getAttributePosition(s);
            attributes = attributes + table.getAttributeByNumber(column) + "\t";
            columns.add(column);
        }
        HashSet<Long> conditionalSet = null;

        if (accept(WHERE)) {
            conditionalSet = conditionCommand(table, conditionalSet);
        }
        String result = attributes + "\n" + table.getDataColumnsAsString(columns, conditionalSet);
        context.setResult(result);
    }

}
