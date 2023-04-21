package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Edge;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class GameEntitiesParser {

    public GameEntitiesParser(File entitiesFile, Map inputMap) throws FileNotFoundException, ParseException {
        this.gameMap = inputMap;
        parseEntities(entitiesFile);
    }

    Map gameMap;

    private void parseEntities(File entitiesFile) throws FileNotFoundException, ParseException {
        Parser parser = new Parser();
        FileReader reader = new FileReader(entitiesFile);
        BufferedReader buffReader = new BufferedReader(reader);
        parser.parse(buffReader);

        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> sections = wholeDocument.getSubgraphs();

        ArrayList<Graph> locations = sections.get(0).getSubgraphs();

        for (Graph location : locations){
            gameMap.addLocation(getLocation(location));
        }

        ArrayList<Edge> paths = sections.get(1).getEdges();

        addPaths(gameMap, paths);

    }

    private Location getLocation(Graph locationData){

        Node locationDetails = locationData.getNodes(false).get(0);
        String locationName = locationDetails.getId().getId();
        String locationDescription = locationDetails.getAttributes().get("description");

        Location gameLocation = new Location(locationName, locationDescription);
        addAllEntityTypes(locationData, gameLocation);
        //System.out.println("LOCATION:" + locationName + "   " + locationDescription);
        return gameLocation;
    }

    private void addAllEntityTypes(Graph locationData, Location gameLocation){

        ArrayList<Graph> entityTypes = locationData.getSubgraphs();

        for(Graph entityType : entityTypes){
            addEntities(gameLocation, entityType);
        }
    }

    private void addEntities(Location gameLocation, Graph entityType){

        ArrayList<Node> entityItems = entityType.getNodes(false);
        String typeName = entityType.getId().getId();

        for (Node item : entityItems) {
            String name = item.getId().getId();
            String description = item.getAttributes().get("description");

            if(typeName.equals("artefacts")) {
                Artefact foundArtefact = new Artefact(name, description);
                gameLocation.addArtifact(foundArtefact);
                //System.out.println("Artifact -> " + name + " : " + description);
            }

            if(typeName.equals("furniture")) {
                Furniture foundFurniture = new Furniture(name, description);
                gameLocation.addFurniture(foundFurniture);
                //System.out.println("Furniture -> " + name + " : " + description);
            }
            if(typeName.equals("characters")) {
                Character foundCharacter = new Character(name, description);
                gameLocation.addCharacter(foundCharacter);
                //System.out.println("Character -> " + name + " : " + description);
            }
        }
    }

    private void addPaths(Map gameMap, ArrayList<Edge> paths){

        for(Edge path : paths){
            Node fromNode = path.getSource().getNode();
            String fromName = fromNode.getId().getId();
            //System.out.println("FROM : " + fromName);
            Node toNode = path.getTarget().getNode();
            String toName = toNode.getId().getId();
            //System.out.println("TO : " + toName);

            Location fromLocation = gameMap.getLocation(fromName);

            Location toLocation = gameMap.getLocation(toName);

            fromLocation.addPath(toLocation);
        }

    }
}
