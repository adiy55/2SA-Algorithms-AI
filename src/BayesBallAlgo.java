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

    public BayesBallAlgo(HashMap<String, VariableNode> data, String[] query_nodes){
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
        return search();
    }

    private String search() { // DFS
        Stack<VariableNode> s = new Stack<>();
        VariableNode v = data.get(query_nodes[0]);
        s.push(v);
        add_children(s, v);
        add_parents(s, v);
        while (!s.isEmpty()) {
            v = s.pop();
            if (v.getName().equals(query_nodes[1])) {
                return "no";
            }
            if (!v.isEvidence()) {
                if (v.isFromChild()) {
                    add_parents(s, v);
                } else {
                    add_children(s, v);
                }
            }
            if (v.isEvidence() && v.isFromParent()) {
                add_parents(s, v);
            }
        }
        return "yes";
    }

    private void add_parents(Stack<VariableNode> s, VariableNode v) {
        for (int i = 0; i < v.getParents().size(); i++) {
            VariableNode currParent = v.getParents().get(i);
            currParent.setFromChild(true);
            s.push(currParent);
        }
    }

    private void add_children(Stack<VariableNode> s, VariableNode v) {
        for (int i = 0; i < v.getChildren().size(); i++) {
            VariableNode currChild = v.getChildren().get(i);
            currChild.setFromParent(true);
            s.push(currChild);
        }
    }

}


//    private String search4() {
//        Queue<VariableNode> s = new LinkedList<>();
//        s.add(data.get(input[0]));
//        while (!s.isEmpty()) {
//            VariableNode v = s.poll();
//            if (v.getName().equals(input[1])) {
//                return "no";
//            }
//            if (!v.isEvidence()) {
//                if (v.isFromChild()) { // fromChild is true when v is the parent of an evidence node (can't go down)
//                    for (int i = 0; i < v.getParents().size(); i++) {
//                        VariableNode currParent = v.getParents().get(i);
//                        currParent.setFromChild(true);
//                        s.add(currParent);
//                    }
//                } else { // v is the start node or not an evidence node (can only go down)
//                    for (int i = 0; i < v.getChildren().size(); i++) {
//                        VariableNode currChild = v.getChildren().get(i);
//                        currChild.setFromParent(true);
//                        s.add(currChild);
//                    }
//                }
//            }
//            if (v.isEvidence() && v.isFromParent()) {
//                for (int i = 0; i < v.getParents().size(); i++) {
//                    VariableNode currParent = v.getParents().get(i);
//                    currParent.setFromChild(true);
//                    s.add(currParent);
//                }
//            }
//        }
//        return "yes";
//    }
