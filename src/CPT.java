import java.util.ArrayList;
import java.util.HashMap;

public class CPT {
    private ArrayList<HashMap<String, String>> rows;

    public CPT(ArrayList<Double> table, ArrayList<VariableNode> variableNodes) {
        this.rows = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) {
            rows.add(new HashMap<>());
        }
        initRows(variableNodes, table);
        System.out.println(rows);
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

    public static void main(String[] args) {
        Network net = new Network("input.txt");
        System.out.println(net);

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

