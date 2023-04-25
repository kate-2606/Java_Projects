package edu.uob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URI;
import java.nio.file.Path;

public class GameActionsParser {

    public GameActionsParser (File actionsFile) throws ParserConfigurationException, IOException, SAXException {
        parseActions(actionsFile);
    }

    private enum elementType { triggers, subjects, consumed, produced, narration }

    private void parseActions(File actionsFile)
            throws SAXException, ParserConfigurationException, IOException {

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        Document document = builder.parse("config" + File.separator + "basic-actions.xml");
        Element root = document.getDocumentElement();
        NodeList actionsDetails = root.getChildNodes();

        //for each action in actions
        int length = actionsDetails.getLength();
        for (int i = 0; i < length; i++) {
            if (actionsDetails.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element actionDetails = (Element) actionsDetails.item(i);
                GameAction action = new GameAction();
                logActionElements(actionDetails, action);
            }
        }
    }

    //revise this
    private void logActionElements(Element inputDetails, GameAction action){

        for(elementType type : elementType.values()){
            String typeName = String.valueOf(type);
            Element elementDetails = (Element) inputDetails.getElementsByTagName(typeName).item(0);

            elementType element = elementType.valueOf(typeName);
            String entityName = element == elementType.triggers? "keyphrase" : "entity";
            NodeList entities = elementDetails.getElementsByTagName(entityName);

            if(entities !=null && element!=elementType.narration) {
                System.out.println("Type: " + element);
                logElementEntities(entities, typeName, action);
            }
            if(element == elementType.narration){

            }
        }
    }

    private void logElementEntities(NodeList entities, String elementName, GameAction action) {

        int i = 0;
        if (entities.item(i) != null) {
            String nextPhrase = entities.item(i).getTextContent();
            elementType element = elementType.valueOf(elementName);
            System.out.println("element: " + element);

            while (nextPhrase != null) {
                switch (element) {
                    case triggers:
                        action.addTrigger(nextPhrase);
                        break;
                    case subjects:
                        action.addSubject(nextPhrase);
                        break;
                    case consumed:
                        action.addConsumed(nextPhrase);
                        break;
                    case produced:
                        action.addProduced(nextPhrase);
                        break;
                    default:
                        break;
                }
                i++;

                nextPhrase = entities.item(i) == null ? null : entities.item(i).getTextContent();

            }
        }
    }
}

