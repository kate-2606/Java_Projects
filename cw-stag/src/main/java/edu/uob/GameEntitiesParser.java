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

public class GameEntitiesParser {

    public GameEntitiesParser(File entitiesFile, GameMap inputGameMap) throws FileNotFoundException, ParseException {
        this.gameMap = inputGameMap;
        parseEntities(entitiesFile);
    }

    GameMap gameMap;

    private void parseEntities(File entitiesFile) throws FileNotFoundException, ParseException {
        Parser parser = new Parser();
        FileReader reader = new FileReader(entitiesFile);
        BufferedReader buffReader = new BufferedReader(reader);
        parser.parse(buffReader);

        Graph wholeDocument = parser.getGraphs().get(0);
        ArrayList<Graph> sections = wholeDocument.getSubgraphs();

        ArrayList<Graph> locations = sections.get(0).getSubgraphs();

        boolean startingPoint = true;
        for (Graph location : locations){
            GameLocation parsedLocation = getLocation(location);
            gameMap.addLocation(parsedLocation);
            if(startingPoint){
                gameMap.setStartLocation(parsedLocation);
                gameMap.setCurrentLocation(parsedLocation);
                startingPoint = false;
            }
        }

        ArrayList<Edge> paths = sections.get(1).getEdges();

        addPaths(gameMap, paths);

    }

    private GameLocation getLocation(Graph locationData){

        Node locationDetails = locationData.getNodes(false).get(0);
        String locationName = locationDetails.getId().getId();
        String locationDescription = locationDetails.getAttributes().get("description");

        GameLocation gameLocation = new GameLocation(locationName, locationDescription);
        addAllEntityTypes(locationData, gameLocation);
        //System.out.println("LOCATION:" + locationName + "   " + locationDescription);
        return gameLocation;
    }

    private void addAllEntityTypes(Graph locationData, GameLocation gameLocation){

        ArrayList<Graph> entityTypes = locationData.getSubgraphs();

        for(Graph entityType : entityTypes){
            addEntities(gameLocation, entityType);
        }
    }

    private void addEntities(GameLocation gameLocation, Graph entityType){

        ArrayList<Node> entityItems = entityType.getNodes(false);
        String typeName = entityType.getId().getId();

        for (Node item : entityItems) {
            String name = item.getId().getId();
            String description = item.getAttributes().get("description");

            if(typeName.equals("artefacts")) {
                GameArtefact foundGameArtefact = new GameArtefact(name, description, gameLocation);
                gameLocation.addArtefact(foundGameArtefact);
                //System.out.println("Artifact -> " + name + " : " + description);
            }

            if(typeName.equals("furniture")) {
                GameFurniture foundGameFurniture = new GameFurniture(name, description, gameLocation);
                gameLocation.addFurniture(foundGameFurniture);
                //System.out.println("Furniture -> " + name + " : " + description);
            }
            if(typeName.equals("characters")) {
                GameCharacter foundGameCharacter = new GameCharacter(name, description, gameLocation);
                gameLocation.addCharacter(foundGameCharacter);
                //System.out.println("Character -> " + name + " : " + description);
            }
        }
    }

    //make sure path is valid and not to storeroom
    private void addPaths(GameMap gameMap, ArrayList<Edge> paths){

        for(Edge path : paths){
            Node fromNode = path.getSource().getNode();
            String fromName = fromNode.getId().getId();
            //System.out.println("FROM : " + fromName);
            Node toNode = path.getTarget().getNode();
            String toName = toNode.getId().getId();
            //System.out.println("toName: "+ toName);
            //System.out.println("TO : " + toName);

            GameLocation fromGameLocation = gameMap.getLocation(fromName);

            fromGameLocation.addPath(toName);
        }

    }
}
