import java.util.ArrayList;

public class Network {
    private fileParser fp;
    private ArrayList<VariableNode> net;
    private String networkName;

    public Network(String filepath) {
        fp = new fileParser(filepath);
        net = fp.getData();
        networkName = "";
    }

    public Network(String filepath, String networkName) { // option to add network name
        fp = new fileParser(filepath);
        net = fp.getData();
        this.networkName = networkName;
    }

    public int findNodeIndex(String nodeName) {
        for (int i = 0; i < net.size(); i++) {
            if (net.get(i).getName().equals(nodeName)) {
                return i;
            }
        }
        return -1;
    }

    public String toString() {
        String s = "NETWORK: " + networkName;
        for (VariableNode var : net) {
            s += "\n" + var.toString();
        }
        return s;
    }

    public ArrayList<VariableNode> getNet() {
        return net;
    }
}
