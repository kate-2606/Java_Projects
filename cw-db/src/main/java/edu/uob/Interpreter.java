package edu.uob;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.nio.file.Paths;
import java.nio.file.*;
import java.nio.*;


public class Interpreter {

    private boolean debugging = true;
    public void Interpreter(ArrayList<Token> tokenList, InterpContext inpIc){
        ic=inpIc;
        tokens=tokenList;
        try {
            command();
        } catch (IOException e) {
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
    private Table readTableFile(String fileName) throws FileNotFoundException {

        Table readTable = null;
        File fileToOpen = new File(ic.getStorageFolderPath() + File.separator + fileName);

        if (!(fileToOpen.isDirectory()) && fileToOpen.exists()) {
            if(fileName.lastIndexOf(File.separator)!=-1){
                String name = fileName.substring(fileName.lastIndexOf(File.separator)+1, fileName.lastIndexOf("."));
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
            //throw exception saying file name is invalid
        }
        return readTable;
    }

    private void exportTable(Table table, File fileToOpen) throws IOException{

        FileWriter writer = new FileWriter(fileToOpen);
        BufferedWriter buffWriter = new BufferedWriter(writer);
        String dataOut = table.getAttributesAsString();
        System.out.println(table.getAttributesAsString());
        if(dataOut!=null){
            buffWriter.write(dataOut + "\n");
        }
        if (table.getNumberOfDataRows()>0) {
            dataOut = table.getRowsAsString();
            if (dataOut != null) {
                buffWriter.write(dataOut);
            }
        }
        buffWriter.flush();
        buffWriter.close();
    }


    public void join(){

    }

    public void delete(){

    }

    public void update(){

    }

    public void select(){
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

    public void insert(){

    }

    public void alter(){

    }

    public void drop() throws FileNotFoundException{
        getNextToken();
        String fileLocation = ic.getStorageFolderPath() + File.separator + getCurrentToken().getValue();
        File file = new File(fileLocation);
        if (accept(Parser.TokenType.DATABASE)) {
            String name = getCurrentToken().getValue();
            deleteFiles(file, name);
            ic.setWorkingDatabase(null);
        }

        if (accept(Parser.TokenType.TABLE)) {
            String name = getCurrentToken().getValue();
            if (file.exists() && !file.isDirectory()) {
                if(!file.delete())
                  throw new FileNotFoundException("Failed to delete " + name + " table");
                //ic.getWorkingDatabase().getTable()
            }
        }
    }

    private void deleteFiles(File file, String name) throws FileNotFoundException{
        if (file.exists() && file.isDirectory()) {
            for (File f : file.listFiles())
                deleteFiles(f, name);
            if (!file.delete()) {
                throw new FileNotFoundException("Failed to delete " + name + " database");
            }
        }
    }

    public void create() throws IOException {
        if (debugging) {
            System.out.println("in interpret create");
        }
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

    private void createTable() throws IOException{
        Token token = getNextToken();

        Table newTable = new Table(token.getValue());
        ic.getWorkingDatabase().addTable(newTable);
        String tablePath =ic.getDatabasePath() + File.separator + newTable.getName() + ".tab";
        File f = new File(tablePath);

        if (f.exists()) {
            throw new IOException("Table already exists");
        }
        try {
            Files.createFile(Paths.get(tablePath));
        } catch (IOException e) {
            throw new IOException("Could not create table");
        }

        getNextToken();
        ArrayList<String> attributes = new ArrayList<>();
        while (!accept(Parser.TokenType.SEMI_COL)) {
            if (isCurrentToken(Parser.TokenType.PLAIN_TXT)){
                attributes.add(getCurrentToken().getValue());
            }
            getNextToken();

        }
        if(attributes.size()!=0){
            newTable.addRowFromCommand(true, attributes);
            exportTable(newTable, f);
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


    private void command() throws IOException {
        Token token = getCurrentToken();
        if(debugging){
            System.out.println("in commandType, token type is: "+ token.getType());
        }
        switch (token.getType()){
            case USE : use();
                break;
            case CREATE : create();
                break;
            case DROP : drop();
                break;
            case ALTER : alter();
                break;
            case INSERT : insert();
                break;
            case SELECT : select();
                break;
            case UPDATE : update();
                break;
            case DELETE : delete();
                break;
            case JOIN : join();
                break;
            default:
        }
    }
}
