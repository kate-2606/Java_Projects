package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CreateCommand extends Interpreter{

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
}
