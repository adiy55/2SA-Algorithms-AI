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
        this.name = name; // node name
        this.outcomes = outcomes; // T,F / v1,v2,v2 (according to xml)
        evidence = null; // String that contains the given outcome, if unknown remains null
        this.parents = new ArrayList<>(); // list of parent nodes (for node, given == parent nodes)
        this.children = new ArrayList<>(); // list of children nodes (given == parent node of current child node)
        this.table = null; // probabilities
        isFromChild = isFromParent = false; // for BayesBall algorithm
        this.cpt = null; // constructor will be called when parsing definition / when initCPT method is called
        this.isCPTUsed = false; // to mark if cpt has been used for variable elimination
    }

    /**
     * Initializes CPT object for each node.
     * First initialization occurs after a node definition is parsed.
     * The next initializations are called after each Variable Elimination query.
     */
    public void initCPT() {
        ArrayList<VariableNode> variableNodes = new ArrayList<>(parents); // add all parents to new arraylist
        variableNodes.add(this); // add current node to end of arraylist
        cpt = new CPT(table, variableNodes); // call cpt constructor
    }

    /**
     * Mark if a CPT object was taken during the Variable Elimination algorithm.
     * This is to ensure that each CPT is used once.
     *
     * @param CPTUsed boolean
     */
    public void setCPTUsed(boolean CPTUsed) {
        isCPTUsed = CPTUsed;
    }

    /**
     * Returns the value of the attribute isCPTUsed.
     * When a CPT is used, it means that there is a factor that already contains the information.
     *
     * @return true if CPT was already taken
     */
    public boolean isCPTUsed() {
        return isCPTUsed;
    }

    /**
     * @return CPT object
     */
    public CPT getCPT() {
        return cpt;
    }

    /**
     * @return String outcome of the node, if the node is not evidence returns null
     */
    public String getEvidence() {
        return evidence;
    }

    /**
     * @param evidence outcome of the node in the current query
     */
    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    /**
     * @return true if evidence is not null
     */
    public boolean isEvidence() {
        return this.evidence != null;
    }

    /**
     * Attribute for the Bayes Ball algorithm.
     *
     * @return true if node was added from a child
     */
    public boolean isFromChild() {
        return isFromChild;
    }

    /**
     * Attribute for the Bayes Ball algorithm.
     *
     * @return true if node was added from a parent
     */
    public boolean isFromParent() {
        return isFromParent;
    }

    /**
     * @param fromChild true if a parent node was added from a child node
     */
    public void setFromChild(boolean fromChild) {
        isFromChild = fromChild;
    }

    /**
     * @param fromParent true if a child node was added from a parent node
     */
    public void setFromParent(boolean fromParent) {
        isFromParent = fromParent;
    }

    /**
     * Used to set the probabilities of each node when the XML definitions are parsed.
     *
     * @param table ArrayList containing the probabilities of the node
     */
    public void setTable(ArrayList<Double> table) {
        this.table = table;
    }

    /**
     * Used to set the parents (marked as GIVEN in the XML file) of the node when the definition is parsed.
     *
     * @param parents ArrayList containing pointers to the parent nodes
     */
    public void setParents(ArrayList<VariableNode> parents) {
        this.parents = parents;
    }

    /**
     * Used to add a child node when a parent is being parsed (a node marked as FOR is added as a child if it has nodes
     * marked as GIVEN in the XML file definition).
     *
     * @param vn pointer to child node
     */
    public void addChild(VariableNode vn) {
        this.children.add(vn);
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

    /**
     * @return string containing the node's information parsed from the XML
     */
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
        }
        s += ", TABLE:" + table + "}";
        return s;
    }
}
