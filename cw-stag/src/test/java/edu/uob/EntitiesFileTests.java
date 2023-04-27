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

        assertNotNull(currentLocation.getEntity("potion"));
        assertEquals("Magic potion", currentLocation.getEntity("potion").getDescription());
        assertNull(currentLocation.getEntity("key"));

        assertNotNull(currentLocation.getEntity("trapdoor"));
        assertEquals("Wooden trapdoor", currentLocation.getEntity("trapdoor").getDescription());
        assertNull(currentLocation.getEntity("door"));

        assertNull(currentLocation.getEntity("elf"));
        assertTrue(currentLocation.getAllPathsAsString().contains("forest"));
        assertFalse(currentLocation.getAllPathsAsString().contains("cellar"));
    }

    @Test
    void forestTest() {
        GameLocation currentLocation = testMap.getLocation("forest");
        assertEquals("A dark forest", currentLocation.getDescription());

        assertNotNull(currentLocation.getEntity("key"));
        assertEquals("Brass key", currentLocation.getEntity("key").getDescription());
        assertNull(currentLocation.getEntity("potion"));

        assertNotNull(currentLocation.getEntity("tree"));
        assertEquals("A big tree", currentLocation.getEntity("tree").getDescription());
        assertNull(currentLocation.getEntity("trapdoor"));

        assertNull(currentLocation.getEntity("witch"));

        assertTrue(currentLocation.getAllPathsAsString().contains("cabin"));
        assertFalse(currentLocation.getAllPathsAsString().contains("cellar"));
    }

    @Test
    void cellarTest() {
        GameLocation currentLocation = testMap.getLocation("cellar");
        assertEquals("A dusty cellar", currentLocation.getDescription());

        assertNull(currentLocation.getEntity("key"));

        assertNull(currentLocation.getEntity("trapdoor"));

        assertNotNull(currentLocation.getEntity("elf"));
        assertEquals("Angry Elf", currentLocation.getEntity("elf").getDescription());

        assertTrue(currentLocation.getAllPathsAsString().contains("cabin"));
        assertFalse(currentLocation.getAllPathsAsString().contains("forest"));
    }

    @Test
    void storeroomTest() {
        GameLocation currentLocation = testMap.getStoreroom();
        assertEquals("Storage for any entities not placed in the game", currentLocation.getDescription());

        assertNull(currentLocation.getEntity("A heavy wooden log"));
        assertEquals("A heavy wooden log", currentLocation.getEntity("log").getDescription());

        assertNull(currentLocation.getEntity("tree"));

        assertNull(currentLocation.getEntity("elf"));
    }

    void mapTest() {
        assertNotNull(testMap.getEntity("key"));
        assertNotNull(testMap.getEntity("potion"));
        assertNotNull(testMap.getEntity("trapdoor"));
        assertNotNull(testMap.getEntity("elf"));
        assertNotNull(testMap.getEntity("tree"));

    }




}
