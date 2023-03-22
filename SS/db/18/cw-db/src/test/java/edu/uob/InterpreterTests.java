package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.FileNotFoundException;
import java.time.Duration;
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
        sendCommandToServer("CREATE DATABASE creator;");
        sendCommandToServer("USE  creator;");

        sendCommandToServer("CREATE TABLE second (column1, column2, column3);");
        String response = sendCommandToServer("SELECT * FROM second;");
        assertTrue(response.contains("id"));
        assertTrue(response.contains("column1"));
        assertTrue(response.contains("column2"));
        assertTrue(response.contains("column3"));
        sendCommandToServer("ALTER TABLE second DROP column3;");
        response = sendCommandToServer("SELECT * FROM second;");
        assertFalse(response.contains("column3"));
        sendCommandToServer("ALTER TABLE second ADD column4;");
        response = sendCommandToServer("SELECT * FROM second;");
        assertFalse(response.contains("column3"));

        assertTrue(response.contains("column4"));
        sendCommandToServer("INSERT INTO second VALUES('OXO', 'Carparks', 'Db');");
        sendCommandToServer("INSERT INTO second VALUES('OXO', 'Carparks', 'Db');");
        sendCommandToServer("INSERT INTO second VALUES('OXO', 'Carparks', 'Db');");
        response = sendCommandToServer("SELECT * FROM second;");
        assertTrue(response.contains("Db\n2"));
        response = sendCommandToServer("CREATE TABLE SECOND (column1, column2, column3);");
        assertTrue(response.contains("[ERROR]"));
        sendCommandToServer("DROP TABLE second;");
    }



    //try to create a table which already exists and do operations on it

    @Test
    public void testBasicCreateAndCommand2() throws InterpreterException, FileNotFoundException {

        sendCommandToServer("CREATE DATABASE food;");
        sendCommandToServer("USE food;");
        sendCommandToServer("CREATE TABLE fruit (yellow, green, red);");
        sendCommandToServer("INSERT INTO fruit VALUES('pineapple', 'apple', 'apple');");
        sendCommandToServer("INSERT INTO fruit VALUES('banana', 'kiwi','raspberry');");
        String response = sendCommandToServer("SELECT * FROM fruit;");
        assertTrue(response.contains("pineapple"));
        assertTrue(response.contains("red"));
        assertTrue(response.contains("yellow"));
        assertTrue(response.contains("green"));
        response=sendCommandToServer("SELECT red FROM fruit;");
        assertTrue(response.contains("red"));
        assertTrue(response.contains("raspberry"));
        response=sendCommandToServer("SELECT red FROM fruit WHERE red=='apple';");
        assertFalse(response.contains("raspberry"));
        assertTrue(response.contains("apple"));
        sendCommandToServer("DROP DATABASE food;");

    }

    @Test
    public void testBasicCreateAndCommand3() throws InterpreterException, IOException {
        sendCommandToServer("CREATE DATABASE creator;");
        sendCommandToServer("USE  creator;");
        String response = sendCommandToServer("CREATE TABLE elvis ();");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testBasicCreateAndCommand4() throws InterpreterException, IOException {
        sendCommandToServer("CREATE DATABASE creator;");
        sendCommandToServer("USE  creator;");
        String response = sendCommandToServer("CREATE TABLE elvis (ADD, column2, column3);");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testBasicCreateAndCommand5() throws InterpreterException, IOException {
        sendCommandToServer("CREATE DATABASE creator;");
        sendCommandToServer("USE  creator;");
        String response = sendCommandToServer("CREATE TABLE elvis (, , ,);");
        assertTrue(response.contains("[ERROR]"));
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
        assertFalse(response.contains("Steve"));
        assertFalse(response.contains("Pete"));
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
        String response = sendCommandToServer("SELECT name FROM marks WHERE mark == 65 AND name LIKE 'Steve';");
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
    public void testDeleteBasicCommand2() {
        sendCommandToServer("USE " + "help"+ ";");
        String response = sendCommandToServer("INSERT INTO t VALUES ('Pamela', 12, FALSE);");
        response = sendCommandToServer("SELECT * FROM t;");
        assertFalse(response.contains("4"));
        assertTrue(response.contains("5\tPamela"));
        sendCommandToServer("DROP DATABASE help;");
    }



    @Test
    public void testBracketedSelectCommand() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + "students" + ";");
        sendCommandToServer("USE " + "students" + ";");
        sendCommandToServer("CREATE TABLE details (name, email, hairColour, height);");
        sendCommandToServer("INSERT INTO details VALUES ('Steve', 's@gmail.com', 'brown', 175);");
        sendCommandToServer("INSERT INTO details VALUES ('Pete', 'p@hotmail.co.uk', 'brown',201);");
        sendCommandToServer("INSERT INTO details VALUES ('Quentin', 'q@bluebottle.co.uk', 'silver', 154);");
        sendCommandToServer("INSERT INTO details VALUES ('Paul', 'paul@hotmail.com', 'pink', 187);");
        sendCommandToServer("INSERT INTO details VALUES ('Fran', 'fran@example.com', 'pink', 150);");
        String response = sendCommandToServer("SELECT * FROM details WHERE (email LIKE 'hotmail' AND hairColour=='brown') AND hairColour!='silver';");
        assertFalse(response.contains("Steve"));
        assertFalse(response.contains("Paul"));
        assertTrue(response.contains("Pete"));
        assertFalse(response.contains("Quentin"));

    }

    @Test
    public void testNestedSelectCommand1() {
        sendCommandToServer("USE " + "students" + ";");
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
    public void testUpdateJoinCommand1() throws InterpreterException {
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
        sendCommandToServer("SELECT * FROM cars;");
        sendCommandToServer("UPDATE cars SET CARS.REG='Unknown' WHERE cars.brand=='Fail';");
        assertEquals("Unknown", table.getRow(5).getCellDataByNumber(0));
    }


    @Test
    public void testUpdateJoinCommand2() throws InterpreterException {

    }


    @Test
    public void testUpdateJoinCommand3() throws InterpreterException {
        sendCommandToServer("USE " + "vehicles" + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass, iq);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE, 650);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE, 350);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE, 200);");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE, 130);");
        String result = sendCommandToServer("JOIN marks AND cars ON iq AND hp;");
        assertFalse(result.contains("hp"));
        assertFalse(result.contains("iq"));
    }

    @Test
    public void testUpdateJoinCommand4() throws InterpreterException {
        sendCommandToServer("USE " + "vehicles" + ";");
        String result = sendCommandToServer("SELECT marks.mark FROM marks;");
        assertFalse(result.contains("Steve"));
        assertFalse(result.contains("TRUE"));
        assertFalse(result.contains("200"));
    }


    @Test
    public void testCaseInsensitive1() throws InterpreterException {
        sendCommandToServer("CREATE DATABASE " + "case" + ";");
        sendCommandToServer("USE " + "case" + ";");
        sendCommandToServer("CREATE TABLE Point (name, email, hairColour, height);");
        sendCommandToServer("INSERT INTO POINT VALUES ('Steve', 's@gmail.com', 'brown', 175);");
        sendCommandToServer("INSERT INTO point VALUES ('Pete', 'p@hotmail.co.uk', 'brown',201);");
        sendCommandToServer("INSERT INTO point VALUES ('Quentin', 'q@bluebottle.co.uk', 'silver', 154);");
        sendCommandToServer("INSERT INTO point VALUES ('Paul', 'paul@hotmail.com', 'pink', 187);");
        sendCommandToServer("INSERT INTO point VALUES ('Fran', 'fran@example.com', 'pink', 150);");
        sendCommandToServer("SELECT * FROM point;");
        String response = sendCommandToServer("SELECT * FROM point WHERE (((email LIKE 'hotmail' AND hairColour=='brown') OR " +
                "(hairColour=='pink' AND email LIKE 'example')) AND height>150) OR name=='Quentin';");
        String response1 = sendCommandToServer("select * FROM poinT where (((email like 'hotmail' and POINT.hairColour=='brown') or " +
                "(hairColour=='pink' and email like 'example')) and height>150) or name=='Quentin';");
        String response2 = sendCommandToServer("select * FROM point where (((Email like 'hotmail' and HAIRCOLOUR=='brown') or " +
                "(hairColour=='pink' and email like 'example')) and height>150) or name=='Quentin';");
        assertTrue(response.contains("name\temail\thairColour\theight"));
        System.out.println(response1);
        assertTrue(response1.contains(response));
        assertTrue(response.contains(response1));
        assertTrue(response.contains(response2));
        sendCommandToServer("DROP TABLE POINT;");
    }

    //test update comand considers name value pairs

    @Test
    private void testReadTableFile() throws IOException {
        sendCommandToServer("CREATE DATABASE coursework;");
        sendCommandToServer("CREATE TABLE java (assignment, bugs, numOfFunction, enjoyable);");
        //exists isn't working in testing? -- passes when true or false....
    }


    @Test
    public void testTranscript() throws InterpreterException {
        String randomName = generateRandomName();
        String result=sendCommandToServer("CREATE DATABASE "+ randomName +";");
        assertEquals("[OK]\n", result);
        result=sendCommandToServer("USE " + randomName +";");
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

        //sendCommandToServer("DROP DATABASE "+ randomName +";");
        //finish this
    }


}