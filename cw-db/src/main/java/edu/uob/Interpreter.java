package edu.uob;

import com.sun.jdi.InternalException;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.nio.file.Paths;



//change method names to verb noun format
// if in create table the table attributes are difftablename.something --> throw an exception

public class Interpreter {

    private boolean debugging = false;
    public void Interpreter(ArrayList<Token> tokenList, InterpContext inpIc){
        ic=inpIc;
        tokens=tokenList;
        try {
            commandCommand();
        } catch (InterpreterException | IOException e) {
            System.out.println(e.getMessage());
        }
    }


    private ArrayList<Token> tokens;

    private int tokenIndex=0;

    private InterpContext ic;


    private boolean accept(Parser.TokenType t){
        Token tok = getCurrentToken();
        if (tok.getType()==t){
            if (t != Parser.TokenType.SEMI_COL) {
                getNextToken();
            }
            return true;
        }
        return false;
    }


    private Token getCurrentToken() {return tokens.get(tokenIndex); }

    private boolean isCurrentToken(Parser.TokenType type) {
        if(getCurrentToken().getType() == type){
            return true;
        }
        return false;
    }



    private Token getNextToken() {
        tokenIndex++;
        if(tokenIndex<=tokens.size()){
            return tokens.get(tokenIndex);
        }
        return null;
     }


