package edu.uob;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.nio.file.Paths;
import static edu.uob.TokenType.*;



//change method names to verb noun format

public class Interpreter {

    private final boolean debugging = false;


    public Interpreter(ArrayList<Token> tokenList, InterpContext inpIc){
        ic=inpIc;
        tokens=tokenList;
        try {
            commandCommand();
            ic. prependResult("[OK]\n");
        } catch (InterpreterException | IOException e) {
            ic.setResult("[ERROR]\n"+ e.getMessage() + "\n");
        }
    }


    private ArrayList<Token> tokens;

    private int tokenIndex=0;

    private InterpContext ic;


    private boolean accept(TokenType t){
        Token tok = getCurrentToken();
        if (tok.getType()==t){
            if (t != SEMI_COL) {
                getNextToken();
            }
            return true;
        }
        return false;
    }


    private Token getCurrentToken() {return tokens.get(tokenIndex); }

    private boolean isCurrentToken(TokenType type) {
        return getCurrentToken().getType() == type;
    }



    private Token getNextToken() {
        tokenIndex++;
        if(tokenIndex<=tokens.size()){
            return tokens.get(tokenIndex);
        }
        return null;
     }

     private boolean isValue() {
         TokenType[] values = {STRING_LIT, BOOL_LIT, FLOAT_LIT, INT_LIT, NULL};
        for(TokenType t : values){
            if(isCurrentToken(t)) return true;
        }
        return false;
     }


     //could check for that each row has the correct number of columns?
    private Table readTableFile(String tableName) throws FileNotFoundException, InterpreterException {

        if(!ic.getWorkingDatabase().tableExists(tableName)) {
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
        }
        return null;
    }

    private void exportTable(Table table) throws IOException {
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
    }

    private void checkAttributeName(String attributeName, String tableName) throws InterpreterException {
        Table table = ic.getWorkingDatabase().getTableByName(tableName);
        if(attributeName.contains(".")) {
            String[] names = attributeName.split("[.]");
            if (!tableName.equals(names[0])) {
                throw new InterpreterException.AccessingNonExistentTable(tableName);
            }
            table.getAttributePosition(names[1]);
        }
        else{
            table.getAttributePosition(attributeName);
        }
    }

    public Table findTable(String tableName) throws InterpreterException, FileNotFoundException {
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

    private String getName(){
        while(getCurrentToken().getType()!=PLAIN_TXT){
            getNextToken();
        }
        return getCurrentToken().getValue();
    }


    public void joinCommand() throws InterpreterException {
        String tableName=getName();
        System.out.println(tableName);
        Table tableA = ic.getWorkingDatabase().getTableByName(tableName);
        getNextToken();
        tableName=getName();
        System.out.println(tableName);
        Table tableB = ic.getWorkingDatabase().getTableByName(tableName);
        getNextToken();
        String attributeA=getName();
        getNextToken();
        String attributeB=getName();

        if(getName().contains(tableA.getName())){
            attributeA=getName();
            getNextToken();
            attributeB=getName();
        }
        if(getName().contains(tableB.getName())){
            attributeB=getName();
            getNextToken();
            attributeA=getName();
        }
        checkAttributeName(attributeA, tableA.getName());
        checkAttributeName(attributeB, tableB.getName());

        ic.setResult(joinHelper(tableA, tableB, attributeA, attributeB));
    }

    private String joinHelper(Table tableA, Table tableB, String attributeA, String attributeB)
            throws InterpreterException {
        ArrayList<Long[]> joinMap = new ArrayList<>();
        HashMap<Long, Row> mapA = tableA.getDataRows();
        HashMap<Long, Row> mapB = tableB.getDataRows();
        String cellA = "";  String cellB = "";
        int posA = tableA.getAttributePosition(attributeA)-1;
        int posB = tableB.getAttributePosition(attributeB)-1;

        for(Long l : mapA.keySet()){
            System.out.println("key here");
            cellA=mapA.get(l).getCellDataByNumber(posA);
            for(Long k : mapB.keySet()){
               cellB=mapB.get(k).getCellDataByNumber(posB);
                System.out.println(cellA + " " + cellB);
               if(cellA.equals(cellB)){
                   Long[] match = {l, k};
                   joinMap.add(match);
                   System.out.println("match " + match);
                }
            }
        }
        return joinMerge(tableA, tableB, attributeA, attributeB, joinMap);
    }



    private String joinMerge(Table tableA, Table tableB, String attributeA, String attributeB, ArrayList<Long[]> map)
            throws InterpreterException {
        String result = "";
        System.out.println("here");
        for(Long[] l : map){
            System.out.println("here1");
            long keyA = l[0];
            int i=0;
            ArrayList<String> row = new ArrayList<>();
            row.add(String.valueOf(keyA));
            while (i<tableA.getNumberOfAttributes()-1){
                if(i!=tableA.getAttributePosition(attributeA)-1)
                    result = result + tableA.getRow(keyA).getCellDataByNumber(i) + "\t";
                i++;
            }
            long keyB = l[0];
            i=0;
            while (i<tableB.getNumberOfAttributes()-1){
                if(i!=tableB.getAttributePosition(attributeB)-1)
                    result = result + tableB.getRow(keyB).getCellDataByNumber(i) + "\t";
                i++;
            }
            result = result.trim() + "\n";
        }
        return result;
    }


    public void deleteCommand(){

    }

    public void updateCommand() throws InterpreterException, IOException {
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


    public void selectCommand() throws InterpreterException, FileNotFoundException {
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
        ic.setResult(result);
    }


    private HashSet<Long> conditionCommand(Table table, HashSet<Long> conditionalSet) throws InterpreterException {
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


    private HashSet<Long> baseConditionCommand(Table table) throws InterpreterException {
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


    private HashSet<Long> baseConditionEquals(int column, String target, String condition, Table table){
        HashSet<Long> setEqual = new HashSet<>();
        if (condition.equals("!="))
            setEqual=table.getEqualHash(column-1, target, false);
        else
            setEqual = table.getEqualHash(column - 1, target, true);
        return setEqual;
    }


    private HashSet<Long> operationBoolean(Table table, HashSet<Long> conditionalSet) throws InterpreterException {

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
        while (!accept(SEMI_COL)){
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
        if(accept(ADD)){
            table.addAttribute(getCurrentToken().getValue());
        }
        if(accept(DROP)){
            table.removeAttribute(getCurrentToken().getValue());
        }
        exportTable(table);
    }

    public void dropCommand() throws FileNotFoundException, InterpreterException {
        getNextToken();

        if (accept(DATABASE)) {
            String fileLocation = ic.getStorageFolderPath() + File.separator + getCurrentToken().getValue();
            File file = new File(fileLocation);
            String name = getCurrentToken().getValue();
            deleteFiles(file, name);
            ic.setWorkingDatabase(null);
        }

        if (accept(TABLE)) {
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
        if (accept(DATABASE)) {
            createDatabase();
        }
        if (accept(TABLE) && ic.getDatabasePath() != null) {
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
        while (!accept(SEMI_COL)) {
            if (isCurrentToken(PLAIN_TXT) || isCurrentToken(ATTRIB_NAME)) {
                String attributeName = getCurrentToken().getValue();
                checkAttributeName(attributeName, tableName);
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
