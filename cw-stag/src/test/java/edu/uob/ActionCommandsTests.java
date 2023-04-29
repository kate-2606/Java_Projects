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

    @BeforeEach
    void setUpParser() throws IOException, ParserConfigurationException, SAXException, ParseException {
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        library = new ActionLibrary();
        testMap = new GameMap();

        GameEntitiesParser entitiesParser = new GameEntitiesParser(entitiesFile, testMap);
        GameActionsParser parser = new GameActionsParser(actionsFile, library, testMap);
        tester = new GameCharacter("tester", "strict tester", testMap.getStartLocation());
        interpreter = new CommandInterpreter(testMap, tester, library);
    }


    GameMap testMap;

    GameCharacter tester;

    ActionLibrary library;

    CommandInterpreter interpreter;


    @Test
    void testBasicActions1() {
        String response  = interpreter.handleCommand("look");
        assertTrue(response.contains("A log cabin in the woods"));
        assertTrue(response.contains("A bottle of magic potion"));
        assertTrue(response.contains("A razor sharp axe"));
        assertTrue(response.contains("A silver coin"));
        assertTrue(response.contains("A locked wooden trapdoor in the floor"));
        assertTrue(response.contains("forest"));
        assertFalse(response.contains("Brass key"));
    }

    //basic get test
    @Test
    void testBasicActions2() {
        interpreter.handleCommand("get potion");
        String response  = interpreter.handleCommand("look");
        assertFalse(response.contains("A bottle of magic potion"));
        response  = interpreter.handleCommand("inv");
        assertTrue(response.contains("A bottle of magic potion"));
    }

    //basic goto test
    @Test
    void testBasicActions3() {
        interpreter.handleCommand("goto forest");
        String response  = interpreter.handleCommand("look");
        assertTrue(response.contains("A deep dark forest"));
        assertFalse(response.contains("A bottle of magic potion"));
        assertTrue(response.contains("A tall pine tree"));
        assertFalse(response.contains("cellar"));
        assertTrue(response.contains("cabin"));
        assertTrue(response.contains("A rusty old key"));
    }

    @Test
    void testBasicActions4() {
        GameAction action = library.getActions("unlock").iterator().next();
        interpreter.handleCommand("goto forest");
        interpreter.handleCommand("get key");
        interpreter.handleCommand("goto cabin");
        assertTrue(library.matchingTrigger("unlock"));
        interpreter.handleCommand("unlock trapdoor with key");
        interpreter.handleCommand("goto cellar");
        String response = interpreter.handleCommand("look");
        assertTrue(response.contains("An angry looking Elf"));
    }

    @Test
    void testBasicActions5() {
        interpreter.handleCommand("get axe");
        interpreter.handleCommand("goto forest");
        assertNotNull(testMap.getCurrentLocation().getEntity("tree"));
        interpreter.handleCommand("look");
        interpreter.handleCommand("chop tree with axe");
        String response = interpreter.handleCommand("look");
        assertTrue(response.contains("A heavy wooden log"));
    }

    @Test
    void testBasicActions6() {
        interpreter.handleCommand("goto forest");
        interpreter.handleCommand("goto riverbank");
        assertNotNull(testMap.getCurrentLocation().getEntity("horn"));
        assertNotNull(testMap.getCurrentLocation().getEntity("river"));
        interpreter.handleCommand("blow horn");
        assertNotNull(testMap.getCurrentLocation().getEntity("horn"));
        assertNotNull(testMap.getCurrentLocation().getEntity("lumberjack"));
    }

    @Test
    void testBasicActions7() {
        interpreter.handleCommand("goto forest");
        interpreter.handleCommand("goto riverbank");
        interpreter.handleCommand("get horn");
        interpreter.handleCommand("goto riverbank");
        interpreter.handleCommand("goto forest");
        interpreter.handleCommand("goto cabin");
        assertNull(testMap.getCurrentLocation().getEntity("horn"));
        assertNotNull(testMap.getCurrentLocation().getEntity("potion"));
        interpreter.handleCommand("blow horn");
        assertNull(testMap.getCurrentLocation().getEntity("horn"));
        assertTrue(testMap.getCurrentPlayer().getInventoryAsString().contains("horn"));
        assertNotNull(testMap.getCurrentLocation().getEntity("lumberjack"));
    }

    @Test
    void testPartialCommand1() {
        interpreter.handleCommand("get axe");
        interpreter.handleCommand("goto forest");
        interpreter.handleCommand("chop tree");
        String response = interpreter.handleCommand("look");
        assertTrue(response.contains("A heavy wooden log"));
    }

    @Test
    void testDecoratedCommand1() {
        interpreter.handleCommand("get the shiny axe");
        assertTrue(testMap.getCurrentPlayer().getInventoryAsString().contains("axe"));
        interpreter.handleCommand("goto the scary forest");
        assertTrue(testMap.getCurrentLocation().getAllEntitiesAsString("furniture").contains("tree"));
        assertTrue(testMap.getCurrentLocation().getAllEntitiesAsString("artefacts").contains("key"));
        interpreter.handleCommand("the beautiful tree chop");
        String response = interpreter.handleCommand("look");
        assertTrue(response.contains("A heavy wooden log"));
    }

    @Test
    void testCapitalisation1() {
        interpreter.handleCommand("GET POTION");
        String response  = interpreter.handleCommand("look");
        assertFalse(response.contains("A bottle of magic potion"));
        response  = interpreter.handleCommand("iNv");
        assertTrue(response.contains("A bottle of magic potion"));
        interpreter.handleCommand("dRINK pOTiON");
        System.out.println(testMap.getCurrentPlayer().health);
        assertEquals(3, testMap.getCurrentPlayer().health);
    }

    @Test
    void testWordOrdering1() {
        interpreter.handleCommand(" axe          GET");
        interpreter.handleCommand("forest goto");
        String response  = interpreter.handleCommand("look");
        assertTrue(response.contains("A tall pine tree"));
        response  = interpreter.handleCommand("iNv");
        assertTrue(response.contains("axe"));
        interpreter.handleCommand("tree cut down axe");
        response  = interpreter.handleCommand("look");
        System.out.println(response);
        assertTrue(response.contains("A heavy wooden log"));
    }

}
