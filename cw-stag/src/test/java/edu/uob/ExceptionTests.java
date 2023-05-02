package edu.uob;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static edu.uob.BasicCommand.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExceptionTests {

    @BeforeEach
    void setUpParser() throws IOException, ParserConfigurationException, SAXException, ParseException {
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        library = new ActionLibrary();
        testMap = new GameMap();

        GameEntitiesParser entitiesParser = new GameEntitiesParser(entitiesFile, testMap);
        GameActionsParser parser = new GameActionsParser(actionsFile, library, testMap);
        tester = new GameCharacter("tester", "strict tester", testMap.getStartLocation());
        testMap.instantiatePlayer(tester);
        interpreter = new CommandInterpreter(testMap, tester, library);
    }

    GameMap testMap;

    GameCharacter tester;

    ActionLibrary library;

    CommandInterpreter interpreter;

    @Test
    void testCannotGetOrDropMultiple1() {
        assertThrows(GameExceptions.CannotGetOrDropMultiple.class, ()-> interpreter.handleCommand("get potion axe"));
    }
    @Test
    void testCannotGetOrDropMultiple2(){
        assertThrows(GameExceptions.CannotGetOrDropMultiple.class, ()-> interpreter.handleCommand("get potion potion"));
    }

    @Test
    void testCannotGetOrDropMultiple3() throws GameExceptions {
        interpreter.handleCommand("goto forest");
        interpreter.handleCommand("goto riverbank");
        assertThrows(GameExceptions.CannotGetOrDropMultiple.class, ()-> interpreter.handleCommand("horn get horn"));
    }

    @Test
    void testCannotGetOrDropNothing1() throws GameExceptions {
        interpreter.handleCommand("goto forest");
        assertThrows(GameExceptions.CannotGetOrDropItem.class, ()-> interpreter.handleCommand("get"));
    }

    @Test
    void testCannotGetOrDropItem1() {
        assertThrows(GameExceptions.CannotGetOrDropItem.class, ()-> interpreter.handleCommand("get trapdoor"));
    }

    @Test
    void testMultipleTriggerWords1() {
        assertThrows(GameExceptions.MultipleTriggerWords.class, ()-> interpreter.handleCommand("look inv"));
    }

    @Test
    void testMultipleTriggerWords2() throws GameExceptions {
        interpreter.handleCommand("get coin");
        interpreter.handleCommand("goto forest");
        interpreter.handleCommand("get key");
        interpreter.handleCommand("goto cabin");
        assertThrows(GameExceptions.MultipleTriggerWords.class, ()-> interpreter.handleCommand("unlock trapdoor with key and look"));
    }

    @Test
    void testExeraneousEntities() throws GameExceptions {
        interpreter.handleCommand("goto forest");
        interpreter.handleCommand("get key");
        interpreter.handleCommand("goto cabin");
        interpreter.handleCommand("get axe");
        assertThrows(GameExceptions.ExeraneousEntities.class, ()-> interpreter.handleCommand("unlock trapdoor with axe and key"));
    }



}
