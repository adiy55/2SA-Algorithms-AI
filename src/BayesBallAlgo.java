import java.util.HashMap;
import java.util.Stack;

public class BayesBallAlgo implements NetworkAlgo {
    private HashMap<String, VariableNode> data;
    private String input;
    private String[] query_nodes;

    /**
     * Bayes Ball Algorithm constructor.
     *
     * @param data  Network
     * @param input Query (parsed from the text file)
     */
    public BayesBallAlgo(HashMap<String, VariableNode> data, String input) {
        this.data = data;
        this.input = input;
        parseInput();
    }

    /**
     * Bayes Ball Algorithm constructor (used in Variable Elimination).
     *
     * @param data        Network
     * @param query_nodes array containing the query variable and a hidden variable.
     */
    public BayesBallAlgo(HashMap<String, VariableNode> data, String[] query_nodes) {
        this.data = data;
        this.query_nodes = query_nodes;
    }

    /**
     * Calls the functions in the order needed to run the algorithm.
     *
     * @return string (search function output): yes = independent (no path was found), no = dependant (a path was found)
     */
    @Override
    public String RunAlgo() {
        String res = search();
        ResetAttributes();
        return res;
    }

    /**
     * Reset the variable attributes used in this algorithm.
     */
    @Override
    public void ResetAttributes() {
        for (VariableNode v : data.values()) {
            v.setFromChild(false);
            v.setFromParent(false);
        }
    }

    /**
     * Extracts the current query variables from the string given input.
     * Input examples:
     * B-E|
     * B-E|J=T
     */
    public void parseInput() {
        String[] s = input.split("\\|");
        String[] query_node_names = s[0].split("-");
        if (s.length > 1) { // might not be any evidence nodes
            String[] given_nodes = s[1].split(",");
            for (String given_node : given_nodes) {
                String[] tmp = given_node.split("=");
                data.get(tmp[0]).setEvidence(tmp[1]); // set the given evidence of current node
            }
        }
        this.query_nodes = query_node_names;
    }

    /**
     * Checks if two nodes are independent.
     * Adds all children and parent nodes of the first query node.
     * If a path is found to the second query node- the nodes are dependant.
     * The search follows the Bayes Ball algorithm rules.
     *
     * @return String result (no = dependant, yes = independent)
     */
    private String search() { // DFS approach
        Stack<VariableNode> s = new Stack<>();
        VariableNode v = data.get(query_nodes[0]);
        addChildren(s, v);
        addParents(s, v);
        while (!s.isEmpty()) {
            v = s.pop();
            if (v.getName().equals(query_nodes[1])) {
                return "no";
            }
            if (!v.isEvidence()) {
                addChildren(s, v); // can go to any child
                if (v.isFromChild()) { // if a node is from child can go to any parent
                    addParents(s, v);
                }
            } else if (v.isEvidence() && v.isFromParent()) { // if a node is evidence can go from parent to parent
                addParents(s, v);
            }
        }
        return "yes";
    }

    /**
     * Add parent nodes tha have not been added from a child to the stack.
     *
     * @param s Stack from search
     * @param v current variable
     */
    private void addParents(Stack<VariableNode> s, VariableNode v) {
        for (int i = 0; i < v.getParents().size(); i++) {
            VariableNode currParent = v.getParents().get(i);
            if (!currParent.isFromChild()) {
                currParent.setFromChild(true);
                s.push(currParent);
            }
        }
    }

    /**
     * Add children nodes that have not been added from a parent to the stack.
     *
     * @param s Stack from search
     * @param v current variable
     */
    private void addChildren(Stack<VariableNode> s, VariableNode v) {
        for (int i = 0; i < v.getChildren().size(); i++) {
            VariableNode currChild = v.getChildren().get(i);
            if (!currChild.isFromParent()) {
                currChild.setFromParent(true);
                s.push(currChild);
            }
        }
    }

}
