import java.util.ArrayList;

public class Network {
    //    private fileParser fp;
    private ArrayList<VariableNode> net;
    private String networkName;
    private inputReader ir;
// input reader (save instead of file parser)

    public Network(String filepath) {
//        fp = new fileParser(filepath);
        ir = new inputReader(filepath);
        net = ir.getData();
        networkName = "";
    }

    public Network(String filepath, String networkName) { // option to add network name
//       fp = new fileParser(filepath);
        ir = new inputReader(filepath);
        net = ir.getData();
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
