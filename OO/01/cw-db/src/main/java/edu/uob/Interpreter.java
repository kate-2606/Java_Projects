package edu.uob;

import java.io.*;
import java.util.ArrayList;


//change method names to verb noun format
// if in create table the table attributes are difftablename.something --> throw an exception

public abstract class Interpreter {

    protected boolean debugging = false;


    public ArrayList<Token> tokens;

    protected int tokenIndex=0;

    protected InterpContext ic;


    protected boolean accept(Parser.TokenType t){
        Token tok = getCurrentToken();
        if (tok.getType()==t){
            if (t != Parser.TokenType.SEMI_COL) {
                getNextToken();
            }
            return true;
        }
        return false;
    }


    protected Token getCurrentToken() {return tokens.get(tokenIndex); }

    protected boolean isCurrentToken(Parser.TokenType type) {
        if(getCurrentToken().getType() == type){
            return true;
        }
        return false;
    }


    protected Token getNextToken() {
        tokenIndex++;
        if(tokenIndex<=tokens.size()){
            return tokens.get(tokenIndex);
        }
        return null;
    }

    protected void deleteFiles(File file, String name) throws FileNotFoundException{
        if (file.exists() && file.isDirectory()) {
            for (File f : file.listFiles()) {
                deleteFiles(f, name);
            }
        }
        if (!file.delete()) {
            throw new FileNotFoundException("Failed to delete " + name + " database");
        }
    }


    protected String getName(){
        while(getCurrentToken().getType()!=Parser.TokenType.PLAIN_TXT){
            getNextToken();
        }
        return getCurrentToken().getValue();
    }


    //could check for that each row has the correct number of columns?
    protected Table readTableFile(String tableName) throws FileNotFoundException, InterpreterException {

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

    protected void exportTable(Table table) throws IOException, InterpreterException {
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

    protected boolean checkAttributeName(String attributeName, String tableName){
        if(attributeName.contains(".")) {
            String[] names = attributeName.split(".");
            if (tableName.equals(names[0])) {
                return false;
            }
        }
        return true;
    }

    protected Table findTable(String tableName) throws InterpreterException, FileNotFoundException {
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


}
