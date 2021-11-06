import java.util.ArrayList;

public class VariableNode {
    private String name; // variable name
    private ArrayList<String> outcomes; // variable outcomes
    private boolean isEvidence; // boolean for query
    private ArrayList<VariableNode> parents; // GIVEN
    private ArrayList<VariableNode> children;
    private ArrayList<Double> table;

    public VariableNode(String name, ArrayList<String> outcomes) {
        this.name = name;
        this.outcomes = outcomes;
        isEvidence = false;
        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();
        this.table = null;
    }

    public boolean isEvidence() {
        return isEvidence;
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

    public void setEvidence(boolean evidence) {
        isEvidence = evidence;
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
        String s = "\n{NAME: " + name + ", OUTCOME:";
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
