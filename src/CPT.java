import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CPT {
    private ArrayList<HashMap<String, String>> rows;
    private HashSet<String> varNames;
    private int asciiVal;

    public CPT(ArrayList<Double> table, ArrayList<VariableNode> variableNodes) {
        this.varNames = new HashSet<>();
        this.asciiVal = 0;
        this.rows = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) {
            rows.add(new HashMap<>());
        }
        initRows(variableNodes, table);
        initVars(variableNodes);
    }

    public CPT() {
        varNames = new HashSet<>();
        asciiVal = 0;
        rows = new ArrayList<>();
    }

    public void addRow(HashMap<String, String> new_row) {
        rows.add(new_row);
    }

    public void setVarNames(HashSet<String> varNames) {
        this.varNames = varNames;
        calcAsciiVal();
    }

    private void initRows(ArrayList<VariableNode> variableNodes, ArrayList<Double> table) {
        int div = 1, step;
        for (VariableNode v : variableNodes) {
            int outcome_index = 0;
            div *= v.getOutcomes().size();
            step = table.size() / div;
            for (int j = 0; j < table.size(); j++) {
                if (j > 0 && j % step == 0) {
                    outcome_index++;
                }
                if (outcome_index >= v.getOutcomes().size()) {
                    outcome_index = 0;
                }
                rows.get(j).put(v.getName(), v.getOutcomes().get(outcome_index));
            }
        }
        for (int i = 0; i < table.size(); i++) {
            rows.get(i).put("P", table.get(i).toString());
        }
    }

    private void initVars(ArrayList<VariableNode> variableNodes) {
        for (VariableNode v : variableNodes) {
            varNames.add(v.getName());
            asciiVal += nameAsAscii(v.getName());
        }
    }

    public void calcAsciiVal() {
        asciiVal = 0;
        varNames.stream().iterator().forEachRemaining(s -> asciiVal += nameAsAscii(s));
    }

    public int nameAsAscii(String var_name) { // return sum of ascii values of variable name
        char[] name = var_name.toCharArray();
        int ascii_value = 0;
        for (char c : name) {
            ascii_value += c;
        }
        return ascii_value;
    }

    public void setAsciiVal(int asciiVal) {
        this.asciiVal = asciiVal;
    }

    public int getAsciiVal() {
        return asciiVal;
    }

    public ArrayList<HashMap<String, String>> getRows() {
        return rows;
    }

    public HashSet<String> getVarNames() {
        return varNames;
    }
}

    /*
    - organize order of cpts according to table size (in queue?)
    - check if both cpts include the same variable
    -
     */

    /*
    save cpt only with relevant rows (according to evidence)
    if node is evidence -> alternate outcome (according to index in outcomes list)

     */

