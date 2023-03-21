package edu.uob;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.nio.file.Paths;
import static edu.uob.TokenType.*;


//change method names to verb noun format
// if in create table the table attributes are difftablename.something --> throw an exception

public abstract class Interpreter {


    private final boolean debugging = false;


    public Interpreter(InterpContext context, ArrayList<Token> tokens) {
        this.context=context;
        this.tokens=tokens;
    }

    abstract public void interpretCommand() throws InterpreterException, IOException;


    protected ArrayList<Token> tokens;

    protected int tokenIndex=0;

    protected InterpContext context;


    protected boolean accept(TokenType t){
        Token tok = getCurrentToken();
        if (tok.getType()==t){
            if (t != SEMI_COL) {
                getNextToken();
            }
            return true;
        }
        return false;
    }


    protected Token getCurrentToken() {return tokens.get(tokenIndex); }

    protected boolean isCurrentToken(TokenType type) {
        return getCurrentToken().getType() == type;
    }



    protected Token getNextToken() {
        tokenIndex++;
        if(tokenIndex<=tokens.size()){
            return tokens.get(tokenIndex);
        }
        return null;
    }

    protected boolean isValue() {
        TokenType[] values = {STRING_LIT, BOOL_LIT, FLOAT_LIT, INT_LIT, NULL};
        for(TokenType t : values){
            if(isCurrentToken(t)) return true;
        }
        return false;
    }


    //could check for that each row has the correct number of columns?
    protected Table readTableFile(String tableName) throws FileNotFoundException, InterpreterException {

        if(!context.getWorkingDatabase().tableExists(tableName)) {
            File fileToOpen = new File(context.getDatabasePath() + File.separator + tableName + ".tab");
            if (!(fileToOpen.isDirectory()) && fileToOpen.exists()) {
                Table readTable = new Table(tableName);
                try {
                    FileReader reader = new FileReader(fileToOpen);
                    BufferedReader buffReader = new BufferedReader(reader);
                    String line = buffReader.readLine();
                    readTable.addRowFromFile(true, line);

                    while ((line = buffReader.readLine()) != null) {
                        readTable.addRowFromFile(false, line);
                    }
                } catch (IOException e) {
                    throw new FileNotFoundException();
                    //this exception is in the wrong place
                }
                context.getWorkingDatabase().addTable(readTable);
                return readTable;
            }
        }
        return null;
    }

    protected void exportTable(Table table) throws IOException {
        String path = context.getDatabasePath() + File.separator + table.getName()+".tab";
        File fileToOpen = new File(path);
        new FileOutputStream(fileToOpen, false).close();
        FileWriter writer = new FileWriter(fileToOpen);
        BufferedWriter buffWriter = new BufferedWriter(writer);
        String dataOut = table.getAttributesAsString();
        if (dataOut != null) {
            buffWriter.write(dataOut + "\n");
        }
        if (table.getNumberOfDataRows() > 0) {
            dataOut = table.getRowsAsString();
            if (dataOut != null) {
                buffWriter.write(dataOut);
            }
        }
        buffWriter.flush();
        buffWriter.close();
    }

    protected void checkAttributeName(String attributeName, String tableName) throws InterpreterException {
        Table table = context.getWorkingDatabase().getTableByName(tableName);
        if(attributeName.contains(".")) {
            String[] names = attributeName.split("[.]");
            if (!tableName.equals(names[0])) {
                throw new InterpreterException.AccessingNonExistentTable(tableName);
            }
            if (table.getAttributePosition(names[1])==-1) {
                throw new InterpreterException.AccessingNonExistentAttribute(attributeName);
            }
        }
    }

    protected Table findTable(String tableName) throws InterpreterException, FileNotFoundException {
        Database database = context.getWorkingDatabase();
        Table table = database.getTableByName(tableName);
        if (table == null) {
            table= readTableFile(tableName);
            if(table==null){
                throw new InterpreterException.AccessingNonExistentTable(tableName);
            }
        }
        return table;
    }

    protected String getName(){
        while(getCurrentToken().getType()!=PLAIN_TXT){
            getNextToken();
        }
        return getCurrentToken().getValue();
    }

    protected HashSet<Long> conditionCommand(Table table, HashSet<Long> conditionalSet) throws InterpreterException {
        if(isCurrentToken(PLAIN_TXT) || isCurrentToken(ATTRIB_NAME)) {
            checkAttributeName(getCurrentToken().getValue(), table.getName());
            conditionalSet = baseConditionCommand(table);
        }
        if(isCurrentToken(BOOL_OP)){
            conditionalSet = operationBoolean(table, conditionalSet);
        }
        if(accept(OPEN_BR)){
            conditionalSet = conditionCommand(table, conditionalSet);
            if(accept(CLOSE_BR)){
                conditionalSet = conditionCommand(table, conditionalSet);
            }
        }
        return conditionalSet;
    }


    protected HashSet<Long> baseConditionCommand(Table table) throws InterpreterException {
        HashSet<Long> setReturn = new HashSet<>();
        HashSet<Long> setEqual = new HashSet<>();
        String columnStr = getCurrentToken().getValue();

        accept(PLAIN_TXT);  accept(ATTRIB_NAME);

        int column = table.getAttributePosition(columnStr);
        String condition = getCurrentToken().getValue();
        String target = getNextToken().getValue();
        if(condition.equals("==") || condition.equals(">=") || condition.equals("<=") || condition.equals("!=")){
            setEqual = baseConditionEquals(column, target, condition, table);
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
        getNextToken();
        return setReturn;
    }


    protected HashSet<Long> baseConditionEquals(int column, String target, String condition, Table table){
        HashSet<Long> setEqual = new HashSet<>();
        if (condition.equals("!="))
            setEqual=table.getEqualHash(column-1, target, false);
        else
            setEqual = table.getEqualHash(column - 1, target, true);
        return setEqual;
    }


    protected HashSet<Long> operationBoolean(Table table, HashSet<Long> conditionalSet) throws InterpreterException {

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
