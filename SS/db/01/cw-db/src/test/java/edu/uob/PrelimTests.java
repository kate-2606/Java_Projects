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
        setup();
        Tokenizer tok = new Tokenizer();
        tok.tokenizer(" INSERT USE DELETE UPDATE (A=0)");
        assertEquals("INSERT", tok.getToken(0));
        assertEquals("USE", tok.getToken(1));
        assertEquals("DELETE", tok.getToken(2));
        assertEquals("(", tok.getToken(4));
        /*
        assertEquals("A", tok.getToken(5));
        assertEquals("=", tok.getToken(6));
        assertEquals("0 ", tok.getToken(7));

         */
        Token testToken = new Token();
        testToken.setTokenType(0);
        String typeString = testToken.tokenTypeToString();

        assertEquals("USE", typeString);
    }

}
