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

public class xpathParser {
    private String filename;
    private HashMap data;

    public xpathParser(String filename) {
        this.filename = filename;
        this.data = new HashMap();
        try {
            readXML();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
    }

    private void readXML() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(filename));
        XPath xp = XPathFactory.newInstance().newXPath();
        parseVariable(xp, doc);
        parseDefinition(xp, doc);
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

            VariableNode vn = (VariableNode) data.get(currName.item(0).getTextContent()); // current node
            String[] tableString = currTable.item(0).getTextContent().split(" ");

            ArrayList<Double> table = new ArrayList<>();
            for (int j = 0; j < tableString.length; j++) {
                table.add(Double.parseDouble(tableString[j]));
            }

            ArrayList<VariableNode> given = new ArrayList<>();
            for (int j = 0; j < currGiven.getLength(); j++) {
                String s = currGiven.item(j).getTextContent();
                given.add((VariableNode) data.get(s));
                ((VariableNode) data.get(s)).addChild((VariableNode) data.get(currName));
            }

            vn.setTable(table);
            vn.setParents(given);
        }
    }

    public HashMap getData() {
        return data;
    }

    public static void main(String[] args) {
        xpathParser mp = new xpathParser("src/alarm_net.xml");
        System.out.println(mp.data);
    }
}
