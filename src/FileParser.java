import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/*
Document Object Model (DOM) is an interface that represent an XML file as a tree structure and enables programs to access the contents of the file.
XPath enables navigation through a DOM to locate nodes.
I used XPath to retrieve specific elements from the XML and initialize them in a data structure that represents the network.
 */

public class FileParser {
    private String filename; // path of the input file
    private HashMap<String, VariableNode> data; // String = variable name
    private ArrayList<String> queries; // list of unparsed queries (they are parsed individually in each algorithm)

    /**
     * File parser constructor.
     *
     * @param filename input file path
     */
    public FileParser(String filename) {
        this.filename = filename;
        this.data = new HashMap<>();
        this.queries = new ArrayList<>();
        try {
            parseFile();
        } catch (IOException | XPathExpressionException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }

    private void parseFile() throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
        Scanner sc = new Scanner(new File(filename));
        while (sc.hasNext()) {
            String currLine = sc.nextLine();
            if (currLine.endsWith(".xml")) { // parses XML
                readXML(currLine);
            } else { // adds query to list
                queries.add(currLine);
            }
        }
    }

    private void readXML(String xml_path) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(xml_path));
        XPath xp = XPathFactory.newInstance().newXPath();
        parseVariable(xp, doc); // initializes variable nodes and sets their outcomes
        parseDefinition(xp, doc); // sets table, parent and children nodes
    }

    private void parseVariable(XPath xp, Document doc) throws XPathExpressionException {
        NodeList nodes = (NodeList) xp.compile("//VARIABLE").evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            NodeList currName = (NodeList) xp.compile(String.format("/NETWORK/VARIABLE[%d]/NAME", i + 1)).evaluate(doc, XPathConstants.NODESET);
            NodeList currOutcome = (NodeList) xp.compile(String.format("/NETWORK/VARIABLE[%d]/OUTCOME", i + 1)).evaluate(doc, XPathConstants.NODESET);

            String name = currName.item(0).getTextContent();
            ArrayList<String> outcomes = new ArrayList<>();
            for (int j = 0; j < currOutcome.getLength(); j++) {
                String s = currOutcome.item(j).getTextContent();
                outcomes.add(s);
            }
            VariableNode vn = new VariableNode(name, outcomes);
            data.put(vn.getName(), vn);
        }
    }

    private void parseDefinition(XPath xp, Document doc) throws XPathExpressionException {
        NodeList nodes = (NodeList) xp.compile("//DEFINITION").evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodes.getLength(); i++) {
            NodeList currName = (NodeList) xp.compile(String.format("/NETWORK/DEFINITION[%d]/FOR", i + 1)).evaluate(doc, XPathConstants.NODESET);
            NodeList currTable = (NodeList) xp.compile(String.format("/NETWORK/DEFINITION[%d]/TABLE", i + 1)).evaluate(doc, XPathConstants.NODESET);
            NodeList currGiven = (NodeList) xp.compile(String.format("/NETWORK/DEFINITION[%d]/GIVEN", i + 1)).evaluate(doc, XPathConstants.NODESET);

            VariableNode vn = data.get(currName.item(0).getTextContent()); // current node
            String[] tableString = currTable.item(0).getTextContent().split(" ");

            ArrayList<Double> table = new ArrayList<>();
            for (String value : tableString) {
                table.add(Double.parseDouble(value));
            }
            ArrayList<VariableNode> given = new ArrayList<>();
            for (int j = 0; j < currGiven.getLength(); j++) {
                String s = currGiven.item(j).getTextContent();
                given.add(data.get(s));
                data.get(s).addChild(data.get(currName.item(0).getTextContent())); // set the current node as the child of its parent node (given)
            }
            vn.setTable(table);
            vn.setParents(given);
            vn.initCPT(); // initialize CPT for each node
        }
    }

    /**
     * @return HashMap containing the network
     */
    public HashMap<String, VariableNode> getData() {
        return data;
    }

    /**
     * @return ArrayList containing the queries
     */
    public ArrayList<String> getQueries() {
        return queries;
    }

}