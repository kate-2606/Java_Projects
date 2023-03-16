package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class InterpreterTests {
    private DBServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    String storageFolderPath = Paths.get("databases").toAbsolutePath().toString();

    String testsFolderPath = storageFolderPath + File.separator + "Tests";

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

    @Test
    public void testBasicCreateAndCommand() {
        sendCommandToServer("CREATE DATABASE coursework;");
        sendCommandToServer("CREATE TABLE java (assignment, bugs, numOfFunction, enjoyable);");
        //sendCommandToServer("INSERT INTO java VALUES ('OXO', 0, 12, TRUE);");
        //sendCommandToServer("INSERT INTO java VALUES ('OXO', 57, 8000, TRUE);");
        String response = sendCommandToServer("SELECT * FROM marks;");
        assertTrue(response.contains("[OK]"), "A valid Command was made, however an [OK] tag was not returned");
    }

    @Test
    private void testReadTableFile() throws IOException {
        sendCommandToServer("CREATE DATABASE coursework;");
        sendCommandToServer("CREATE TABLE java (assignment, bugs, numOfFunction, enjoyable);");
        //exists isn't working in testing? -- passes when true or false....
        File testDatabase = new File(testsFolderPath+File.separator+"coursework");
        assertTrue(testDatabase.exists());
    }
}