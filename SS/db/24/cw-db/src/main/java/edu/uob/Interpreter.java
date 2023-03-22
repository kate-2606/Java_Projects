package edu.uob;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.nio.file.Paths;
import static edu.uob.TokenType.*;



//change method names to verb noun format

public class Interpreter {

    private final boolean debugging = false;


    public Interpreter(ArrayList<Token> tokenList, InterpContext inpIc){
        ic=inpIc;
        tokens=tokenList;
        if(!ic.getResult().contains("[ERROR]")) {
            try {
                commandCommand();
                ic.prependResult("[OK]\n");
            } catch (InterpreterException | IOException e) {
                ic.setResult("[ERROR]\n" + e.getMessage() + "\n");
            }
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

     private void isNotKeyWord(String name) throws InterpreterException {
        int i=0;
         TokenType type=TokenType.values()[i];
         while (type!=OPEN_BR){
             type=TokenType.values()[i];
             if(type.toString().equalsIgnoreCase(name)){
                 throw new InterpreterException.MatchingKeyWord();
             }
             i++;
         }
         if(name.equalsIgnoreCase("AND") || name.equalsIgnoreCase("OR") || name.equalsIgnoreCase("LIKE")){
             throw new InterpreterException.MatchingKeyWord();
         }
         if(name.equalsIgnoreCase("TRUE") || name.equalsIgnoreCase("FALSE")){
             throw new InterpreterException.MatchingKeyWord();
         }
     }


     //could check for that each row has the correct number of columns?
    private Table readTableFile(String tableName) throws FileNotFoundException, InterpreterException {

        if(!ic.getWorkingDatabase().tableExists(tableName)) {
            File fileToOpen = new File(ic.getDatabasePath() + File.separator + tableName + ".tab");
            if (!(fileToOpen.isDirectory()) && fileToOpen.exists()) {
                Table readTable = new Table(tableName, ic.getDatabasePath());
                try {
                    FileReader reader = new FileReader(fileToOpen);
                    BufferedReader buffReader = new BufferedReader(reader);
                    String line = buffReader.readLine();
                    readTable.addRowFromFile(true, line);

                    while ((line = buffReader.readLine()) != null) {
                        readTable.addRowFromFile(false, line);
                    }
                    buffReader.close();
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

    private void exportTable(Table table) throws IOException, InterpreterException {
        String path = ic.getDatabasePath() + File.separator + table.getName()+".tab";

        File fileToOpen = new File(path);
        if(fileToOpen.exists()){
            fileToOpen.delete();
        }
        fileToOpen = new File(path);
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

    private void checkAttributeName(String attributeName, String tableName, Boolean creatingTable) throws InterpreterException {
        Table table = ic.getWorkingDatabase().getTableByName(tableName.toLowerCase());

        if(attributeName.contains(".")) {
            String[] names = attributeName.split("[.]");

            isNotKeyWord(names[0]);
            isNotKeyWord(names[1]);

            if (!tableName.equalsIgnoreCase(names[0])) {
                throw new InterpreterException.InvalidAttributeName(attributeName);
            }
            if (table.getAttributePosition(names[1]) == -1 && !creatingTable) {
                throw new InterpreterException.AccessingNonExistentAttribute(attributeName);
            }
        }
        else {
            isNotKeyWord(attributeName);
            if (table.getAttributePosition(attributeName)==-1 && !creatingTable) {
                throw new InterpreterException.AccessingNonExistentAttribute(attributeName);
            }
        }
        isNotKeyWord(attributeName);
    }

    public Table findTable(String tableName) throws InterpreterException, FileNotFoundException {
        Database database = ic.getWorkingDatabase();
        Table table = database.getTableByName(tableName.toLowerCase());

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


    //NULL OR BOOL CANT USE < >
    public void joinCommand() throws InterpreterException, FileNotFoundException {
        Table tableA = findTable(getName());
        getNextToken();
        Table tableB = findTable(getName());
        getNextToken();  accept(ON);
        String nameA = getCurrentToken().getValue();
        getNextToken();  accept(BOOL_OP);
        String nameB = getCurrentToken().getValue();

        if(nameA!=nameB && !nameB.contains(".") && !nameA.contains(".")){
            if(tableA.attributeExists(nameA) && tableB.attributeExists(nameA)){
                throw new InterpreterException.ContainedInTwoTables(nameA);
            }
            if(tableA.attributeExists(nameB) && tableB.attributeExists(nameB)){
                throw new InterpreterException.ContainedInTwoTables(nameB);
            }
        }

        String attributeA = attributeBelongsTo(nameA, nameB, tableA);
        String attributeB = attributeBelongsTo(nameA, nameB, tableB);

        checkAttributeName(attributeA, tableA.getName(), false);
        checkAttributeName(attributeB, tableB.getName(), false);
        ic.setResult(joinHelper(tableA, tableB, attributeA, attributeB));
    }

    private String attributeBelongsTo(String nameA, String nameB, Table table) throws InterpreterException {
        if(nameA.contains(".")) {
            String[] names = nameA.split("[.]");
            if(table.getName().equalsIgnoreCase(names[0])) {
                checkAttributeName(nameA, table.getName(), false);
                return names[1].toLowerCase();
            }
        }
        if(nameB.contains(".")) {
            String[] names = nameB.split("[.]");
            if(table.getName().equalsIgnoreCase(names[0])) {
                checkAttributeName(nameB, table.getName(), false);
                return names[1].toLowerCase();
            }
        }
        else if (!table.attributeExists(nameA)) {
            if (!table.attributeExists(nameB)) {
                throw new InterpreterException("attribute " + nameA + "does not exist in either table");
            }
            return nameB;
        }
        return nameA;
    }

    private String joinHelper(Table tableA, Table tableB, String attributeA, String attributeB)
            throws InterpreterException {
        ArrayList<Long[]> joinMap = new ArrayList<>();
        HashMap<Long, Row> mapA = tableA.getDataRows();
        HashMap<Long, Row> mapB = tableB.getDataRows();
        String cellA;  String cellB;
        int posA = tableA.getAttributePosition(attributeA)-1;
        int posB = tableB.getAttributePosition(attributeB)-1;

        String attributes = tableA.getAttributesAsString().replace(attributeA+"\t", "");

        attributes = attributes + tableB.getAttributesAsString().replace("id\t", "") +"\n";

        attributes = attributes.replace(attributeB+"\t", "");


        int i=0;
        for(Long keyA : mapA.keySet()){
            cellA=mapA.get(keyA).getCellDataByNumber(posA);

            for(Long keyB : mapB.keySet()){
               cellB=mapB.get(keyB).getCellDataByNumber(posB);
               if(cellA.equals(cellB)){
                   Long[] match = {keyA, keyB};
                   joinMap.add(match);
                }
            }
        }
        return attributes + joinMerge(tableA, tableB, attributeA, attributeB, joinMap);
    }



    private String joinMerge(Table tableA, Table tableB, String attributeA, String attributeB, ArrayList<Long[]> map)
            throws InterpreterException {
        String result = "";
        int key=1;
        for(Long[] l : map){
            long keyA = l[0];
            int i=0;
            result =result + key + "\t";
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
            key++;
        }
        return result;
    }


    public void deleteCommand() throws InterpreterException, IOException {
        String tableName=getName();
        Table table = findTable(tableName);
        getNextToken();
        accept(WHERE);
        HashSet<Long> deletedRows = null;
        deletedRows = conditionCommand(table, deletedRows);
        table.deleteRows(deletedRows);
        exportTable(table);
    }

    public void updateCommand() throws InterpreterException, IOException {
        String tableName=getName();
        Table table =findTable(tableName);
        ArrayList<String[]> valuePairs = new ArrayList<>();
        getNextToken();
        accept(SET);
        int i=0;
        while(!accept(WHERE)){
            accept(COMMA);
            String[] valuePair = {"",""};
            if(isCurrentToken(PLAIN_TXT) || isCurrentToken(ATTRIB_NAME)){
                String attributeName = getCurrentToken().getValue();
                checkAttributeName(attributeName, tableName, false);
                if(accept(ATTRIB_NAME) && attributeName.contains(".")){
                    attributeName=attributeName.split("[.]")[1];
                }
                valuePair[0] = attributeName;
                accept(PLAIN_TXT);
            }

            accept(EQUALS);
            if(isValue()){
                valuePair[1] = getCurrentToken().getValue();
                getNextToken();
            }
            valuePairs.add(valuePair);
        }
        HashSet<Long> conditionalSet = new HashSet<>();
        conditionalSet=conditionCommand(table, conditionalSet);
        table.updateTableData(valuePairs, conditionalSet);
        exportTable(table);
    }

//reduce the size of this
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
            checkAttributeName(s, tableName, false);
            if(s.contains(".")){

                String[] arr =s.split("[.]");
                s=arr[1];
            }
            column = table.getAttributePosition(s);
            attributes = attributes + table.getAttributeByNumber(column) + "\t";
            columns.add(column);
        }
        HashSet<Long> selectedRows = null;

        if (accept(WHERE)) {
            selectedRows = conditionCommand(table, selectedRows);
        }
        String data="";
        if(table.getNumberOfDataRows()!=0){
            data = table.getDataColumnsAsString(columns, selectedRows);
        }
        String result = attributes + "\n" + data;
        ic.setResult(result);
    }


    private HashSet<Long> conditionCommand(Table table, HashSet<Long> conditionalSet) throws InterpreterException {
        if(isCurrentToken(PLAIN_TXT) || isCurrentToken(ATTRIB_NAME)) {
            checkAttributeName(getCurrentToken().getValue(), table.getName(), false);
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
        checkAttributeName(columnStr, table.getName(), false);
        if(!accept(PLAIN_TXT) && columnStr.contains(".")){
            columnStr=columnStr.split("[.]")[1];
        }
        accept(ATTRIB_NAME);

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

        if(values.size()!=table.getNumberOfAttributes()-1){
            throw new InterpreterException.InvalidNumberOfValues();
        }

        table.addRowFromCommand(false, values);
        exportTable(table);
    }

    public void alterCommand() throws InterpreterException, IOException {
        String tableName = getName();
        Table table = findTable(tableName);

        getNextToken();
        if(accept(ADD)){
            String name = getName();
            checkAttributeName(name, tableName, true);
            table.addAttribute(getCurrentToken().getValue());
        }
        if(accept(DROP)){
            String name = getName();
            checkAttributeName(name, tableName, false);
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
            String name = getCurrentToken().getValue().toLowerCase();
            String fileLocation = ic.getDatabasePath() + File.separator + name + ".tab";
            String fileKeyLocation = ic.getDatabasePath() + File.separator + name + "Key.txt";
            File file = new File(fileLocation);
            File fileKey = new File(fileKeyLocation);
            if (file.exists() && !file.isDirectory()) {
                if(!file.delete() || !fileKey.delete())
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
            String tableName = getCurrentToken().getValue().toLowerCase();
            isNotKeyWord(tableName);
            String tablePath = ic.getDatabasePath() + File.separator + tableName + ".tab";

            File f = new File(tablePath);
            if (f.exists()) {
                throw new IOException("Table already exists");
            }
            if (f.exists()) {
                if (!ic.getWorkingDatabase().tableExists(tableName)) {
                    Table table = readTableFile(tableName);
                    table.setNextPrimaryKey();
                    ic.getWorkingDatabase().addTable(table);
                }
                throw new InterpreterException.CreatingTableThatExistsAlready(tableName);
            }
            else if (!f.exists()) { createTable(tableName, tablePath); }
        }
    }

    private void createDatabase() throws IOException, InterpreterException {
        String name = getCurrentToken().getValue();
        isNotKeyWord(name);
        String dirPath = ic.getStorageFolderPath() + File.separator + name;
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
        Table newTable = new Table(tableName, ic.getDatabasePath());
        ic.getWorkingDatabase().addTable(newTable);
        newTable.getNextPrimaryKey();

        getNextToken();
        ArrayList<String> attributes = new ArrayList<>();
        while (!accept(SEMI_COL)) {
            if (isCurrentToken(PLAIN_TXT) || isCurrentToken(ATTRIB_NAME)) {
                String attributeName = getCurrentToken().getValue();
                checkAttributeName(attributeName, tableName, true);
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
