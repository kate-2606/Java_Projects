package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.io.FileNotFoundException;

public class PrelimTests {
    private DBServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    @Test
    public void testFileReading() throws FileNotFoundException {
        setup();
        String fileName = "Tests/people.tab";
        Table tesTable = server.readFile(fileName);

        assertEquals(tesTable.getTableName(), "people");
        assertEquals(4, tesTable.getNumberOfAttributes());
        assertEquals(3, tesTable.getNumberOfDataRows());
        assertEquals("42", tesTable.getCellDataByNumber(2, 2));
        assertEquals("Bob", tesTable.getCellDataByNumber(1, 0));


        String fileLocation = "/home/kate/CS_Java/Java_CW/cw-db/databases/Tests/Out/people.txt";
        server.exportTable(fileLocation, tesTable);

    }

    @Test
    public void testTokenizer() {

        LexAnalyser test = new LexAnalyser();
        test.setCommand(" INSERT USE DELETE UPDATE 'Test' AND (A=0)");
        test.setup();
        assertEquals("INSERT", test.getWord(0));
        assertEquals("USE", test.getWord(1));
        assertEquals("DELETE", test.getWord(2));

        assertEquals("'Test'", test.getWord(4));
        assertEquals("AND", test.getWord(5));
        assertEquals("(", test.getWord(6));

        Token testToken = new Token();
        testToken.setType(0);
        assertEquals("USE", testToken.typeToString());

        test.setCommand("CREATE TABLE marks (name, mark, pass);");
        test.setup();
        assertEquals("CREATE",test.getNextToken().typeToString());
        assertEquals("TABLE", test.getNextToken().typeToString());
        assertEquals("PLAIN_TXT", test.getNextToken().typeToString());
        assertEquals("OPEN_BR", test.getNextToken().typeToString());



    }

    @Test
    public void testLexicalAnalyser(){
        /*
        LexicalAnalyser test = new LexicalAnalyser();
        test.setQuery(" INSERT USE DELETE UPDATE 'Test' AND (A=0)");
        test.setup();

         */
        LexAnalyser test = new LexAnalyser();
        test.setCommand("CREATE 'Steve' TRUE +2.0 -76 plaintext * NULL");
        test.setup();
        assertEquals("CREATE", test.getNextToken().typeToString());
        assertEquals("STRING_LIT", test.getNextToken().typeToString());
        assertEquals("BOOL_LIT", test.getNextToken().typeToString());
        assertEquals("FLOAT_LIT", test.getNextToken().typeToString());
        assertEquals("INT_LIT", test.getNextToken().typeToString());
        assertEquals("PLAIN_TXT", test.getNextToken().typeToString());
        assertEquals("WILD_CRD", test.getNextToken().typeToString());
        assertEquals("NULL", test.getNextToken().typeToString());

    }

    @Test
    public void testParserUse(){
        Parser testParser = new Parser();
        testParser.Parser("USE markbook;");
        assertTrue(testParser.getParseResult());
    }

    @Test
    public void testParserCreate1(){
        Parser testParser = new Parser();
        testParser.Parser("CREATE DATABASE markbook;");
        assertTrue(testParser.getParseResult());
    }

    @Test
    public void testParserCreate2(){
        Parser testParser = new Parser();
        testParser.Parser("CREATE TABLE marks (name, mark, pass);");
        assertTrue(testParser.getParseResult());

        testParser.Parser("CREATE TABLE marks(name, mark, pass);");
        System.out.println(testParser.lex.getWord(2));
        assertTrue(testParser.getParseResult());

        testParser.Parser("CREATE TABLE marks (name,mark,pass);");
        System.out.println(testParser.lex.getWord(2));
        assertTrue(testParser.getParseResult());

        testParser.Parser("CREATE TABLEmarks (name,mark,pass);");
        System.out.println(testParser.lex.getWord(2));
        assertFalse(testParser.getParseResult());
    }

    @Test
    public void testParserDrop() {
        Parser testParser = new Parser();
        testParser.Parser("DROP databasename;");
        assertTrue(testParser.getParseResult());

        testParser.Parser("DROP TABLE databasename;");
        assertFalse(testParser.getParseResult());
    }

    @Test
    public void testParserAlter() {
        Parser testParser = new Parser();
        testParser.Parser("ALTER TABLE marks ADD perfection;");
        assertTrue(testParser.getParseResult());

        testParser.Parser("ALTER TABLE marks CREATE perfection;");
        assertFalse(testParser.getParseResult());
    }

    @Test
    public void testParserInsert() {
        Parser testParser = new Parser();
        testParser.Parser("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        assertTrue(testParser.getParseResult());

        testParser.Parser("INSERT INTO marks VALUES ('Dave', 55, TRUE);");
        assertTrue(testParser.getParseResult());
    }

    @Test
    public void testParserSelect() {
        Parser testParser = new Parser();
        testParser.Parser("SELECT * FROM marks;");
        assertTrue(testParser.getParseResult());

        testParser.Parser("SELECT * FROM marks WHERE (pass == FALSE) AND (mark > 35);");
        assertTrue(testParser.getParseResult());

        testParser.Parser("SELECT * FROM marks WHERE pass == FALSE AND mark > 35;");
        assertTrue(testParser.getParseResult());

        testParser.Parser("SELECT * FROM marks WHERE (pass == FALSE AND mark > 35);");
        assertTrue(testParser.getParseResult());

        testParser.Parser("SELECT * FROM marks WHERE ((pass == FALSE) AND (mark > 35));");
        assertTrue(testParser.getParseResult());

        testParser.Parser("SELECT * FROM marks WHERE (pass == FALSE) AND mark > 35;");
        assertTrue(testParser.getParseResult());

        testParser.Parser("SELECT * FROM marks WHERE (pass == FALSE) AND mark LIKE 35;");
        assertTrue(testParser.getParseResult());

        testParser.Parser("SELECT * FROM marks WHERE (pass == FALSE) ADD mark LIKE 35;");
        assertFalse(testParser.getParseResult());
    }

    @Test
    public void testParserUpdate() {
        Parser testParser = new Parser();
        testParser.Parser("UPDATE marks SET grade = 'rubbish', extraWork = 'lots' WHERE student == 'lazy';");
        assertTrue(testParser.getParseResult());


        testParser.Parser("UPDATE marks SET grade = 'rubbish', extraWork.amount = 'lots' WHERE student == 'lazy';");
        assertTrue(testParser.getParseResult());

    }


    @Test
    public void testParserDelete() {
        Parser testParser = new Parser();
        testParser.Parser("DELETE FROM marks WHERE name == 'Dave';");
        assertTrue(testParser.getParseResult());


        testParser.Parser("DELETE FROM marks WHERE name = 'Dave';");
        assertFalse(testParser.getParseResult());
    }


    @Test
    public void testParserJoin() {
        Parser testParser = new Parser();
        testParser.Parser("JOIN coursework AND marks ON submission AND id;");
        assertTrue(testParser.getParseResult());


        testParser.Parser("JOIN coursework AND marks ON submission.ref AND id;");
        assertTrue(testParser.getParseResult());
    }

}
