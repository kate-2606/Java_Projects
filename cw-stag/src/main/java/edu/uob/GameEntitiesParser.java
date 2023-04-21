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
        this.entitiesFile = entitiesFile;
        this.gameMap = inputMap;
        this.parser = new Parser();
        parseEntities();
    }

    Map gameMap;
    File entitiesFile;
    Parser parser;


    private void parseEntities() throws FileNotFoundException, ParseException {
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

        // The paths will always be in the second subgraph
        ArrayList<Edge> paths = sections.get(1).getEdges();
        Edge firstPath = paths.get(0);
        Node fromLocation = firstPath.getSource().getNode();
        String fromName = fromLocation.getId().getId();
        Node toLocation = firstPath.getTarget().getNode();
        String toName = toLocation.getId().getId();
    }

    private Location getLocation(Graph locationData){

        Node locationDetails = locationData.getNodes(false).get(0);
        String locationName = locationDetails.getId().getId();
        String locationDescription = locationDetails.getAttributes().get("description");

        Location gameLocation = new Location(locationName, locationDescription);
        getArtefacts(locationData, gameLocation);
        //System.out.println("LOCATION:" + locationName + "   " + locationDescription);
        return gameLocation;
    }

    private void getArtefacts(Graph locationData, Location gameLocation){

        ArrayList<Graph> itemTypes = locationData.getSubgraphs();
        ArrayList<Node> artifactDetails = null;

        for(Graph type : itemTypes){
            String typeName = type.getId().getId();
            if(typeName.equals("artefacts")){
                artifactDetails = type.getNodes(false);
            }
        }

        HashMap<String, Artifact> locationArtifacts = new HashMap<>();
        if(artifactDetails!=null) {
            for (Node item : artifactDetails) {
                String name = item.getId().getId();
                String description = item.getAttributes().get("description");
                Artifact foundArtifact = new Artifact(name, description);
                locationArtifacts.put(foundArtifact.getName(), foundArtifact);
                //System.out.println(name + "   " + description);
            }
            gameLocation.setArtifacts(locationArtifacts);
        }
    }

}
