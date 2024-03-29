package edu.uob;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static org.junit.jupiter.api.Assertions.*;

final class ActionsFileTests {

  // Test to make sure that the basic actions file is readable

    @BeforeEach
    void setUpParser() throws IOException, ParserConfigurationException, SAXException, ParseException {
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        library = new ActionLibrary();
        GameMap map = new GameMap();
        this.map=map;
        GameEntitiesParser entitiesParser = new GameEntitiesParser(entitiesFile,  map);
        GameActionsParser parser = new GameActionsParser(actionsFile, library, map);
    }

    ActionLibrary library;

    GameMap map;
  @Test
  void testBasicActionsFileIsReadable() {
      try {
          DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
          Document document = builder.parse("config" + File.separator + "basic-actions.xml");
          Element root = document.getDocumentElement();
          NodeList actions = root.getChildNodes();
          // Get the first action (only the odd items are actually actions - 1, 3, 5 etc.)
          Element firstAction = (Element)actions.item(1);
          Element triggers = (Element)firstAction.getElementsByTagName("triggers").item(0);
          // Get the first trigger phrase
          String firstTriggerPhrase = triggers.getElementsByTagName("keyphrase").item(0).getTextContent();
          assertEquals("open", firstTriggerPhrase, "First trigger phrase was not 'open'");
      } catch(ParserConfigurationException pce) {
          fail("ParserConfigurationException was thrown when attempting to read basic actions file");
      } catch(SAXException saxe) {
          fail("SAXException was thrown when attempting to read basic actions file");
      } catch(IOException ioe) {
          fail("IOException was thrown when attempting to read basic actions file");
      }
  }

  @Test
    void firstAction(){
      Set<GameAction> actions= library.getActions("open");
      GameAction action = actions.iterator().next();
      assertEquals(1, actions.size());
      ArrayList<String> triggers = action.getTriggers();
      assertEquals(triggers.get(1), "unlock");
      ArrayList<String> subjects = action.getActionAsString("subjects");
      assertEquals(subjects.get(0), "trapdoor");
      assertEquals(subjects.get(1), "key");
      ArrayList<String> consumed = action.getActionAsString("consumed");
      assertEquals(consumed.get(0), "key");
      ArrayList<String> produced = action.getActionAsString("produced");
      assertEquals(produced.get(0), "cellar");
      assertEquals(action.getNarration(), "You unlock the door and see steps leading down into a cellar");
  }

    @Test
    void secondAction(){
        Set<GameAction> actions= library.getActions("chop");
        GameAction action = actions.iterator().next();
        assertEquals(1, actions.size());
        ArrayList<String> triggers = action.getTriggers();
        assertEquals(triggers.get(1), "cut");
        assertEquals(triggers.get(2), "cut down");
        ArrayList<String> subjects = action.getActionAsString("subjects");
        assertEquals(subjects.get(0), "tree");
        assertEquals("forest", map.getEntity("tree").getLocation().getName());
        ArrayList<String> consumed = action.getActionAsString("consumed");
        assertEquals(consumed.get(0), "tree");
        ArrayList<String> produced = action.getActionAsString("produced");
        assertEquals(produced.get(0), "log");
        assertEquals(action.getNarration(), "You cut down the tree with the axe");
    }

    @Test
    void thirdAction(){
        Set<GameAction> actions= library.getActions("drink");
        GameAction action = actions.iterator().next();
        assertEquals(1, actions.size());
        ArrayList<String> subjects = action.getActionAsString("subjects");
        assertEquals(subjects.get(0), "potion");
        ArrayList<String> consumed = action.getActionAsString("consumed");
        assertEquals(consumed.get(0), "potion");
        ArrayList<String> produced = action.getActionAsString("produced");
        assertEquals(action.getNarration(), "You drink the potion and your health improves");
    }

    @Test
    void fourthAction(){
        Set<GameAction> actions= library.getActions("fight");
        GameAction action = actions.iterator().next();
        assertEquals(1, actions.size());
        ArrayList<String> triggers = action.getTriggers();
        assertEquals(triggers.get(1), "hit");
        assertEquals(triggers.get(2), "attack");
        ArrayList<String> subjects = action.getActionAsString("subjects");
        assertEquals(subjects.get(0), "elf");
        ArrayList<String> consumed = action.getActionAsString("consumed");
        ArrayList<String> produced = action.getActionAsString("produced");
        assertEquals(action.getNarration(), "You attack the elf, but he fights back and you lose some health");
    }

}
