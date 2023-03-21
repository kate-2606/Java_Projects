package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
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

    private StartCommand interpretCommand(String command, InterpContext ic){
        ArrayList<Token> tokens = new ArrayList<>();
        Lexer lexer = new Lexer();
        lexer.Lexer(command, tokens);
        Parser parser = new Parser();
        parser.Parser(tokens, lexer);

        if (parser.getParserResult()) {
            ic.InterpContext(storageFolderPath);
            StartCommand startCommand = new StartCommand();
            startCommand.StartCommand(tokens, ic);
            return startCommand;
        }
        return null;
    }

    @Test
    public void testBasicCreateAndCommand1() {
        InterpContext ic = new InterpContext();
        interpretCommand("CREATE DATABASE coursework;", ic);
        interpretCommand("USE coursework;", ic);
        String path = ic.getDatabasePath();
        String databasePath = path.substring(path.lastIndexOf(File.separator)+1);
        assertEquals("coursework", databasePath );
        assertEquals("coursework", ic.getWorkingDatabase().getName());
        interpretCommand("CREATE TABLE firstTest (column1, column2, column3);", ic);
        Database database = ic.getWorkingDatabase();
        assertEquals(1, database.getNumberOfTables());
        Table table = database.getTableByName("firstTest");
        assertEquals("id", table.getAttributes().getAttributeByNumber(0));
        assertEquals("column1", table.getAttributes().getAttributeByNumber(1));
        assertEquals("column2", table.getAttributes().getAttributeByNumber(2));
        assertEquals("column3", table.getAttributes().getAttributeByNumber(3));
        assertEquals(4, table.getNumberOfAttributes());
        interpretCommand("ALTER TABLE firstTest DROP column3;", ic);
        assertEquals(3, table.getNumberOfAttributes());
        interpretCommand("ALTER TABLE firstTest ADD column4;", ic);
        assertEquals(3, table.getAttributes().getAttributePosition("column4"));
        interpretCommand("INSERT INTO firstTest VALUES('OXO', 'Carparks', 'Db');", ic);
        interpretCommand("INSERT INTO firstTest VALUES('OXO', 'Carparks', 'Db');", ic);
        interpretCommand("INSERT INTO firstTest VALUES('OXO', 'Carparks', 'Db');", ic);
        assertEquals(4, table.getNextPrimaryKey());
        interpretCommand("DROP DATABASE coursework;", ic);
    }

    //try to create a table which already exists and do operations on it

    @Test
    public void testBasicCreateAndCommand2() throws InterpreterException, FileNotFoundException {
        InterpContext ic = new InterpContext();
        interpretCommand("CREATE DATABASE food;", ic);
        interpretCommand("USE food;", ic);
        interpretCommand("CREATE TABLE fruit (yellow, green, red);", ic);
        interpretCommand("INSERT INTO fruit VALUES('pineapple', 'apple', 'apple');", ic);
        interpretCommand("INSERT INTO fruit VALUES('banana', 'kiwi','raspberry');", ic);
        interpretCommand("SELECT * FROM fruit;", ic);
        assertTrue(ic.getResult().contains("pineapple"));
        interpretCommand("SELECT red FROM fruit;", ic);
        assertTrue(ic.getResult().contains("red"));
        assertTrue(ic.getResult().contains("raspberry"));
        interpretCommand("SELECT red FROM fruit WHERE red=='apple';", ic);
        assertFalse(ic.getResult().contains("raspberry"));
        assertTrue(ic.getResult().contains("apple"));
        interpretCommand("DROP DATABASE food;", ic);
    }

    @Test
    public void testTablePersistsAfterRestart() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + "test"+ ";");
        sendCommandToServer("USE " + "test"+ ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Pete', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Quentin', 33, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Paul', 10, FALSE);");
        String response = sendCommandToServer("SELECT * FROM marks WHERE mark==65;");
        assertTrue(response.contains("Steve"));
        assertFalse(response.contains("Pete"));
        response = sendCommandToServer("SELECT * FROM marks WHERE mark<65;");
        assertFalse(response.contains("Steve"));
        assertTrue(response.contains("Pete"));
        assertTrue(response.contains("Quentin"));
        assertTrue(response.contains("Paul"));
        response = sendCommandToServer("SELECT * FROM marks WHERE mark<'poodle';");
        assertFalse(response.contains("Steve"));
        assertFalse(response.contains("Pete"));
        assertFalse(response.contains("Quentin"));
        assertFalse(response.contains("Paul"));
        response = sendCommandToServer("SELECT * FROM marks WHERE mark!=10;");
        assertFalse(response.contains("Steve"));
        assertFalse(response.contains("Pete"));
        assertFalse(response.contains("Quentin"));
        assertTrue(response.contains("Paul"));
        response = sendCommandToServer("SELECT * FROM marks WHERE mark LIKE 5;");
        assertTrue(response.contains("Steve"));
        assertTrue(response.contains("Pete"));
        assertFalse(response.contains("Quentin"));
        assertFalse(response.contains("Paul"));
        response = sendCommandToServer("SELECT name FROM marks WHERE mark LIKE 5 AND name LIKE 'Steve';");
        assertTrue(response.contains("Steve"));
        assertFalse(response.contains("Pete"));
        assertFalse(response.contains("Quentin"));
        assertFalse(response.contains("Paul"));
        sendCommandToServer("DROP DATABASE test;");
    }



    @Test
    public void testNestedSelectCommand() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + "students" + ";");
        sendCommandToServer("USE " + "students" + ";");
        sendCommandToServer("CREATE TABLE details (name, email, hairColour);");
        sendCommandToServer("INSERT INTO details VALUES ('Steve', 's@gmail.com', 'brown');");
        sendCommandToServer("INSERT INTO details VALUES ('Pete', 'p@hotmail.co.uk', 'brown');");
        sendCommandToServer("INSERT INTO details VALUES ('Quentin', 'q@bluebottle.co.uk', 'silver');");
        sendCommandToServer("INSERT INTO details VALUES ('Paul', 'paul@hotmail.com', 'pink');");
        sendCommandToServer("INSERT INTO details VALUES ('Fran', 'fran@hotmail.com', 'pink');");
        String response = sendCommandToServer("SELECT * FROM details WHERE (email LIKE 'hotmail' AND hairColour=='brown') OR hairColour=='silver';");
        sendCommandToServer("DROP DATABASE students;");
    }


    //add exceptions so handles invalid attribute

    @Test
    private void testReadTableFile() throws IOException {
        sendCommandToServer("CREATE DATABASE coursework;");
        sendCommandToServer("CREATE TABLE java (assignment, bugs, numOfFunction, enjoyable);");
        //exists isn't working in testing? -- passes when true or false....
    }
}