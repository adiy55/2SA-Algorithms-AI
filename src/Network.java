import java.io.FileNotFoundException;
import java.util.HashMap;

public class Network {
    private txtReader reader;
    private HashMap<String, VariableNode> net;

    public Network(String filepath) throws FileNotFoundException {
        reader = new txtReader(filepath);
        net = new xpathParser(reader.getXml()).getData();
    }

    public txtReader getReader() {
        return reader;
    }

    public HashMap<String, VariableNode> getNet() {
        return net;
    }

    public void resetVariables() {
        for (VariableNode v : net.values()) {
            v.setFromChild(false);
            v.setFromParent(false);
            v.setEvidence(null);
        }
    }

    public String toString() {
        String s = "NETWORK: ";
        for (Object var : net.values()) {
            s = s + "\n" + var.toString();
        }
        return s;
    }

}
