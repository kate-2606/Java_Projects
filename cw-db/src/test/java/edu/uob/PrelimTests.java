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
        String fileName = "Tests/people.tab";
        Table tesTable = server.readFile(fileName);

        assertEquals(tesTable.getTableName(), "people");
        assertEquals(tesTable.getNumberOfAttributes(), 4);
        assertEquals(tesTable.getNumberOfDataRows(), 3);
        assertEquals(tesTable.getCellDataByNumber(2, 2), "42");
        assertEquals(tesTable.getCellDataByNumber(0, 1), "Bob");

    }
}
