package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

public class InterpreterTests {
    private DBServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    private String generateRandomName()
    {
        String randomName = "";
        for(int i=0; i<10 ;i++) randomName += (char)( 97 + (Math.random() * 25.0));
        return randomName;
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will time out if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }


    @Test
    public void testBasicCreateAndCommand1() {
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
    public void testBasicCreateAndCommand2() {

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
        sendCommandToServer("ALTER TABLE food ADD salt;");
        sendCommandToServer("DROP DATABASE food;");

    }

    @Test
    public void testBasicCreateAndCommand3() {
        sendCommandToServer("CREATE DATABASE creator;");
        sendCommandToServer("USE  creator;");
        String response = sendCommandToServer("CREATE TABLE elvis ();");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testBasicCreateAndCommand4() {
        sendCommandToServer("CREATE DATABASE creator;");
        sendCommandToServer("USE  creator;");
        String response = sendCommandToServer("CREATE TABLE elvis (ADD, column2, column3);");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testBasicCreateAndCommand5() {
        sendCommandToServer("CREATE DATABASE creator;");
        sendCommandToServer("USE  creator;");
        String response = sendCommandToServer("CREATE TABLE elvis (, , ,);");
        assertTrue(response.contains("[ERROR]"));
    }


    @Test
    public void testSelectBasicCommand00() {
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
    }

    @Test
    public void testSelectBasicCommand6() {
        sendCommandToServer("USE " + "test"+ ";");
        String response = sendCommandToServer("SELECT name FROM marks WHERE pass == true;");
        assertTrue(response.contains("Steve"));
        assertTrue(response.contains("Pete"));
        assertTrue(response.contains("Quentin"));
        assertFalse(response.contains("Paul"));
    }

    @Test
    public void testSelectBasicCommand7() {
        sendCommandToServer("USE " + "test"+ ";");
        String response = sendCommandToServer("SELECT name FROM marks WHERE pass == fALse;");
        assertFalse(response.contains("Steve"));
        assertFalse(response.contains("Pete"));
        assertFalse(response.contains("Quentin"));
        assertTrue(response.contains("Paul"));
    }

    @Test
    public void testSelectBasicCommand8() {
        sendCommandToServer("USE " + "test"+ ";");
        String response = sendCommandToServer("SELECT name FROM marks WHERE mark<=fALse;");
        assertFalse(response.contains("Steve"));
        assertFalse(response.contains("Pete"));
        assertFalse(response.contains("Quentin"));
        assertFalse(response.contains("Paul"));
    }

    @Test
    public void testSelectBasicCommand9() {
        sendCommandToServer("USE " + "test"+ ";");
        String response = sendCommandToServer("SELECT name FROM marks WHERE mark LIKE 65;");
        assertFalse(response.contains("Steve"));
        assertFalse(response.contains("Pete"));
        assertFalse(response.contains("Quentin"));
        assertFalse(response.contains("Paul"));
        sendCommandToServer("DROP DATABASE test;");
    }


    @Test
    public void testSelectBasicCommand01() {
        sendCommandToServer("USE " + "test"+ ";");
        String response = sendCommandToServer("SELECT id FROM marks WHERE PASS == FALSE;");
        assertFalse(response.contains("1"));
        assertFalse(response.contains("2"));
        assertFalse(response.contains("3"));
        assertTrue(response.contains("4"));

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
        sendCommandToServer("INSERT INTO t VALUES ('Pamela', 12, FALSE);");
        String response = sendCommandToServer("SELECT * FROM t;");
        assertFalse(response.contains("4"));
        assertTrue(response.contains("5\tPamela"));
        sendCommandToServer("DROP DATABASE help;");
    }


    @Test
    public void testBracketedSelectCommand() {
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
        assertFalse(response.contains("Fran"));
        assertTrue(response.contains("Quentin"));
        assertFalse(response.contains("Pete"));
    }

    @Test
    public void testSelectCommandGTE2() {
        sendCommandToServer("USE " + "students" + ";");
        sendCommandToServer("INSERT INTO details VALUES ('Kate', null, 'brown', null);");
        String response = sendCommandToServer("SELECT * FROM details WHERE email==null;");
        assertFalse(response.contains("Steve"));
        assertFalse(response.contains("Paul"));
        assertFalse(response.contains("Fran"));
        assertFalse(response.contains("Quentin"));
        assertFalse(response.contains("Pete"));
        assertTrue(response.contains("Kate"));
    }

    @Test
    public void testSelectCommandGTE3() {
        sendCommandToServer("USE " + "students" + ";");
        String response = sendCommandToServer("SELECT * FROM details WHERE height==null;");
        assertFalse(response.contains("Steve"));
        assertFalse(response.contains("Paul"));
        assertFalse(response.contains("Fran"));
        assertFalse(response.contains("Quentin"));
        assertFalse(response.contains("Pete"));
        assertTrue(response.contains("Kate"));
    }

    @Test
    public void testSelectCommandGTE4() {
        sendCommandToServer("USE " + "students" + ";");
        String response = sendCommandToServer("SELECT * FROM details WHERE height!=null;");
        assertTrue(response.contains("Steve"));
        assertTrue(response.contains("Paul"));
        assertTrue(response.contains("Fran"));
        assertTrue(response.contains("Quentin"));
        assertTrue(response.contains("Pete"));
        assertFalse(response.contains("Kate"));
    }


    @Test
    public void testSelectCommandGTE1() {
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
        sendCommandToServer("INSERT INTO cars VALUES ('YE456H', 'toyota', 'yellow', 500, TRUE);");
        sendCommandToServer("INSERT INTO cars VALUES ('ZCEMKHU', 'toyota', 'red', 450, FALSE);");
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
    public void testUpdateJoinCommand2() {
        sendCommandToServer("USE " + "vehicles" + ";");
        sendCommandToServer("CREATE TABLE marks (name, mark, pass, iq);");
        sendCommandToServer("INSERT INTO marks VALUES ('Steve', 65, TRUE, 650);");
        sendCommandToServer("INSERT INTO marks VALUES ('Dave', 55, TRUE, 350);");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 35, FALSE, 200);");
        sendCommandToServer("INSERT INTO marks VALUES ('Clive', 20, FALSE, 130);");
        String result = sendCommandToServer("JOIN marks AND cars ON iq AND hp;");
        assertFalse(result.contains("hp"));
        assertFalse(result.contains("iq"));
        assertTrue(result.contains("toyota"));
        assertTrue(result.contains("Steve"));
        String result1 = sendCommandToServer("JOIN marks AND cars ON hp AND iq;");
        assertTrue(result1.contains(result));
    }

    @Test
    public void testUpdateJoinCommand3() {
        sendCommandToServer("USE " + "vehicles" + ";");
        String result = sendCommandToServer("JOIN marks AND cars ON CARS.HP AND marks.iq;");
        String result1 = sendCommandToServer("JOIN marks AND cars ON marks.iq AND CARS.HP;");
        assertFalse(result.contains("hp"));
        assertFalse(result.contains("iq"));
        assertTrue(result.contains("toyota"));
        assertTrue(result.contains("Steve"));
        assertTrue(result1.contains(result));
        assertTrue(result.contains(result1));
    }

    @Test
    public void testUpdateJoinCommand4() {
        sendCommandToServer("USE " + "vehicles" + ";");
        String result = sendCommandToServer("JOIN marks AND cars ON CARS.HP AND marks.IQ;");
        assertFalse(result.contains("hp"));
        assertFalse(result.contains("iq"));
        assertTrue(result.contains("toyota"));
        assertTrue(result.contains("Steve"));
    }


    @Test
    public void testUpdateJoinCommand5() {        Long ret;
        sendCommandToServer("USE " + "vehicles" + ";");
        String result = sendCommandToServer("SELECT marks.mark FROM marks;");
        assertFalse(result.contains("Steve"));
        assertFalse(result.contains("TRUE"));
        assertFalse(result.contains("200"));
    }

    @Test
    public void testUpdateJoinCommand6() {
        sendCommandToServer("USE " + "vehicles" + ";");
        String result = sendCommandToServer("SELECT * FROM marks WHERE mark>40.0;");
        assertFalse(result.contains("Bob"));
        assertFalse(result.contains("Clive"));
        assertTrue(result.contains("Steve"));
        assertTrue(result.contains("Dave"));
    }

    @Test
    public void testUpdateJoinCommand7() {
        sendCommandToServer("USE " + "vehicles" + ";");
        sendCommandToServer("ALTER TABLE cars ADD iq;");
        String result = sendCommandToServer("JOIN marks AND cars ON iq AND hp;");
        assertFalse(result.contains("hp"));
        assertFalse(result.contains("toyota"));
        assertFalse(result.contains("Steve"));

    }


    @Test
    public void testUpdateJoinCommand8() {
        sendCommandToServer("USE " + "vehicles" + ";");
        String result = sendCommandToServer("SELECT * FROM marks WHERE pass>=TRUE;");
        assertFalse(result.contains("Bob"));
        assertFalse(result.contains("Clive"));
        assertFalse(result.contains("Steve"));
        assertFalse(result.contains("Dave"));
    }


    @Test
    public void testUpdateJoinCommand9() {
        sendCommandToServer("USE " + "vehicles" + ";");
        sendCommandToServer("INSERT INTO marks VALUES ('Bob', 79, FALSE 400");
        String result = sendCommandToServer("SELECT * FROM marks WHERE name>='Bob';");
        assertFalse(result.contains("Bob"));
        assertFalse(result.contains("Clive"));
        assertFalse(result.contains("Steve"));
        assertFalse(result.contains("Dave"));
        sendCommandToServer("DROP DATABASE vehicles;");
    }


    @Test
    public void testCaseInsensitive1() {
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
        assertTrue(response1.contains(response));
        assertTrue(response.contains(response1));
        assertTrue(response.contains(response2));
        sendCommandToServer("DROP TABLE POINT;");
    }


    @Test
    public void testTranscript1() {
        String result=sendCommandToServer("CREATE DATABASE  transcript;");
        result=sendCommandToServer("USE transcript;");
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
    }

    @Test
    public void testTranscript2() {
        sendCommandToServer("USE transcript;");
        String result=sendCommandToServer("SELECT * FROM marks WHERE name != 'Dave';");
        assertFalse(result.contains("Dave"));
        assertTrue(result.contains("Steve"));
        assertTrue(result.contains("Bob"));
        assertTrue(result.contains("Clive"));
    }

    @Test
    public void testTranscript3() {
        sendCommandToServer("USE transcript;");
        String result=sendCommandToServer("SELECT * FROM marks WHERE pass == TRUE;");
        assertTrue(result.contains("Dave"));
        assertTrue(result.contains("Steve"));
        assertFalse(result.contains("Bob"));
        assertFalse(result.contains("Clive"));

    }

    @Test
    public void testTranscript4() {
        sendCommandToServer("USE transcript;");
        String result=sendCommandToServer("CREATE TABLE coursework (task, submission);");
        assertEquals("[OK]\n", result);
        result = sendCommandToServer("INSERT INTO coursework VALUES ('OXO', 3);");
        assertEquals("[OK]\n", result);
        result = sendCommandToServer("INSERT INTO coursework VALUES ('db', 1);");
        assertEquals("[OK]\n", result);
        result = sendCommandToServer("INSERT INTO coursework VALUES ('OXO', 4);");
        assertEquals("[OK]\n", result);
        result = sendCommandToServer("INSERT INTO coursework VALUES ('STAG', 2);");
        assertEquals("[OK]\n", result);
        result = sendCommandToServer("SELECT * FROM coursework;");
        result = sendCommandToServer("SELECT * FROM marks;");
        result = sendCommandToServer("JOIN coursework AND marks ON coursework.submission AND marks.id;");
        sendCommandToServer("DROP DATABASE transcript;");
    }

}