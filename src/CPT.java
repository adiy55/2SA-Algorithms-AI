import java.util.ArrayList;

public class CPT {
    private String name; // name of node used
    private ArrayList<Double> table;
    private ArrayList<String> var_names;

    public CPT(VariableNode v) {
        this.name = v.getName();
        if (v.isEvidence()) {
            table_filter(v);
        }
        this.var_names = new ArrayList<>();
        for (int i = 0; i < v.getParents().size(); i++) {
            var_names.add(v.getParents().get(i).getName());
        }
        var_names.add(this.name);

    }

    // check if node is evidence / if children are evidence and remove from table

    private void table_filter(VariableNode v) {
        ArrayList<Double> curr_table = v.getTable();
        ArrayList<VariableNode> curr_children = v.getChildren(); // need to remove from children tables
        for (VariableNode child : curr_children) {
            if (child.isEvidence()) {

            }
        }

    }

//    private int find_outcome_index(VariableNode v) {
//        int outcome_index = -1;
//        for (int i = 0; i < v.getOutcomes().size(); i++) {
//
//        }
//    }

    /*
    save cpt only with relevant rows (according to evidence)
    if node is evidence -> alternate outcome (according to index in outcomes list)

     */
}
