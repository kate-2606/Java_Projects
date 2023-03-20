package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class InterpreterTests {
    private DBServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    String storageFolderPath = Paths.get("databases").toAbsolutePath().toString();

    private String generateRandomName()
    {
        String randomName = "";
        for(int i=0; i<10 ;i++) randomName += (char)( 97 + (Math.random() * 25.0));
        return randomName;
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    private Interpreter interpretCommand(String command, InterpContext ic){
        ArrayList<Token> tokens = new ArrayList<>();
        Lexer lexer = new Lexer();
        lexer.Lexer(command, tokens);
        Parser parser = new Parser();
        parser.Parser(tokens, lexer);
        if (parser.getParserResult()) {
            ic.InterpContext(storageFolderPath);
            Interpreter interpreter = new Interpreter();
            interpreter.Interpreter(tokens, ic);
            return interpreter;
        }
        return null;
    }

    @Test
    public void testBasicCreateAndCommand() {

        InterpContext ic = new InterpContext();
        interpretCommand("CREATE DATABASE coursework;", ic);
        interpretCommand("USE coursework;", ic);
        String path = ic.getDatabasePath();
        String database = path.substring(path.lastIndexOf(File.separator)+1);
        assertEquals("coursework", database );
        assertEquals("coursework", ic.getWorkingDatabase().getName());
        interpretCommand("CREATE TABLE firstTest (column1, column2, column3);", ic);
        System.out.println(ic.getWorkingDatabase().getName()+" number of tables is "+ ic.getWorkingDatabase().getNumberOfTables());
        System.out.println(ic.getWorkingDatabase().getTableByIndex(0).getName());
        assertEquals(1, ic.getWorkingDatabase().getNumberOfTables());
        interpretCommand("DROP DATABASE coursework;", ic);

    }

    @Test
    private void testReadTableFile() throws IOException {
        sendCommandToServer("CREATE DATABASE coursework;");
        sendCommandToServer("CREATE TABLE java (assignment, bugs, numOfFunction, enjoyable);");
        //exists isn't working in testing? -- passes when true or false....
    }
}