     //could check for that each row has the correct number of columns?
    private Table readTableFile(String tableName) throws FileNotFoundException, InterpreterException {

        if(ic.getDatabasePath()!=null && !ic.getWorkingDatabase().tableExists(tableName)) {
            File fileToOpen = new File(ic.getDatabasePath() + File.separator + tableName + ".tab");
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
                ic.getWorkingDatabase().addTable(readTable);
                return readTable;
            }
            return null;
        }
        throw new InterpreterException.WorkingDatabaseIsNull();
    }

    private void exportTable(Table table) throws IOException, InterpreterException {
        if(ic.getDatabasePath()!=null) {
            String path = ic.getDatabasePath() + File.separator + table.getName()+".tab";
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
            return ;
        }
        throw new InterpreterException.WorkingDatabaseIsNull();
    }

    private boolean checkAttributeName(String attributeName, String tableName){
        if(attributeName.contains(".")) {
            String[] names = attributeName.split(".");
            if (tableName.equals(names[0])) {
                return false;
            }
        }
        return true;
    }

    public Table findTable(String tableName) throws InterpreterException, FileNotFoundException {
        if(ic.getWorkingDatabase()!=null) {
            Database database = ic.getWorkingDatabase();
            Table table = database.getTableByName(tableName);
            if (table == null) {
                 table= readTableFile(tableName);
                 if(table==null){
                     throw new InterpreterException.AccessingNonExistentTable(tableName);
                 }
            }
            return table;
        }
        throw new InterpreterException.WorkingDatabaseIsNull();
    }

    private String getName(){
        while(getCurrentToken().getType()!=Parser.TokenType.PLAIN_TXT){
            getNextToken();
        }
        return getCurrentToken().getValue();
    }


    public void joinCommand(){

    }

    public void deleteCommand(){

    }

    public void updateCommand(){
        String tableName=getNextToken().getValue();
    }


    public void selectCommand() throws InterpreterException, FileNotFoundException {
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


    public void insertCommand() throws InterpreterException, IOException {
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

    public void alterCommand() throws InterpreterException, IOException {
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

    public void dropCommand() throws FileNotFoundException{
        getNextToken();

        if (accept(Parser.TokenType.DATABASE)) {
            String fileLocation = ic.getStorageFolderPath() + File.separator + getCurrentToken().getValue();
            File file = new File(fileLocation);
            String name = getCurrentToken().getValue();
            deleteFiles(file, name);
            ic.setWorkingDatabase(null);
        }

        if (accept(Parser.TokenType.TABLE)) {
            String fileLocation = ic.getDatabasePath() + File.separator + getCurrentToken().getValue() + ".tab";
            File file = new File(fileLocation);
            String name = getCurrentToken().getValue();
            if (file.exists() && !file.isDirectory()) {
                if(!file.delete())
                  throw new FileNotFoundException("Failed to delete " + name + " table");
                ic.getWorkingDatabase().removeTable(name);
            }
        }
    }


    private void deleteFiles(File file, String name) throws FileNotFoundException{
        if (file.exists() && file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteFiles(f, name);
            }
        }
        if (!file.delete()) {
            throw new FileNotFoundException("Failed to delete " + name + " database");
        }
    }

    public void createCommand() throws IOException, InterpreterException {
        getNextToken();
        if (accept(Parser.TokenType.DATABASE)) {
            createDatabase();
        }
        if (accept(Parser.TokenType.TABLE) && ic.getDatabasePath() != null) {
            String tableName = getCurrentToken().getValue();
            String tablePath = ic.getDatabasePath() + File.separator + tableName + ".tab";

            File f = new File(tablePath);
            if (f.exists()) {
                if (!ic.getWorkingDatabase().tableExists(tableName)) {
                    Table table = readTableFile(tableName);
                    table.setNextPrimaryKey(-1);
                    ic.getWorkingDatabase().addTable(table);
                }
                throw new InterpreterException.CreatingTableThatExistsAlready(tableName);
            }
            else if (!f.exists()) { createTable(tableName, tablePath); }
        }
    }

    private void createDatabase() throws IOException{
        String dirPath = ic.getStorageFolderPath() + File.separator + getCurrentToken().getValue();
        File f = new File(dirPath);
        if (f.exists()) {
            throw new IOException("Database already exists");
        }
        try {
            Files.createDirectories(Paths.get(dirPath));
        } catch (IOException e) {
            throw new IOException("Could not create database");
        }
    }

    private void createTable(String tableName, String tablePath) throws InterpreterException, IOException{
        try {
            Files.createFile(Paths.get(tablePath));
        } catch (IOException e) {
            throw new IOException("Could not create table");
        }
        Table newTable = new Table(tableName);
        ic.getWorkingDatabase().addTable(newTable);
        newTable.setNextPrimaryKey(1);

        getNextToken();
        ArrayList<String> attributes = new ArrayList<>();
        while (!accept(Parser.TokenType.SEMI_COL)) {
            if (isCurrentToken(Parser.TokenType.PLAIN_TXT) || isCurrentToken(Parser.TokenType.ATTRIB_NAME)) {
                String attributeName = getCurrentToken().getValue();
                if (!checkAttributeName(attributeName, tableName))
                    throw new InterpreterException.ReferencingWrongTable(attributeName);
                attributes.add(attributeName);
            }
            getNextToken();
        }
        if (attributes.size() != 0) {
            newTable.addRowFromCommand(true, attributes);
        }
        exportTable(newTable);
    }



    public void useCommand(){
        if(debugging){
            System.out.println("in interpret use");
        }
        Token token = getNextToken();
        String databaseName = token.getValue();

        Database workingDatabase = new Database(databaseName);
        ic.setWorkingDatabase(workingDatabase);
        ic.setDatabasePath(databaseName);
    }


    private void commandCommand() throws InterpreterException, IOException {
        Token token = getCurrentToken();
        if(debugging){
            System.out.println("in commandType, token type is: "+ token.getType());
        }
        switch (token.getType()){
            case USE : useCommand();
                break;
            case CREATE : createCommand();
                break;
            case DROP : dropCommand();
                break;
            case ALTER : alterCommand();
                break;
            case INSERT : insertCommand();
                break;
            case SELECT : selectCommand();
                break;
            case UPDATE : updateCommand();
                break;
            case DELETE : deleteCommand();
                break;
            case JOIN : joinCommand();
                break;
            default:
        }
    }
}
