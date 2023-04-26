package edu.uob;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

import javax.xml.stream.Location;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

final class EntitiesFileTests {

  // Test to make sure that the basic entities file is readable

    GameMap testMap;
    @BeforeEach
    void setUpParser() throws FileNotFoundException, ParseException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        testMap = new GameMap();
        GameEntitiesParser parser = new GameEntitiesParser(entitiesFile, testMap);
    }
  @Test
  void testBasicEntitiesFileIsReadable() {
      try {
          Parser parser = new Parser();
          FileReader reader = new FileReader("config" + File.separator + "basic-entities.dot");
          parser.parse(reader);
          Graph wholeDocument = parser.getGraphs().get(0);
          ArrayList<Graph> sections = wholeDocument.getSubgraphs();

          // The locations will always be in the first subgraph
          ArrayList<Graph> locations = sections.get(0).getSubgraphs();
          Graph firstLocation = locations.get(0);
          Node locationDetails = firstLocation.getNodes(false).get(0);
          // Yes, you do need to get the ID twice !
          String locationName = locationDetails.getId().getId();
          assertEquals("cabin", locationName, "First location should have been 'cabin'");

          // The paths will always be in the second subgraph
          ArrayList<Edge> paths = sections.get(1).getEdges();
          Edge firstPath = paths.get(0);
          Node fromLocation = firstPath.getSource().getNode();
          String fromName = fromLocation.getId().getId();
          Node toLocation = firstPath.getTarget().getNode();
          String toName = toLocation.getId().getId();
          assertEquals("cabin", fromName, "First path should have been from 'cabin'");
          assertEquals("forest", toName, "First path should have been to 'forest'");

      } catch (FileNotFoundException fnfe) {
          fail("FileNotFoundException was thrown when attempting to read basic entities file");
      } catch (ParseException pe) {
          fail("ParseException was thrown when attempting to read basic entities file");
      }
  }

  @Test
    void locationsTest() {
      assertEquals(3, testMap.getNumberOfLocations());
      assertNotNull(testMap.getLocation("cabin"));
      assertNotNull(testMap.getLocation("forest"));
      assertNotNull(testMap.getLocation("cellar"));
      assertNull(testMap.getLocation("storeroom"));
      assertNull(testMap.getLocation("pond"));
      assertNull(testMap.getLocation("pool"));
      assertNull(testMap.getLocation("beach"));
      assertNull(testMap.getLocation("church"));
  }

    @Test
    void cabinTest() {
        GameLocation currentLocation = testMap.getLocation("cabin");
        assertEquals("A log cabin in the woods", currentLocation.getDescription());

        assertEquals(1, currentLocation.getNumberOfArtefacts());
        assertNotNull(currentLocation.getArtefact("potion"));
        assertEquals("Magic potion", currentLocation.getArtefact("potion").getDescription());
        assertNull(currentLocation.getArtefact("key"));

        assertEquals(1, currentLocation.getNumberOfFurniture());
        assertNotNull(currentLocation.getFurniture("trapdoor"));
        assertEquals("Wooden trapdoor", currentLocation.getFurniture("trapdoor").getDescription());
        assertNull(currentLocation.getFurniture("door"));

        assertEquals(0, currentLocation.getNumberOfCharacters());
        assertNull(currentLocation.getCharacter("elf"));
        assertTrue(currentLocation.getAllPathsAsString().contains("forest"));
        assertFalse(currentLocation.getAllPathsAsString().contains("cellar"));
    }

    @Test
    void forestTest() {
        GameLocation currentLocation = testMap.getLocation("forest");
        assertEquals("A dark forest", currentLocation.getDescription());

        assertEquals(1, currentLocation.getNumberOfArtefacts());
        assertNotNull(currentLocation.getArtefact("key"));
        assertEquals("Brass key", currentLocation.getArtefact("key").getDescription());
        assertNull(currentLocation.getArtefact("potion"));

        assertEquals(1, currentLocation.getNumberOfFurniture());
        assertNotNull(currentLocation.getFurniture("tree"));
        assertEquals("A big tree", currentLocation.getFurniture("tree").getDescription());
        assertNull(currentLocation.getFurniture("trapdoor"));

        assertEquals(0, currentLocation.getNumberOfCharacters());
        assertNull(currentLocation.getCharacter("witch"));

        assertTrue(currentLocation.getAllPathsAsString().contains("cabin"));
        assertFalse(currentLocation.getAllPathsAsString().contains("cellar"));
    }

    @Test
    void cellarTest() {
        GameLocation currentLocation = testMap.getLocation("cellar");
        assertEquals("A dusty cellar", currentLocation.getDescription());

        assertEquals(0, currentLocation.getNumberOfArtefacts());
        assertNull(currentLocation.getArtefact("key"));


        assertEquals(0, currentLocation.getNumberOfFurniture());
        assertNull(currentLocation.getFurniture("trapdoor"));

        assertEquals(1, currentLocation.getNumberOfCharacters());
        assertNotNull(currentLocation.getCharacter("elf"));
        assertEquals("Angry Elf", currentLocation.getCharacter("elf").getDescription());

        assertTrue(currentLocation.getAllPathsAsString().contains("cabin"));
        assertFalse(currentLocation.getAllPathsAsString().contains("forest"));
    }

    @Test
    void storeroomTest() {
        GameLocation currentLocation = testMap.getStoreroom();
        assertEquals("Storage for any entities not placed in the game", currentLocation.getDescription());

        assertEquals(1, currentLocation.getNumberOfArtefacts());
        assertNull(currentLocation.getArtefact("A heavy wooden log"));
        assertEquals("A heavy wooden log", currentLocation.getArtefact("log").getDescription());

        assertEquals(0, currentLocation.getNumberOfFurniture());
        assertNull(currentLocation.getFurniture("tree"));

        assertEquals(0, currentLocation.getNumberOfCharacters());
        assertNull(currentLocation.getCharacter("elf"));

    }




}
