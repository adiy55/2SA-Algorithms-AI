import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CPT {
    private ArrayList<HashMap<String, String>> rows;
    private HashSet<String> varNames;

    public CPT(ArrayList<Double> table, ArrayList<VariableNode> variableNodes) {
        this.varNames =  new HashSet<>();
        this.rows = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) {
            rows.add(new HashMap<>());
        }
        initRows(variableNodes, table);
        initVarNames(variableNodes);
    }

    private void initRows(ArrayList<VariableNode> variableNodes, ArrayList<Double> table) {
        int div = 1, step;
        for (VariableNode variableNode : variableNodes) {
            int n_outcome = 0;
            div *= variableNode.getOutcomes().size();
            step = table.size() / div;
            for (int j = 0; j < table.size(); j++) {
                if (j > 0 && j % step == 0) {
                    n_outcome++;
                }
                if (n_outcome >= variableNode.getOutcomes().size()) {
                    n_outcome = 0;
                }
                rows.get(j).put(variableNode.getName(), variableNode.getOutcomes().get(n_outcome));
            }
        }
        for (int i = 0; i < table.size(); i++) {
            rows.get(i).put("P", table.get(i).toString());
        }
    }

    private void initVarNames(ArrayList<VariableNode> variableNodes) {
        for (VariableNode v : variableNodes) {
            varNames.add(v.getName());
        }
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

