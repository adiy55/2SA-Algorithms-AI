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
    private boolean isCPTUsed;

    public VariableNode(String name, ArrayList<String> outcomes) {
        this.name = name;
        this.outcomes = outcomes; // T,F / v1,v2,v2 (according to xml)
        evidence = null; // String that contains the given outcome, if unknown remains null
        this.parents = new ArrayList<>(); // list of parent nodes (for node, given == parent nodes)
        this.children = new ArrayList<>(); // list of children nodes (given == parent node of current child node)
        this.table = null; // probabilities
        isFromChild = isFromParent = false; // for BayesBall algorithm
        this.cpt = null; // constructor will be called when parsing definition / when initCPT method is called
        this.isCPTUsed = false; // to mark if cpt has been used for variable elimination
    }

    public void setCPTUsed(boolean CPTUsed) {
        isCPTUsed = CPTUsed;
    }

    public boolean isCPTUsed() {
        return isCPTUsed;
    }

    public CPT getCPT() {
        return cpt;
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

    public void initCPT() {
        ArrayList<VariableNode> variableNodes = new ArrayList<>(parents); // add all parents to new arraylist
        variableNodes.add(this); // add current node to end of arraylist
        cpt = new CPT(table, variableNodes); // call cpt constructor
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
