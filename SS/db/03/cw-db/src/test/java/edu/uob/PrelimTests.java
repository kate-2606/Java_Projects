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
    public void testParser(){
        Parser testParser = new Parser();
        testParser.main(null);

    }


}
