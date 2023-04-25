package edu.uob;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import static edu.uob.ActionElement.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

public class GameActionsParser {

    public GameActionsParser (File actionsFile, ActionLibrary library) throws ParserConfigurationException,
            IOException, SAXException {
        this.library = library;
        parseActions(actionsFile);

    }

    ActionLibrary library;

    //the action file will always be in the config folder? not in a subfolder?
    //only odd actions are actually actions?
    private void parseActions(File actionsFile)
            throws SAXException, ParserConfigurationException, IOException {

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        String[] splitPath = actionsFile.toString().split(File.separator);
        int num = splitPath.length;
        String filePath = splitPath[num-2]+ File.separator + splitPath[num-1];

        Document document = builder.parse(filePath);
        Element root = document.getDocumentElement();
        NodeList actionsDetails = root.getChildNodes();

        //for each action in actions
        int length = actionsDetails.getLength();

        for (int i = 0; i < length; i++) {
            if (actionsDetails.item(i).getNodeType() == Node.ELEMENT_NODE) {

                Element actionDetails = (Element) actionsDetails.item(i);

                GameAction action = new GameAction();
                logActionElements(actionDetails, action);
                library.addAction(action);

            }
        }
    }

    //revise this
    private void logActionElements(Element inputDetails, GameAction action){

        for(ActionElement type : ActionElement.values()){

            String typeName = String.valueOf(type);
            Element elementDetails = (Element) inputDetails.getElementsByTagName(typeName).item(0);

            String entityName = type == triggers ? "keyphrase" : "entity";
            NodeList words = elementDetails.getElementsByTagName(entityName);

            if(words !=null && type!=narration) {
                logElementEntities(words, type, action);
            }
            if(type == narration){
                Element n = (Element) inputDetails.getElementsByTagName(narration.toString()).item(0);
                action.addNarration(n.getTextContent());
            }
        }
    }

    private void logElementEntities(NodeList words, ActionElement type, GameAction action) {

        int i = 0;
        if (words.item(i) != null) {
            String nextPhrase = words.item(i).getTextContent();

            while (nextPhrase != null) {
                switch (type) {
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
                nextPhrase = words.item(i) == null ? null : words.item(i).getTextContent();
            }
        }
    }
}

