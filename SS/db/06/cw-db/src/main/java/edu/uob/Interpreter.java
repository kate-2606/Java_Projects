package edu.uob;

import com.sun.jdi.InternalException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.nio.file.*;
import java.nio.*;


//change method names to verb noun format
// if in create table the table attributes are difftablename.something --> throw an exception

public class Interpreter {

    private boolean debugging = true;
    public void Interpreter(ArrayList<Token> tokenList, InterpContext inpIc){
        ic=inpIc;
        tokens=tokenList;
        try {
            commandInterpretation();
        } catch (InterpreterException | IOException e) {
            System.out.println(e.getMessage());
        }
    }


    private ArrayList<Token> tokens;

    private int tokenIndex=0;

    private InterpContext ic;

    private String output = null;



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
    private Table readTableFile(String fileName) throws FileNotFoundException, InterpreterException {

        Table readTable = null;
        if(ic.getDatabasePath()!=null) {
            File fileToOpen = new File(ic.getStorageFolderPath() + File.separator + fileName);

            if (!(fileToOpen.isDirectory()) && fileToOpen.exists()) {
                if (fileName.lastIndexOf(File.separator) != -1) {
                    String name = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.lastIndexOf("."));
                    readTable = new Table(name);

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
                }
                System.out.println("got here " + readTable.getName());
                return readTable;
            }
            throw new InterpreterException.AccessingNonExistentTable(fileName);
        }
        throw new InterpreterException.WorkingDatabaseIsNull();
    }

    private void exportTable(Table table) throws IOException, InterpreterException {
        if(ic.getDatabasePath()!=null) {
            String path = ic.getDatabasePath() + File.separator + table.getName()+".tab";
            File fileToOpen = new File(path);
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

    private Table findTable(String tableName) throws InterpreterException, FileNotFoundException {
        if(ic.getWorkingDatabase()!=null) {
            Database database = ic.getWorkingDatabase();
            Table table = database.getTableByName(tableName);
            if (table != null) {
                return table;
            }
            System.out.println("read table " + tableName);
            return readTableFile(tableName);
        }
        throw new InterpreterException.WorkingDatabaseIsNull();
    }


    public void joinInterpretation(){

    }

    public void deleteInterpretation(){

    }

    public void updateInterpretation(){

    }

    public void selectInterpretation(){
        String result = null;
        getNextToken();
        if(accept(Parser.TokenType.WILD_CRD)){
            String tableName=getNextToken().getValue();
            Table table = ic.getWorkingDatabase().tableExists(tableName);
            if(table!=null){
                result = table.getAttributesAsString() + "\n";
                result = result + table.getRowsAsString();
            }
        }
        output = result;
    }

    public void insertInterpretation(){

    }

    public void alterInterpretation() throws InterpreterException, IOException {
        while(getCurrentToken().getType()!=Parser.TokenType.PLAIN_TXT){
            getNextToken();
        }
        String tableName = getCurrentToken().getValue();
        Table table = findTable(tableName);

        getNextToken();
        if(accept(Parser.TokenType.ADD)){
            Attributes attributes =table.getAttributes();
            attributes.addAttribute(getCurrentToken().getValue());
        }
        if(accept(Parser.TokenType.DROP)){
            table.removeAttribute(getCurrentToken().getValue());
        }
        exportTable(table);
    }

    public void dropInterpretation() throws FileNotFoundException{
        getNextToken();

        if (accept(Parser.TokenType.DATABASE)) {
            String fileLocation = ic.getStorageFolderPath() + File.separator + getCurrentToken().getValue();
            System.out.println(fileLocation);
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
                System.out.println("deleting " + name);
                deleteFiles(f, name);
            }
        }
        if (!file.delete()) {
            throw new FileNotFoundException("Failed to delete " + name + " database");
        }
    }

    public void createInterpretation() throws IOException, InterpreterException {
        Token token = getNextToken();
        if (accept(Parser.TokenType.DATABASE)) {
            createDatabase();
        }
        if (accept(Parser.TokenType.TABLE) && ic.getDatabasePath() != null) {
            createTable();
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

    private void createTable() throws InterpreterException, IOException{
        String tableName = getCurrentToken().getValue();
        Table newTable = new Table(tableName);
        ic.getWorkingDatabase().addTable(newTable);
        String tablePath =ic.getDatabasePath() + File.separator + newTable.getName() + ".tab";
        File f = new File(tablePath);

        if (f.exists()) { throw new InterpreterException.TableAlreadyExists(tableName); }
        try {
            Files.createFile(Paths.get(tablePath));
        } catch (IOException e) { throw new IOException("Could not create table"); }

        getNextToken();
        ArrayList<String> attributes = new ArrayList<>();
        while (!accept(Parser.TokenType.SEMI_COL)) {
            if (isCurrentToken(Parser.TokenType.PLAIN_TXT) || isCurrentToken(Parser.TokenType.ATTRIB_NAME)){
                String attributeName = getCurrentToken().getValue();
                if(!checkAttributeName(attributeName, tableName))
                    throw new InterpreterException.ReferencingWrongTable(attributeName);
                attributes.add(attributeName);
            }
            getNextToken();
        }
        if(attributes.size()!=0){
            newTable.addRowFromCommand(true, attributes);
            exportTable(newTable);
        }
    }


    public void use(){
        if(debugging){
            System.out.println("in interpret use");
        }
        Token token = getNextToken();
        String databaseName = token.getValue();

        Database workingDatabase = new Database(databaseName);
        ic.setWorkingDatabase(workingDatabase);
        ic.setDatabasePath(databaseName);
    }


    private void commandInterpretation() throws InterpreterException, IOException {
        Token token = getCurrentToken();
        if(debugging){
            System.out.println("in commandType, token type is: "+ token.getType());
        }
        switch (token.getType()){
            case USE : use();
                break;
            case CREATE : createInterpretation();
                break;
            case DROP : dropInterpretation();
                break;
            case ALTER : alterInterpretation();
                break;
            case INSERT : insertInterpretation();
                break;
            case SELECT : selectInterpretation();
                break;
            case UPDATE : updateInterpretation();
                break;
            case DELETE : deleteInterpretation();
                break;
            case JOIN : joinInterpretation();
                break;
            default:
        }
    }
}
