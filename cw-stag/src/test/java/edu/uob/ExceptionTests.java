package edu.uob;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

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
        interpreter = new CommandInterpreter(testMap, tester, library);
    }

    GameMap testMap;

    GameCharacter tester;

    ActionLibrary library;

    CommandInterpreter interpreter;

    @Test
    void testCannotDropMultiple(){
        assertThrows(GameExceptions.CannotGetOrDropMultiple.class, ()-> interpreter.handleCommand("get potion axe"));
    }


}
