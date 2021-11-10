import java.io.FileNotFoundException;
import java.util.HashMap;

public class Network {
    private txtReader reader;
    private HashMap net;

    public Network(String filepath) throws FileNotFoundException {
        reader = new txtReader(filepath);
        net = new xpathParser("src/" + reader.getXml()).getData();
    }

    public txtReader getReader() {
        return reader;
    }

    public HashMap getNet() {
        return net;
    }

    public void resetVariables() {
        for (Object v : net.values()) {
            VariableNode vn = (VariableNode) v;
            vn.setFromChild(false);
            vn.setFromParent(false);
            vn.setEvidence(null);
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
