package edu.uob;

import com.alexmerz.graphviz.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.IOException;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
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
    void setUpParser() throws IOException, ParserConfigurationException, SAXException {
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        library = new ActionLibrary();
        GameActionsParser parser = new GameActionsParser(actionsFile, library);
    }

    ActionLibrary library;
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
      HashSet<GameAction> actions= library.getActions("open");
      GameAction action = actions.iterator().next();
      assertEquals(1, actions.size());
      ArrayList<String> triggers = action.getTriggers();
      assertEquals(triggers.get(1), "unlock");
      ArrayList<String> subjects = action.getSubjects();
      assertEquals(subjects.get(0), "trapdoor");
      assertEquals(subjects.get(1), "key");
      ArrayList<String> consumed = action.getConsumed();
      assertEquals(consumed.get(0), "key");
      ArrayList<String> produced = action.getProduced();
      assertEquals(produced.get(0), "cellar");
      assertEquals(action.getNarration(), "You unlock the trapdoor and see steps leading down into a cellar");
  }

    @Test
    void secondAction(){
        HashSet<GameAction> actions= library.getActions("chop");
        GameAction action = actions.iterator().next();
        assertEquals(1, actions.size());
        ArrayList<String> triggers = action.getTriggers();
        assertEquals(triggers.get(1), "cut");
        assertEquals(triggers.get(2), "cutdown");
        ArrayList<String> subjects = action.getSubjects();
        assertEquals(subjects.get(0), "tree");
        assertEquals(subjects.get(1), "axe");
        ArrayList<String> consumed = action.getConsumed();
        assertEquals(consumed.get(0), "tree");
        ArrayList<String> produced = action.getProduced();
        assertEquals(produced.get(0), "log");
        assertEquals(action.getNarration(), "You cut down the tree with the axe");
    }

    @Test
    void thirdAction(){
        HashSet<GameAction> actions= library.getActions("drink");
        GameAction action = actions.iterator().next();
        assertEquals(1, actions.size());
        ArrayList<String> subjects = action.getSubjects();
        assertEquals(subjects.get(0), "potion");
        ArrayList<String> consumed = action.getConsumed();
        assertEquals(consumed.get(0), "potion");
        ArrayList<String> produced = action.getProduced();
        assertEquals(produced.get(0), "health");
        assertEquals(action.getNarration(), "You drink the potion and your health improves");
    }

    @Test
    void fourthAction(){
        HashSet<GameAction> actions= library.getActions("fight");
        GameAction action = actions.iterator().next();
        assertEquals(1, actions.size());
        ArrayList<String> triggers = action.getTriggers();
        assertEquals(triggers.get(1), "hit");
        assertEquals(triggers.get(2), "attack");
        ArrayList<String> subjects = action.getSubjects();
        assertEquals(subjects.get(0), "elf");
        ArrayList<String> consumed = action.getConsumed();
        assertEquals(consumed.get(0), "health");
        ArrayList<String> produced = action.getProduced();
        assertEquals(action.getNarration(), "You attack the elf, but he fights back and you lose some health");
    }

}
