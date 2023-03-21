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

public class InterpreterBasicTests {
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
        ic.InterpContext(storageFolderPath);
        Lexer lexer = new Lexer();
        lexer.Lexer(command, tokens, ic);
        Parser parser = new Parser();
        parser.Parser(tokens, lexer, ic);
        if (parser.getParserResult()) {
            Interpreter interpreter = new Interpreter(tokens, ic);
            return interpreter;
        }
        return null;
    }

    @Test
    public void testBasicCreateAndCommand1() throws InterpreterException, IOException {
        InterpContext ic = new InterpContext();
        interpretCommand("CREATE DATABASE coursework;", ic);
        interpretCommand("USE coursework;", ic);
        String path = ic.getDatabasePath();
        String databasePath = path.substring(path.lastIndexOf(File.separator)+1);
        assertEquals("coursework", databasePath );
        assertEquals("coursework", ic.getWorkingDatabase().getName());
        interpretCommand("CREATE TABLE firstTest (column1, column2, column3);", ic);
        Database database = ic.getWorkingDatabase();
        //assertEquals(1, ic.getNumberOfTables());
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
        interpretCommand("SELECT * FROM firstTest;", ic);

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
    public void testSelectBasicCommand0() {
        sendCommandToServer("CREATE DATABASE " + "test"+ ";");
        sendCommandToServer("USE " + "test"+ ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Pete', 55, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Quentin', 33, TRUE);");
        sendCommandToServer("INSERT INTO marks VALUES ('Paul', 10, FALSE);");

        String response = sendCommandToServer("SELECT * FROM marks WHERE mark LIKE 5;");
        assertTrue(response.contains("Steve"));
        assertTrue(response.contains("Pete"));
        assertFalse(response.contains("Quentin"));
        assertFalse(response.contains("Paul"));
    }

    @Test
    public void testSelectBasicCommand1() {
        sendCommandToServer("USE " + "test"+ ";");
        String response = sendCommandToServer("SELECT * FROM marks WHERE mark==65;");
        assertTrue(response.contains("Steve"));
        assertFalse(response.contains("Pete"));
    }

    @Test
    public void testSelectBasicCommand2() {
        sendCommandToServer("USE " + "test"+ ";");
        String response = sendCommandToServer("SELECT * FROM marks WHERE mark<65;");
        assertFalse(response.contains("Steve"));
        assertTrue(response.contains("Pete"));
        assertTrue(response.contains("Quentin"));
        assertTrue(response.contains("Paul"));
    }

    @Test
    public void testSelectBasicCommand3() {
        sendCommandToServer("USE " + "test"+ ";");
        String response = sendCommandToServer("SELECT * FROM marks WHERE mark<'poodle';");
        assertFalse(response.contains("Steve"));
        assertFalse(response.contains("Pete"));
        assertFalse(response.contains("Quentin"));
        assertFalse(response.contains("Paul"));
    }

    @Test
    public void testSelectBasicCommand4() {
        sendCommandToServer("USE " + "test"+ ";");
        String response = sendCommandToServer("SELECT * FROM marks WHERE mark!=10;");
        assertTrue(response.contains("Steve"));
        assertTrue(response.contains("Pete"));
        assertTrue(response.contains("Quentin"));
        assertFalse(response.contains("Paul"));
    }

    @Test
    public void testSelectBasicCommand5() {
        sendCommandToServer("USE " + "test"+ ";");
        String response = sendCommandToServer("SELECT name FROM marks WHERE mark LIKE 5 AND name LIKE 'Steve';");
        assertTrue(response.contains("Steve"));
        assertFalse(response.contains("Pete"));
        assertFalse(response.contains("Quentin"));
        assertFalse(response.contains("Paul"));
        sendCommandToServer("DROP DATABASE test;");
    }


    @Test
    public void testDeleteBasicCommand1() {
        sendCommandToServer("CREATE DATABASE " + "help"+ ";");
        sendCommandToServer("USE " + "help"+ ";");
        sendCommandToServer("CREATE TABLE t (name, mark, pass);");
        sendCommandToServer("INSERT INTO t VALUES ('Steve', 65, TRUE);");
        sendCommandToServer("INSERT INTO t VALUES ('Pete', 55, TRUE);");
        sendCommandToServer("INSERT INTO t VALUES ('Quentin', 33, TRUE);");
        sendCommandToServer("INSERT INTO t VALUES ('Paul', 10, FALSE);");
        sendCommandToServer("DELETE FROM t WHERE pass==FALSE;");
        String response = sendCommandToServer("SELECT * FROM t;");
        assertTrue(response.contains("Steve"));
        assertTrue(response.contains("Pete"));
        assertTrue(response.contains("Quentin"));
        assertFalse(response.contains("Paul"));
    }

    @Test
    public void testDeleteBasicCommand() {

    }
/*
    @Test
    public void testKeyPersists() {
        sendCommandToServer("USE " + "test"+ ";");
        String response = sendCommandToServer("INSERT INTO marks VALUES ('Pamela', 12, FALSE);");
        System.out.println(response);
        response = sendCommandToServer("SELECT * FROM marks;");
        System.out.println(response);
        assertFalse(response.contains("4"));
        assertTrue(response.contains("5\tPamela"));
        //sendCommandToServer("DROP DATABASE test;");
    }
 */



    @Test
    public void testBracketedSelectCommand() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + "students" + ";");
        sendCommandToServer("USE " + "students" + ";");
        System.out.println("create");
        sendCommandToServer("CREATE TABLE details (name, email, hairColour, height);");
        System.out.println("insert 1");
        sendCommandToServer("INSERT INTO details VALUES ('Steve', 's@gmail.com', 'brown', 175);");
        System.out.println("insert 2");
        sendCommandToServer("INSERT INTO details VALUES ('Pete', 'p@hotmail.co.uk', 'brown',201);");
        sendCommandToServer("INSERT INTO details VALUES ('Quentin', 'q@bluebottle.co.uk', 'silver', 154);");
        sendCommandToServer("INSERT INTO details VALUES ('Paul', 'paul@hotmail.com', 'pink', 187);");
        sendCommandToServer("INSERT INTO details VALUES ('Fran', 'fran@example.com', 'pink', 150);");
        System.out.println("select");
        String response = sendCommandToServer("SELECT * FROM details WHERE (email LIKE 'hotmail' AND hairColour=='brown') AND hairColour!='silver';");
        assertFalse(response.contains("Steve"));
        assertFalse(response.contains("Paul"));
        assertTrue(response.contains("Pete"));
        assertFalse(response.contains("Quentin"));

    }

    @Test
    public void testNestedSelectCommand1() {
        sendCommandToServer("USE " + "students" + ";");
        System.out.println("select");
        String response = sendCommandToServer("SELECT * FROM details WHERE ((email LIKE 'hotmail' AND hairColour=='brown') OR " +
                "(hairColour=='pink' AND email LIKE 'example')) OR name=='Quentin';");
        assertFalse(response.contains("Steve"));
        assertFalse(response.contains("Paul"));
        assertTrue(response.contains("Fran"));
        assertTrue(response.contains("Quentin"));
        assertTrue(response.contains("Pete"));
    }


    @Test
    public void testNestedSelectCommand2() {
        sendCommandToServer("USE " + "students" + ";");
        String response = sendCommandToServer("SELECT * FROM details WHERE (((email LIKE 'hotmail' AND hairColour=='brown') OR " +
                "(hairColour=='pink' AND email LIKE 'example')) AND height>150) OR name=='Quentin';");
        assertFalse(response.contains("Steve"));
        assertFalse(response.contains("Paul"));
        assertFalse(response.contains("Fran"));
        assertTrue(response.contains("Quentin"));
        assertTrue(response.contains("Pete"));
    }


    @Test
    public void testNestedSelectCommand3() {
        sendCommandToServer("USE " + "students" + ";");
        String response = sendCommandToServer("SELECT * FROM details WHERE (((email LIKE 'hotmail' AND hairColour=='brown') OR " +
                "(hairColour=='pink' AND email LIKE 'example')) AND (height>=150 AND hairColour!='brown')) OR name=='Quentin';");
        assertFalse(response.contains("Steve"));
        assertFalse(response.contains("Paul"));
        assertTrue(response.contains("Fran"));
        assertTrue(response.contains("Quentin"));
        assertFalse(response.contains("Pete"));
    }

    @Test
    public void testSelectCommandGTE() {
        sendCommandToServer("USE " + "students" + ";");
        String response = sendCommandToServer("SELECT * FROM details WHERE height>=160;");
        assertTrue(response.contains("Steve"));
        assertTrue(response.contains("Paul"));
        assertFalse(response.contains("Fran"));
        assertFalse(response.contains("Quentin"));
        assertTrue(response.contains("Pete"));
        sendCommandToServer("SELECT * FROM details WHERE height>=160;");
    }


    @Test
    public void testUpdateCommand() throws InterpreterException {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + "vehicles" + ";");
        sendCommandToServer("USE " + "vehicles" + ";");
        sendCommandToServer("CREATE TABLE cars(reg, brand, colour, hp, good);");
        sendCommandToServer("INSERT INTO cars VALUES ('YE456H', 'tyota', 'yellow', 500, TRUE);");
        sendCommandToServer("INSERT INTO cars VALUES ('ZCEMKHU', 'tyota', 'red', 450, FALSE);");
        sendCommandToServer("INSERT INTO cars VALUES ('BQ22223', 'fiat', 'blue' , 700, TRUE);");
        sendCommandToServer("INSERT INTO cars VALUES ('P3456H', 'landrover', 'blue', 650, TRUE);");
        sendCommandToServer("INSERT INTO cars VALUES ('HGEMKHU', 'fiat', 'silver', 350, FALSE);");
        sendCommandToServer("INSERT INTO cars VALUES ('QX22223', 'merc', 'blue' , 200, TRUE);");
        sendCommandToServer("UPDATE cars SET brand='Fail' WHERE hp<450;");
        InterpContext ic = server.getInterpretationContext();
        Table table = ic.getWorkingDatabase().getTableByName("cars");
        assertEquals("Fail", table.getRow(5).getCellDataByNumber(1));
        assertEquals("Fail", table.getRow(6).getCellDataByNumber(1));
    }


    @Test
    public void testJoinCommand() throws InterpreterException {
        sendCommandToServer("USE " + "vehicles" + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass, iq);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE, 650);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE, 350);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE, 200);");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE, 130);");
        String result = sendCommandToServer("JOIN marks AND cars ON iq AND hp;");
        assertFalse(result.contains("hp"));
        assertFalse(result.contains("iq"));
        sendCommandToServer("DROP DATABASE vehicles;");
        sendCommandToServer("DROP DATABASE students;");
    }

    @Test
    private void testReadTableFile() throws IOException {
        sendCommandToServer("CREATE DATABASE coursework;");
        sendCommandToServer("CREATE TABLE java (assignment, bugs, numOfFunction, enjoyable);");
        //exists isn't working in testing? -- passes when true or false....
    }

    @Test
    public void testTranscript() throws InterpreterException {
        String result=sendCommandToServer("CREATE DATABASE markbook;");
        assertEquals("[OK]\n", result);
        result=sendCommandToServer("USE markbook;");
        assertEquals("[OK]\n", result);
        result=sendCommandToServer("CREATE TABLE marks (name, mark, pass);");
        assertEquals("[OK]\n", result);
        result=sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        assertEquals("[OK]\n", result);
        result=sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE);");
        assertEquals("[OK]\n", result);
        result=sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE);");
        assertEquals("[OK]\n", result);
        result=sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE);");
        assertEquals("[OK]\n", result);
        result=sendCommandToServer("SELECT * FROM marks;");
        assertTrue(result.contains("Dave"));
        assertTrue(result.contains("Steve"));
        assertTrue(result.contains("Bob"));
        assertTrue(result.contains("Clive"));

        sendCommandToServer("DROP DATABASE markbook;");
        //finish this
    }
}