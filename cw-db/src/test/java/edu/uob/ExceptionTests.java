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

public class ExceptionTests {
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

    @Test
    public void testExistingAttributes1() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        String result = sendCommandToServer("CREATE TABLE people (name, email, hairColour, height, Name);");
        assertTrue(result.contains("Attribute name 'name' already exists in this table"));
        sendCommandToServer("DROP DATABASE humans;");
        assertTrue(result.contains("Attribute name 'name' already exists in this table"));
    }

    @Test
    public void testExistingAttributes2() {
        sendCommandToServer("CREATE DATABASE food;");
        sendCommandToServer("USE food;");
        sendCommandToServer("CREATE TABLE fruit (yellow, green, red);");
        String response = sendCommandToServer("INSERT INTO fruit VALUES('pineapple', 'apple', 'apple', 'pepper');");
        assertTrue(response.contains("The number of values does not math the number of attributes in the table"));
        response = sendCommandToServer("INSERT INTO fruit VALUES('pineapple', 'apple');");
        assertTrue(response.contains("The number of values does not math the number of attributes in the table"));
    }


    @Test
    public void testInvalidName1() {
        String response = sendCommandToServer("CREATE DATABASE null;");
        assertTrue(response.contains("[ERROR]"));
    }


    @Test
    public void testInvalidName2() {
        String randomName=generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName +";");
        String response = sendCommandToServer("USE add;");
        assertTrue(response.contains("[ERROR]"));
    }

    @Test
    public void testInvalidName3() {
        String randomName=generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName +";");
        sendCommandToServer("USE "+randomName+";");
        String response = sendCommandToServer("INSERT INTO fruit VALUES('pineapple.true', 'apple', 'apple', 'pepper');");
        assertTrue(response.contains("[ERROR]"));
    }
}
