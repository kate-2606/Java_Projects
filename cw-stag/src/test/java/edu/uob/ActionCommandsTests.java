package edu.uob;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ActionCommandsTests {


    // Test to make sure that the basic actions file is readable


    @BeforeEach
    void setUpParser() throws IOException, ParserConfigurationException, SAXException, ParseException {
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        library = new ActionLibrary();
        testMap = new GameMap();

        GameActionsParser parser = new GameActionsParser(actionsFile, library, testMap);
        GameEntitiesParser entitiesParser = new GameEntitiesParser(entitiesFile, testMap);
        tester = new GameCharacter("tester", "strict tester", testMap.getStartLocation());
        interpreter = new CommandInterpreter(testMap, tester, library);
    }

    GameMap testMap;

    GameCharacter tester;

    ActionLibrary library;

    CommandInterpreter interpreter;

    /*
    @Test
    void testBasicActions1() {
        String response  = interpreter.handleCommand("look");
        assertTrue(response.contains("A log cabin in the woods"));
        assertTrue(response.contains("Magic potion"));
        assertTrue(response.contains("Wooden trapdoor"));
        assertTrue(response.contains("forest"));
        assertFalse(response.contains("Brass key"));
    }

    //basic get test
    @Test
    void testBasicActions2() {
        interpreter.handleCommand("get potion");
        String response  = interpreter.handleCommand("look");
        assertFalse(response.contains("Magic potion"));
        response  = interpreter.handleCommand("inv");
        assertTrue(response.contains("Magic potion"));
    }

    //basic goto test
    @Test
    void testBasicActions3() {
        interpreter.handleCommand("goto forest");
        String response  = interpreter.handleCommand("look");
        assertTrue(response.contains("A dark forest"));
        assertFalse(response.contains("Magic potion"));
        assertTrue(response.contains("A big tree"));
        assertFalse(response.contains("cellar"));
        assertTrue(response.contains("cabin"));
        assertTrue(response.contains("Brass key"));
    }

     */

    @Test
    void testBasicActions4() {
        interpreter.handleCommand("goto forest");
        interpreter.handleCommand("get key");
        interpreter.handleCommand("goto cabin");
        assertTrue(library.matchingTrigger("unlock"));
        interpreter.handleCommand("unlock trapdoor with key");
        interpreter.handleCommand("goto cellar");
        String response = interpreter.handleCommand("look");
        System.out.println(testMap.getCurrentLocation().getName());
        assertTrue(response.contains("Angry elf"));
    }
}
