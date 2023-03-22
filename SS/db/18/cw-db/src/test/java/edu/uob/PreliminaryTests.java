package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;


// Test lots of floats ---------------------------------

public class PreliminaryTests {
    private DBServer server;

    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    InterpContext ic = new InterpContext();

    String storageFolderPath = Paths.get("databases").toAbsolutePath().toString();


    private Parser createNewParser(String command){
        ArrayList<Token> tokens = new ArrayList<>();
        ic.InterpContext(storageFolderPath);
        Lexer testLexer = new Lexer();
        testLexer.Lexer(command, tokens, ic);
        Parser testParser = new Parser();
        testParser.Parser(tokens, testLexer, ic);
        return testParser;
    }



    @Test
    public void testTokenizer() throws IOException {
        ArrayList<Token> tokens = new ArrayList<>();
        Lexer test = new Lexer();
        test.Lexer(" INSERT USE DELETE UPDATE 'Test' AND (A=0)", tokens, ic);
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
    public void testLexicalAnalyser1() throws IOException{
        ArrayList<Token> tokens = new ArrayList<>();
        Lexer test = new Lexer();
        test.Lexer("CREATE 'Steve' TRUE +2.0 -76 plaintext * NULL", tokens, ic);
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
    public void testLexicalAnalyser2() throws IOException{
        ArrayList<Token> tokens = new ArrayList<>();
        Lexer test = new Lexer();
        test.Lexer("+2..0 -178 +968796 +0.8 98.7.546 true false", tokens, ic);
        assertEquals(null, test.getNextToken());
        assertEquals("INT_LIT", test.getNextToken().typeToString());
        assertEquals("INT_LIT", test.getNextToken().typeToString());
        assertEquals("FLOAT_LIT", test.getNextToken().typeToString());
        assertEquals(null, test.getNextToken());
        assertEquals("BOOL_LIT", test.getNextToken().typeToString());
        assertEquals("BOOL_LIT", test.getNextToken().typeToString());
    }

    @Test
    public void testParserUse(){
        Parser testParser = createNewParser("USE markbook;");
        assertTrue(testParser.getParserResult());
    }

    @Test
    public void testParserCreate1(){
        Parser testParser = createNewParser("CREATE DATABASE markbook;");
        assertTrue(testParser.getParserResult());
    }


    @Test
    public void testParserCreate2() {
        Parser testParser = createNewParser("CREATE TABLE marks (name, mark, pass);");
        assertTrue(testParser.getParserResult());
    }


    @Test
    public void testParserCreate3() {
        Parser testParser1 = createNewParser("CREATE TABLE marks(name, mark, pass);");
        assertTrue(testParser1.getParserResult());
    }


    @Test
    public void testParserCreate4() {
        Parser testParser2 = createNewParser("CREATE TABLE marks (name1,mark,pass);");
        assertTrue(testParser2.getParserResult());
    }


    @Test
    public void testParserCreate5() {
        Parser testParser3 = createNewParser("CREATE TABLEmarks (name,mark,pass);");
        assertFalse(testParser3.getParserResult());
    }


    @Test
    public void testParserCreate6() {
        Parser testParser4 = createNewParser("CREATE TABLE firstTest (column1, column2, column3);");
        assertTrue(testParser4.getParserResult());
    }


    @Test
    public void testParserCreate7(){
        Parser testParser5 = createNewParser("CREATE TABLE firstTest (1, 2, 3);");
        assertTrue(testParser5.getParserResult());
    }

    @Test
    public void testParserDrop1() {
        Parser testParser = createNewParser("DROP databasename4527;");
        assertFalse(testParser.getParserResult());
    }


    @Test
    public void testParserDrop2() {
        Parser testParser1 = createNewParser("DROP TABLE databasename;");
        assertTrue(testParser1.getParserResult());
    }


    @Test
    public void testParserDrop3() {
        Parser testParser2 = createNewParser("DROP TABLE databasename;;");
        assertFalse(testParser2.getParserResult());
    }

    @Test
    public void testParserAlter1() {
        Parser testParser = createNewParser("ALTER TABLE marks ADD perfection;");
        assertTrue(testParser.getParserResult());
    }


    @Test
    public void testParserAlter2() {
        Parser testParser1 = createNewParser("ALTER TABLE marks2 CREATE perfection;");
        assertFalse(testParser1.getParserResult());
    }

    @Test
    public void testParserInsert1() {
        Parser testParser = createNewParser("INSERT INTO marks VALUES ('Steve', 65, TRUE);");
        assertTrue(testParser.getParserResult());
    }

    @Test
    public void testParserInsert2() {
        Parser testParser1 = createNewParser("INSERT INTO marks VALUES ('Dave', 55, TRUE);");
        assertTrue(testParser1.getParserResult());
    }

    @Test
    public void testParserSelect1() {
        Parser testParser = createNewParser("SELECT * FROM marks;");
        assertTrue(testParser.getParserResult());
    }

    @Test
    public void testParserSelect2() {
        Parser testParser1 = createNewParser("SELECT * FROM marks WHERE (pass == FALSE) AND (mark > 35);");
        assertTrue(testParser1.getParserResult());
    }

    @Test
    public void testParserSelect3() {
        Parser testParser2 = createNewParser("SELECT * FROM marks WHERE pass == FALSE AND mark > 35;");
        assertTrue(testParser2.getParserResult());
    }

    @Test
    public void testParserSelect4() {
        Parser testParser3 = createNewParser("SELECT * FROM marks WHERE (pass == FALSE AND mark > 35);");
        assertTrue(testParser3.getParserResult());
    }

    @Test
    public void testParserSelect5() {
        Parser testParser4 = createNewParser("SELECT * FROM marks WHERE ((pass == FALSE) AND (mark > 35));");
        assertTrue(testParser4.getParserResult());
    }

    @Test
    public void testParserSelect6() {
        Parser testParser5 = createNewParser("SELECT * FROM marks WHERE (pass == FALSE) AND mark > 35;");
        assertTrue(testParser5.getParserResult());
    }

    @Test
    public void testParserSelect7() {
        Parser testParser6 = createNewParser("SELECT * FROM marks WHERE (pass == FALSE)) AND mark > 35;");
        assertFalse(testParser6.getParserResult());
    }

    @Test
    public void testParserSelect8() {
        Parser testParser7 = createNewParser("SELECT * FROM marks WHERE (pass == FALSE) AND markLIKE 35;");
        assertFalse(testParser7.getParserResult());
    }

    @Test
    public void testParserSelect9() {
        Parser testParser8 = createNewParser("SELECT * FROM marks WHERE (pass == FALSE) ADD mark LIKE 35;");
        assertFalse(testParser8.getParserResult());
    }

    @Test
    public void testParserSelect10() {
        Parser testParser9 = createNewParser("SELECT * FROM marks WHERE (pass = FALSE) AND (mark > 35);");
        assertFalse(testParser9.getParserResult());
    }

    @Test
    public void testParserUpdate1() {
        Parser testParser = createNewParser("UPDATE marks SET grade = 'rubbish', extraWork = 'lots' WHERE student == 'lazy';");
        assertTrue(testParser.getParserResult());
    }

    @Test
    public void testParserUpdate2() {
        Parser testParser1 = createNewParser("UPDATE marks SET grade = 'rubbish', extraWork.amount = 'lots' WHERE student == 'lazy';");
        assertTrue(testParser1.getParserResult());
    }

    @Test
    public void testParserUpdate3() {
        Parser testParser2 = createNewParser("UPDATE marks SET grade = 'rubbish', extraWork.amount = 'lots' WHERE student.name LIKE 'lazy';");
        assertTrue(testParser2.getParserResult());
    }

    @Test
    public void testParserUpdate4() {
        Parser testParser3 = createNewParser("UPDATE cars SET brand='itallianFail' WHERE hp<450;");
        assertTrue(testParser3.getParserResult());
    }

    @Test
    public void testParserUpdate5() {
        Parser testParser3 = createNewParser("UPDATE cars SET brand=itallianFail WHERE hp<450;");
        assertFalse(testParser3.getParserResult());
    }


    @Test
    public void testParserDelete1() {
        Parser testParser = createNewParser("DELETE FROM marks WHERE name == 'Dave';");
        assertTrue(testParser.getParserResult());
    }

    @Test
    public void testParserDelete2() {
        Parser testParser1 = createNewParser("DELETE FROM marks WHERE name = 'Dave';");
        assertFalse(testParser1.getParserResult());
    }


    @Test
    public void testParserJoin1() {
        Parser testParser = createNewParser("JOIN coursework AND marks ON submission AND id;");
        assertTrue(testParser.getParserResult());
    }

    @Test
    public void testParserJoin2() {
        Parser testParser1 = createNewParser("JOIN coursework AND marks ON submission.ref AND id;");
        assertTrue(testParser1.getParserResult());
    }

    @Test
    public void testParserNull() {
        Parser testParser1 = createNewParser("CREATE DATABASE null;");
        assertFalse(testParser1.getParserResult());
    }


}
