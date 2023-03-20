package edu.uob;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

public class SelectCommand extends Interpreter{


    public void SelectCommand() throws InterpreterException, FileNotFoundException {
        ArrayList<String> selectedAttributes = new ArrayList<>();
        getNextToken();

        while (!accept(Parser.TokenType.FROM)) {
            accept(Parser.TokenType.COMMA);
            if (isCurrentToken(Parser.TokenType.PLAIN_TXT) || isCurrentToken(Parser.TokenType.ATTRIB_NAME)) {
                selectedAttributes.add(getCurrentToken().getValue());
            }
            getNextToken();
        }
        String tableName = getCurrentToken().getValue();
        Table table = findTable(tableName);
        getNextToken();

        if (selectedAttributes.size() == 0) {
            selectedAttributes = table.getAttributes().getAttributesAsList();
        }
        ArrayList<Integer> columns = new ArrayList<>();
        int column;
        String attributes = "";
        for (String s : selectedAttributes) {
            checkAttributeName(s, tableName);
            column = table.getAttributes().getAttributePosition(s);
            attributes = attributes + table.getAttributes().getAttributeByNumber(column) + "\t";
            columns.add(column);
        }
        HashSet<Long> conditionalSet = null;

        if (accept(Parser.TokenType.WHERE)) {
            conditionalSet = conditionCommand(table, conditionalSet);
        }
        String result = attributes + "\n" + table.getDataColumnsAsString(columns, conditionalSet);
        ic.setResult(result);
    }


    private HashSet<Long> conditionCommand(Table table, HashSet<Long> conditionalSet) {
        if(isCurrentToken(Parser.TokenType.PLAIN_TXT) || isCurrentToken(Parser.TokenType.ATTRIB_NAME)) {
            conditionalSet = baseConditionCommand(table);
        }
        if(isCurrentToken(Parser.TokenType.BOOL_OP)){
            conditionalSet = operationBoolean(table, conditionalSet);
        }
        if(accept(Parser.TokenType.OPEN_BR)){
            conditionalSet = conditionCommand(table, conditionalSet);
            if(accept(Parser.TokenType.CLOSE_BR)){
                conditionalSet = conditionCommand(table, conditionalSet);
            }
        }
        return conditionalSet;
    }


    private HashSet<Long> baseConditionCommand(Table table){
        HashSet<Long> setReturn = new HashSet<>();
        HashSet<Long> setEqual = new HashSet<>();
        String columnStr = getCurrentToken().getValue();
        if(accept(Parser.TokenType.PLAIN_TXT) || accept(Parser.TokenType.ATTRIB_NAME)){
            int column = table.getAttributes().getAttributePosition(columnStr);
            String condition = getCurrentToken().getValue();
            String target = getNextToken().getValue();
            if(condition.equals("==") || condition.equals(">=") || condition.equals("<=") || condition.equals("!=")){
                setEqual=table.getEqualHash(column-1, target, true);
                setReturn=setEqual;
            }
            if(condition.equals(">") || condition.equals(">=") || condition.equals("<=") || condition.equals("<")){
                HashSet<Long> setGreaterLess = table.getGreaterOrLessHash(column-1, target, condition);
                if(condition.equals(">=") || condition.equals("<=")) {
                    setGreaterLess.addAll(setEqual);
                }
                setReturn=setGreaterLess;
            }
            if(condition.equals("LIKE")){
                setEqual=table.getLikeHash(column-1, target);
                setReturn=setEqual;
            }
        }
        getNextToken();
        return setReturn;
    }

    private HashSet<Long> operationBoolean(Table table, HashSet<Long> conditionalSet) {

        HashSet<Long> conditionalSetAfterBool = new HashSet<>();
        if (getCurrentToken().getValue().equals("AND")) {
            getNextToken();
            conditionalSetAfterBool = conditionCommand(table, conditionalSet);
            conditionalSet.retainAll(conditionalSetAfterBool);
        } else if (getCurrentToken().getValue().equals("OR")) {
            getNextToken();
            conditionalSetAfterBool = conditionCommand(table, conditionalSet);
            conditionalSet.addAll(conditionalSetAfterBool);
        }
        return conditionalSet;
    }

}
