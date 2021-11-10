import java.util.ArrayList;

public class VariableNode {

//    enum State { // for marking nodes in bayes ball algorithm
//        UNOBSERVED,
//        PENDING,
//        OBSERVED
//    }

    private String name; // variable name
    private ArrayList<String> outcomes; // variable outcomes
    private String evidence; // for query
    private ArrayList<VariableNode> parents; // GIVEN
    private ArrayList<VariableNode> children;
    private ArrayList<Double> table;
    private boolean isFromChild;
    private boolean isFromParent;
//    private State state;

    public VariableNode(String name, ArrayList<String> outcomes) {
        this.name = name;
        this.outcomes = outcomes;
        evidence = null;
        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();
        this.table = null;
        isFromChild = isFromParent = false;
//        this.state = State.UNOBSERVED;
    }

    public void addOutcome(String outcome) {
        this.outcomes.add(outcome);
    }

    public boolean isEvidence() {
        if (evidence != null) {
            return true;
        }
        return false;
    }

    public boolean isFromChild() {
        return isFromChild;
    }

    public boolean isFromParent() {
        return isFromParent;
    }

    public boolean isRootNode() {
        return (parents.isEmpty());
    }

    public boolean isLeafNode() {
        return (children.isEmpty());
    }

    public void setTable(ArrayList<Double> table) {
        this.table = table;
    }

    public void setParents(ArrayList<VariableNode> parents) {
        this.parents = parents;
    }

    public void addChild(VariableNode vn) {
        this.children.add(vn);
    }

    public void setEvidence(String evidence) {
        evidence = evidence;
    }

    public String getName() {
        return name;
    }

    public ArrayList<VariableNode> getParents() {
        return parents;
    }

    public ArrayList<VariableNode> getChildren() {
        return children;
    }

    public ArrayList<Double> getTable() {
        return table;
    }

    public ArrayList<String> getOutcomes() {
        return outcomes;
    }

    public String toString() {
        String s = "{NAME: " + name + ", OUTCOME:";
        for (String value : outcomes) {
            s = s + " " + value;
        }
        if (!parents.isEmpty()) {
            s += ", GIVEN:";
            for (VariableNode parent : parents) {
                s += " " + parent.getName();
            }
            s += "";
        }
        s += ", TABLE:" + table + "}";
        return s;
    }
}
