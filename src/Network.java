import java.util.ArrayList;
import java.util.HashMap;

public class Network {
    private FileParser parser;
    private HashMap<String, VariableNode> net;
    private ArrayList<String> queries;
    private ArrayList<String> results;

    public Network(String filepath) {
        parser = new FileParser(filepath);
        net = parser.getData();
        queries = parser.getQueries();
        results = new ArrayList<>();
        runQueries();
    }

    public void runQueries() {
        for (String curr_query : queries) {
            if (curr_query.contains("P(")) {
                VariableEliminationAlgo vea = new VariableEliminationAlgo(net, curr_query);
                String res = vea.RunAlgo();
                results.add(res);
            } else {
                BayesBallAlgo bba = new BayesBallAlgo(net, curr_query);
                String res = bba.RunAlgo();
                results.add(res);
            }
            resetVariables();
        }
    }

    public void resetVariables() {
        for (VariableNode v : net.values()) {
            v.setFromChild(false);
            v.setFromParent(false);
            v.setEvidence(null);
            v.initCPT();
        }
    }

    public ArrayList<String> getResults() {
        return results;
    }

    public HashMap<String, VariableNode> getNet() {
        return net;
    }

    public String toString() {
        String s = "NETWORK: ";
        for (Object var : net.values()) {
            s = s + "\n" + var.toString();
        }
        return s;
    }
}
