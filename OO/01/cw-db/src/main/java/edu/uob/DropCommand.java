package edu.uob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static edu.uob.TokenType.DATABASE;
import static edu.uob.TokenType.TABLE;

public class DropCommand extends Interpreter{

    public DropCommand(InterpContext context, ArrayList<Token> tokens) throws InterpreterException, IOException {
        super(context, tokens);
        interpretCommand();
        /*
        try{
        }
        } catch (InterpreterException | IOException e) {
            System.out.println(e.getMessage());
        }
        */
    }
    public void interpretCommand() throws FileNotFoundException, InterpreterException {
        getNextToken();

        if (accept(DATABASE)) {
            String fileLocation = context.getStorageFolderPath() + File.separator + getCurrentToken().getValue();
            File file = new File(fileLocation);
            String name = getCurrentToken().getValue();
            deleteFiles(file, name);
            context.setWorkingDatabase(null);
        }

        if (accept(TABLE)) {
            String fileLocation = context.getDatabasePath() + File.separator + getCurrentToken().getValue() + ".tab";
            File file = new File(fileLocation);
            String name = getCurrentToken().getValue();
            if (file.exists() && !file.isDirectory()) {
                if(!file.delete())
                    throw new FileNotFoundException("Failed to delete " + name + " table");
                context.getWorkingDatabase().removeTable(name);
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
}
