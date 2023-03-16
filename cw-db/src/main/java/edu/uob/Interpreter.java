package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.nio.*;

public class Interpreter {

    private boolean debugging = true;
    public void Interpreter(String serverStorageFolderPath, ArrayList<Token> tokenList){
        storageFolderPath=serverStorageFolderPath;
        tokens=tokenList;
        interpretCommand();
    }

    private ArrayList<Token> tokens;

    private int tokenIndex=0;

    private String storageFolderPath;

    private Database workingDatabase = null;

    private String databasePath;

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

    public Token getCurrentToken() {return tokens.get(tokenIndex); }

    public Token getNextToken() {
        tokenIndex++;
        if(tokenIndex<=tokens.size()){
            return tokens.get(tokenIndex);
        }
        return null;
     }

    private Table readTableFile(String fileName) throws FileNotFoundException {

        Table readTable = null;

        File fileToOpen = new File(storageFolderPath + File.separator + fileName);

        if (!(fileToOpen.isDirectory()) && fileToOpen.exists()) {
            //line too long
            if(fileName.lastIndexOf(File.separator)!=-1){
                String tableName = fileName.substring(fileName.lastIndexOf(File.separator)+1, fileName.lastIndexOf("."));
                readTable = new Table(tableName);

                try {
                    FileReader reader = new FileReader(fileToOpen);
                    BufferedReader buffReader = new BufferedReader(reader);
                    String line = buffReader.readLine();
                    readTable.setAttributes(true, line, null);

                    while ((line = buffReader.readLine()) != null) {
                       readTable.addRow(true, line, null);
                    }
                    readTable.primaryKey();

                } catch (IOException e) {
                    throw new FileNotFoundException();
                    //this exception is in the wrong place
                }
            }
            //throw exception saying file name is invalid

        }
        return readTable;
    }

    //private void exportTable


    public void interpretJoin(){

    }

    public void interpretDelete(){

    }

    public void interpretUpdate(){

    }

    public void interpretSelect(){
        String result = null;
        getNextToken();
        if(accept(Parser.TokenType.WILD_CRD)){
            String tableName=getNextToken().getValue();
            Table table = workingDatabase.tableExists(tableName);
            if(table!=null){
                result = table.getAttributesAsString() + "\n";
                result = result + table.getRowsAsString();
            }
        }
        output = result;
    }

    public void interpretInsert(){

    }

    public void interpretAlter(){

    }

    public void interpretDrop(){
        getNextToken();
        //check this deletes every file in the directory
        if(accept(Parser.TokenType.DATABASE)){
            String fileLocation = storageFolderPath + File.separator + getCurrentToken().getValue();
            File fileToDelete = new File(fileLocation);
            if (fileToDelete.exists() && fileToDelete.isDirectory()) {
                fileToDelete.delete();
            }
            else if (!debugging){
                System.out.println("Could not delete database as it does not exist");
            }
        }
        if (accept(Parser.TokenType.TABLE)){
            String fileLocation = databasePath + File.separator + getCurrentToken().getValue();
            File fileToDelete = new File(fileLocation);
            if (fileToDelete.exists() && !fileToDelete.isDirectory()) {
                fileToDelete.delete();
            }
            else if (!debugging){
                System.out.println("Could not delete table as it does not exist in the working database");
            }
        }

    }

    public void interpretCreate(){
        Token token = getNextToken();
        if(accept(Parser.TokenType.DATABASE)) {
            token = getNextToken();
            workingDatabase = new Database(token.getValue());
        }
        //make an error message here
        if(accept(Parser.TokenType.TABLE) && workingDatabase!=null) {

            Table newTable = new Table(token.getValue());
            workingDatabase.addTable(newTable);

            if(getNextToken().getType()!= Parser.TokenType.SEMI_COL){

                ArrayList<String> newAttributes = new ArrayList<>();

                while(getCurrentToken().getType()!= Parser.TokenType.SEMI_COL) {
                    if (getCurrentToken().getType() != Parser.TokenType.COMMA) {
                        getNextToken();
                    }
                    newAttributes.add(getCurrentToken().getValue());
                }
                newTable.setAttributes(false, null, newAttributes);
            }
        }
    }

    public void interpretUse(){
        Token token = getNextToken();
        String databaseName = token.getValue();
        databasePath = storageFolderPath + File.separator + databaseName ;
    }

    private void interpretCommand(){
        Token token = getCurrentToken();
        if(debugging){
            System.out.println("in commandType, token type is: "+ token.getType());
        }
        boolean ret;
        switch (token.getType()){
            case USE : interpretUse();
                break;
            case CREATE : interpretCreate();
                break;
            case DROP : interpretDrop();
                break;
            case ALTER : interpretAlter();
                break;
            case INSERT : interpretInsert();
                break;
            case SELECT : interpretSelect();
                break;
            case UPDATE : interpretUpdate();
                break;
            case DELETE : interpretDelete();
                break;
            case JOIN : interpretJoin();
                break;
            default:
        }
    }
}
