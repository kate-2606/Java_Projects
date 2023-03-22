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
    }

    @Test
    public void testExistingAttributes2() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        String result = sendCommandToServer("CREATE TABLE people (name, email, hairColour, height, name);");
        assertTrue(result.contains("Attribute name 'name' already exists in this table"));
        sendCommandToServer("DROP DATABASE humans;");
    }


    @Test
    public void testExistingAttributes3() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        String result = sendCommandToServer("CREATE TABLE people (name, email, hairColour, height, ID);");
        assertTrue(result.contains("Attribute name 'id' already exists in this table"));
        sendCommandToServer("DROP DATABASE humans;");
    }


    @Test
    public void testInvalidInsert1() {
        String randomName=generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName +";");
        sendCommandToServer("USE "+randomName+";");
        sendCommandToServer("CREATE TABLE fruit;");
        String response = sendCommandToServer("INSERT INTO fruit VALUES('pineapple', 'apple', 'apple', 'pepper');");
        assertTrue(response.contains("[ERROR]"));
    }


    @Test
    public void testInvalidInsert2() {
        String randomName=generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName +";");
        sendCommandToServer("USE "+randomName+";");
        sendCommandToServer("CREATE TABLE fruit (yellow, green, purple);");
        String response = sendCommandToServer("INSERT INTO fruit VALUES('pineapple', 'apple', 'apple', 'pepper');");
        assertTrue(response.contains("[ERROR]"));
    }


    @Test
    public void testInvalidInsert3() {
        String randomName=generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName +";");
        sendCommandToServer("USE "+randomName+";");
        sendCommandToServer("CREATE TABLE fruit (yellow, green, purple, blue);");
        String response = sendCommandToServer("INSERT INTO fruit VALUES('pineapple', 'apple', 'apple');");
        assertTrue(response.contains("[ERROR]"));
    }


    @Test
    public void testInvalidName1() {
        String response = sendCommandToServer("CREATE DATABASE null;");
        assertTrue(response.contains("[ERROR]"));
    }


    @Test
    public void testInvalidName2() {
        String randomName=generateRandomName();
        sendCommandToServer("CREATE DATABASE add;");
        String response = sendCommandToServer("USE add;");
        assertTrue(response.contains("[ERROR]"));
    }


    @Test
    public void testInvalidName3() {
        String randomName=generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName+";");
        sendCommandToServer("USE " + randomName +";");
        String response = sendCommandToServer("CREATE TABLE true (yellow, green, purple, blue);");
        assertTrue(response.contains("CREATE syntax failed"));
    }



    @Test
    public void testInvalidName4() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        String response = sendCommandToServer("CREATE TABLE false (yellow, green, purple, blue);");
        assertTrue(response.contains("CREATE syntax failed"));
        System.out.println(response);
    }

    @Test
    public void testInvalidName5() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        String response = sendCommandToServer("CREATE TABLE colours (colours.select, green, purple, blue);");
        assertTrue(response.contains("[ERROR]"));
        System.out.println(response);
    }



    @Test
    public void testInvalidName6() {
        String randomName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + randomName + ";");
        sendCommandToServer("USE " + randomName + ";");
        String response = sendCommandToServer("CREATE TABLE paint (like, green, purple, blue);");
        assertTrue(response.contains("[ERROR]"));
        System.out.println(response);
    }




}
