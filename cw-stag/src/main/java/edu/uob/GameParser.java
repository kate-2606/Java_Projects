package edu.uob;

import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.objects.Graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

public class GameParser {

    public GameParser(File entitiesFile, File actionsFile ) throws FileNotFoundException, ParseException {
        this.entitiesFile = entitiesFile;
        this.actionsFile = actionsFile;
        this.parser = new Parser();
        parseEntities();
    }

    File actionsFile;
    File entitiesFile;
    Parser parser;
    ArrayList<Graph> graphs;

    private void parseEntities() throws FileNotFoundException, ParseException {
        FileReader reader = new FileReader(entitiesFile);
        BufferedReader buffReader = new BufferedReader(reader);
        parser.parse(buffReader);
        graphs = parser.getGraphs();
        System.out.print(graphs);
    }

}
