package edu.uob;

import java.io.File;
import java.io.FileNotFoundException;

public class DropCommand extends Interpreter{

    public void DropCommand() throws FileNotFoundException {
        getNextToken();

        if (accept(Parser.TokenType.DATABASE)) {
            String fileLocation = ic.getStorageFolderPath() + File.separator + getCurrentToken().getValue();
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
}
