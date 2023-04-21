package edu.uob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;

public class GameActionsParser {
    public GameActionsParser (String actionFileLocation){

    }

    private void parseActions(String actionFileLocation)
            throws SAXException, ParserConfigurationException, IOException {

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(actionFileLocation);
        Element root = document.getDocumentElement();
        NodeList actions = root.getChildNodes();

        int i=1;
        while ((Element)actions.item(i)!=null){

            ArrayList<Element> action = (ArrayList<Element>) actions.item(i);

            GameAction newAction = new GameAction();

            ArrayList<Element> triggers = action.getElementsByTagName("triggers");
            addTriggers((Element)actions.item(i), newAction);
        }
        Element firstAction = (Element)actions.item(1);
        Element triggers = (Element)firstAction.getElementsByTagName("triggers").item(0);
        // Get the first trigger phrase
        String firstTriggerPhrase = triggers.getElementsByTagName("keyphrase").item(0).getTextContent();
        //assertEquals("open", firstTriggerPhrase, "First trigger phrase was not 'open'");
    }

    private void addTriggers(Element action, GameAction newAction){



        for(Element trigger : triggers){
            newAction.addTrigger();
        }
    }
}

