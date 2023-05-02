package edu.uob;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExampleSTAGTests {

  private GameServer server;

  // Create a new server _before_ every @Test
  @BeforeEach
  void setup() throws IOException, ParseException, ParserConfigurationException, SAXException {
      File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  String sendCommandToServer(String command) {
      // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
      return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
      "Server took too long to respond (probably stuck in an infinite loop)");
  }

  // A lot of tests will probably check the game state using 'look' - so we better make sure 'look' works well !
  @Test
  void testLook() {
    String response = sendCommandToServer("simon: look");
    response = response.toLowerCase();
    assertTrue(response.contains("cabin"), "Did not see the name of the current room in response to look");
    assertTrue(response.contains("log cabin"), "Did not see a description of the room in response to look");
    assertTrue(response.contains("magic potion"), "Did not see a description of artifacts in response to look");
    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
    assertTrue(response.contains("forest"), "Did not see available paths in response to look");
  }

  // Test that we can pick something up and that it appears in our inventory
  @Test
  void testGet()
  {
      String response;
      sendCommandToServer("simon: get potion");
      response = sendCommandToServer("simon: inv");
      response = response.toLowerCase();
      assertTrue(response.contains("potion"), "Did not see the potion in the inventory after an attempt was made to get it");
      response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertFalse(response.contains("potion"), "Potion is still present in the room after an attempt was made to get it");
  }

  // Test that we can goto a different location (we won't get very far if we can't move around the game !)
  @Test
  void testGoto()
  {
      sendCommandToServer("simon: goto forest");
      String response = sendCommandToServer("simon: look");
      response = response.toLowerCase();
      assertTrue(response.contains("key"), "Failed attempt to use 'goto' command to move to the forest - there is no key in the current location");
  }



    @Test
    void testReincarnate()
    {
        String response = sendCommandToServer("simon: look");
        assertTrue(response.contains("axe"));
        assertTrue(response.contains("coin"));
        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: get axe");
        response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("axe"));
        assertTrue(response.contains("coin"));
        sendCommandToServer("simon: look");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: look");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: look");
        sendCommandToServer("simon: unlock trapdoor with key");
        sendCommandToServer("simon: goto cellar");
        sendCommandToServer("simon: look");
        response = sendCommandToServer("simon: fight elf");
        assertTrue(response.contains("You attack the elf, but he fights back and you lose some health"));
        response = sendCommandToServer("simon: fight elf");
        assertTrue(response.contains("You attack the elf, but he fights back and you lose some health"));
        response = sendCommandToServer("simon: fight elf");
        assertTrue(response.contains("You attack the elf, but he fights back and you lose some health"));
        response = sendCommandToServer("simon: fight elf");
        assertTrue(response.contains("You attack the elf, but he fights back and you lose some health"));
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("simon"));
        assertFalse(response.contains("axe"));
        assertFalse(response.contains("coin"));
        response = sendCommandToServer("sion: look");
        assertTrue(response.contains("simon"));
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("sion"));
        sendCommandToServer("simon: goto cellar");
        response = sendCommandToServer("simon: look");
        assertFalse(response.contains("sion"));
        assertTrue(response.contains("axe"));
        assertTrue(response.contains("coin"));
    }

    @Test
    void testNoStealing1() {

        sendCommandToServer("simon: get coin");
        sendCommandToServer("simon: get axe");
        String response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("axe"));
        assertTrue(response.contains("coin"));
        response = sendCommandToServer("sion: inv");
        assertTrue(response.contains("There is nothing in your inventory."));
    }

    @Test
    void testNoStealing2() {

        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: goto riverbank");
        sendCommandToServer("sion: goto forest");
        sendCommandToServer("sion: goto riverbank");
        sendCommandToServer("sion: get horn");
        String response = sendCommandToServer("sion: inv");
        assertTrue(response.contains("horn"));
        assertFalse(response.contains("coin"));
        response = sendCommandToServer("simon: inv");
        assertFalse(response.contains("horn"));
        assertTrue(response.contains("There is nothing in your inventory."));
        response = sendCommandToServer("simon: blow horn");
        assertFalse(response.contains("lumberjack"));
    }

    @Test
    void testNoSGettingOfPlayers() {

        sendCommandToServer("simon: look");
        sendCommandToServer("sion: look");
        sendCommandToServer("simon: get sion");
        String response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("There is nothing in your inventory."));
    }

    @Test
    void testNoSGettingOfCharacters() {

        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: open with key");
        sendCommandToServer("sion: goto cellar");
        sendCommandToServer("sion: get elf");
        String response = sendCommandToServer("sion: inv");
        assertTrue(response.contains("There is nothing in your inventory."));
        assertFalse(response.contains("An angry looking Elf"));
    }

    @Test
    void testNoSGettingOfFurniture1() {

        sendCommandToServer("simon: get trapdoor");
        String response = sendCommandToServer("simon: inv");
        assertTrue(response.contains("There is nothing in your inventory."));
    }


    @Test
    void testNoSGettingOfFurniture2() {
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        sendCommandToServer("simon: goto cabin");
        sendCommandToServer("simon: open with key");
        String response = sendCommandToServer("sion: inv");
        assertTrue(response.contains("There is nothing in your inventory."));
    }

    @Test
    void testIndependentPlayerMovement1() {
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: get key");
        String response = sendCommandToServer("sion: look");
        assertTrue(response.contains("axe"));
        assertTrue(response.contains("coin"));
        assertTrue(response.contains("trapdoor"));
        assertFalse(response.contains("simon"));
        sendCommandToServer("simon: goto cabin");
        response = sendCommandToServer("simon: look");
        assertTrue(response.contains("sion"));
    }

    @Test
    void testCaseInsensitive1() {
        String response = sendCommandToServer("simon: LOOK");
        assertTrue(response.contains("axe"));
        assertTrue(response.contains("coin"));
        assertTrue(response.contains("trapdoor"));
        assertFalse(response.contains("simon"));

    }

    @Test
    void testExtraLetters() {
        String response = sendCommandToServer("simon: LOOKs");
        assertFalse(response.contains("axe"));
        assertFalse(response.contains("coin"));
        assertFalse(response.contains("trapdoor"));
    }

    @Test
    void testInvalidLocation() {
        String response = sendCommandToServer("simon: goto riverbank");
        System.out.println(response);
        assertTrue(response.contains("You cannot access the riverbank from the cabin"));
    }

    @Test
    void testInvalidCommand1() {
        sendCommandToServer("simon: get axe");
        sendCommandToServer("simon: goto forest");
        sendCommandToServer("simon: chop tree");
        sendCommandToServer("simon: get log");
        sendCommandToServer("simon: goto riverbank");
        sendCommandToServer("simon: bridge log");
        sendCommandToServer("simon: goto clearing");
        sendCommandToServer("sion: get coin");
        sendCommandToServer("sion: goto forest");
        sendCommandToServer("sion: get key");
        sendCommandToServer("sion: goto cabin");
        sendCommandToServer("sion: unlock trapdoor");
    }



  // Add more unit tests or integration tests here.

}
