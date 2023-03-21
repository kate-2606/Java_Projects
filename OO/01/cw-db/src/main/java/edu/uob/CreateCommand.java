package edu.uob;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static edu.uob.TokenType.*;

public class CreateCommand extends Interpreter{

    public CreateCommand(InterpContext context, ArrayList<Token> tokens) {
        super(context, tokens);
    }

    public void interpretCommand() throws IOException, InterpreterException {
        getNextToken();
        if (accept(DATABASE)) {
            createDatabase();
        }
        if (accept(TABLE) && context.getDatabasePath() != null) {
            String tableName = getCurrentToken().getValue();
            String tablePath = context.getDatabasePath() + File.separator + tableName + ".tab";

            File f = new File(tablePath);
            if (f.exists()) {
                if (!context.getWorkingDatabase().tableExists(tableName)) {
                    Table table = readTableFile(tableName);
                    table.setNextPrimaryKey(-1);
                    context.getWorkingDatabase().addTable(table);
                }
                throw new InterpreterException.CreatingTableThatExistsAlready(tableName);
            }
            else if (!f.exists()) { createTable(tableName, tablePath); }
        }
    }

    private void createDatabase() throws IOException{
        String dirPath = context.getStorageFolderPath() + File.separator + getCurrentToken().getValue();
        System.out.println(dirPath);
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
        context.getWorkingDatabase().addTable(newTable);
        System.out.println(context.getWorkingDatabase().getTableByName(tableName));
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
}
