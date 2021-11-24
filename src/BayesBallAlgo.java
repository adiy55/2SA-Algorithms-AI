import java.util.HashMap;
import java.util.Stack;

public class BayesBallAlgo implements NetworkAlgo {
    private HashMap<String, VariableNode> data;
    private String input;
    private String[] query_nodes;

    public BayesBallAlgo(HashMap<String, VariableNode> data, String input) {
        this.data = data;
        this.input = input;
        parseInput();
    }

    public BayesBallAlgo(HashMap<String, VariableNode> data, String[] query_nodes) {
        this.data = data;
        this.query_nodes = query_nodes;
    }

    private void parseInput() {
        String[] s = input.split("\\|");
        String[] query_node_names = s[0].split("-");
        if (s.length > 1) {
            String[] given_nodes = s[1].split(",");
            for (String given_node : given_nodes) {
                String[] tmp = given_node.split("=");
                data.get(tmp[0]).setEvidence(tmp[1]);
            }
        }
        this.query_nodes = query_node_names;
    }

    @Override
    public String RunAlgo() {
        String res = search();
        for (VariableNode v : data.values()) {
            v.setFromChild(false);
            v.setFromParent(false);
        }
        return res;
    }

    private String search() { // DFS
        Stack<VariableNode> s = new Stack<>();
        VariableNode v = data.get(query_nodes[0]);
        s.push(v);
        v = s.pop();
        add_children(s, v);
        add_parents(s, v);
        while (!s.isEmpty()) {
            if (v.getName().equals(query_nodes[1])) {
                return "no";
            }
            v = s.pop();
            if (!v.isEvidence()) {
                add_children(s, v);
                if (v.isFromChild()) {
                    add_parents(s, v);
                }
            } else if (v.isEvidence() && v.isFromParent()) {
                add_parents(s, v);
            }
        }
        return "yes";
    }

    private void add_parents(Stack<VariableNode> s, VariableNode v) {
        for (int i = 0; i < v.getParents().size(); i++) {
            VariableNode currParent = v.getParents().get(i);
            if (!currParent.isFromChild()) {
                currParent.setFromChild(true);
                s.push(currParent);
            }
        }
    }

    private void add_children(Stack<VariableNode> s, VariableNode v) {
        for (int i = 0; i < v.getChildren().size(); i++) {
            VariableNode currChild = v.getChildren().get(i);
            if (!currChild.isFromParent()) {
                currChild.setFromParent(true);
                s.push(currChild);
            }
        }
    }

}
