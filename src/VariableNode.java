import java.util.ArrayList;

public class VariableNode {

    private String name; // variable name
    private ArrayList<String> outcomes; // variable outcomes
    private String evidence; // for query
    private ArrayList<VariableNode> parents; // GIVEN
    private ArrayList<VariableNode> children;
    private ArrayList<Double> table;
    private boolean isFromChild;
    private boolean isFromParent;
    private CPT cpt;

    public VariableNode(String name, ArrayList<String> outcomes) {
        this.name = name;
        this.outcomes = outcomes;
        evidence = null;
        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();
        this.table = null;
        isFromChild = isFromParent = false;
        this.cpt = null;
    }

    public CPT getCpt() {
        return cpt;
    }

    public void setCpt(CPT cpt) {
        this.cpt = cpt;
    }

    public boolean isRootNode() {
        return (parents.isEmpty());
    }

    public String getEvidence() {
        return evidence;
    }

    public boolean isEvidence() {
        return this.evidence != null;
    }

    public boolean isFromChild() {
        return isFromChild;
    }

    public boolean isFromParent() {
        return isFromParent;
    }

    public void setFromChild(boolean fromChild) {
        isFromChild = fromChild;
    }

    public void setFromParent(boolean fromParent) {
        isFromParent = fromParent;
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
        this.evidence = evidence;
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

    public int getEvidenceIndex() {
        return outcomes.indexOf(evidence);
    }

    public void initCPT() {
        ArrayList<VariableNode> variableNodes = new ArrayList<>(parents);
        variableNodes.add(this);
        cpt = new CPT(table, variableNodes);
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
