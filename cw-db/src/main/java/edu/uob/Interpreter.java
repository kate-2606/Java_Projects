package edu.uob;

import java.io.File;
import java.util.ArrayList;

public class Interpreter {

    private boolean debugging = true;
    public void Interpreter(String serverStorageFolderPath, ArrayList<Token> tokenList){
        storageFolderPath=serverStorageFolderPath;
        tokens=tokenList;
    }

    private ArrayList<Token> tokens;

    private int tokenIndex=0;

    private String storageFolderPath;

    private Database workingDatabase = null;

    private String databasePath;

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


    public void interpretJoin(){

    }

    public void interpretDelete(){

    }

    public void interpretUpdate(){

    }

    public void interpretSelect(){

    }

    public void interpretInsert(){

    }

    public void interpretAlter(){

    }

    public void interpretDrop(){
        getNextToken();
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
        if(token.getType() == Parser.TokenType.TABLE && workingDatabase!=null) {
            token = getNextToken();
            Table newTable = new Table(token.getValue());
            workingDatabase.addTable(newTable);
        }
    }

    public void interpretUse(){
        Token token = getNextToken();
        String databaseName = token.getValue();
        databasePath = storageFolderPath + File.separator + databaseName ;
    }

    private void commandType(){
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
