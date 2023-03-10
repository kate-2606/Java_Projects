package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

public class PrelimTests {
    private DBServer server;

    // Create a new server _before_ every @Test
    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    @Test
    public void firstTest() throws FileNotFoundException {
        String fileName = "people.tab";
        System.out.println(server.readFile(fileName).get(0));
        System.out.println(server.readFile(fileName).get(1));
        System.out.println(server.readFile(fileName).get(2));
        System.out.println(server.readFile(fileName).get(3));
    }
}
