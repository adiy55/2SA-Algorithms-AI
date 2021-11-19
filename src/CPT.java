import java.util.ArrayList;

public class CPT {
    private ArrayList<Double> table;
    private ArrayList<String> var_names;

    public CPT(VariableNode v) {
        this.var_names = new ArrayList<>();
        this.table = new ArrayList<>();
        for (int i = 0; i < v.getParents().size(); i++) {
            var_names.add(v.getParents().get(i).getName());
        }
        if (v.isEvidence()) {
            table_filter(v);
        } else {
            table.addAll(v.getTable());
            var_names.add(v.getName()); // name of node used
        }
    }

    // check if node is evidence / if children are evidence and remove from table

    private void table_filter(VariableNode v) { // filters evidence nodes table
        int start_index = v.getEvidenceIndex();
        ArrayList<Double> new_table = new ArrayList<>();
        for (int i = start_index; i < v.getTable().size(); i += v.getOutcomes().size()) {
            new_table.add(v.getTable().get(i));
        }
        if (new_table.size() > 1) {
            table = new_table;
        }
    }

    public ArrayList<String> getVar_names() {
        return var_names;
    }

    public void setVar_names(ArrayList<String> var_names) {
        this.var_names = var_names;
    }

    public ArrayList<Double> getTable() {
        return table;
    }

    public void setTable(ArrayList<Double> table) {
        this.table = table;
    }
}

    /*
    - organize order of cpts according to table size (in queue?)
    - check if both cpts include the same variable
    -
     */

    /*
    count1 = count2 = 1
    CPT new_cpt = new CPT()
    for i=0; i<cpt1.var_names; i++
        count1 *= data.var_names(i)
        for j=0; j<cpt2.var_names; j++
            count2 *= data.var_names(j)
            step1 = count1
            step2 = count2
            if cpt1.var_names.get(i) == cpt2.var_names.get(j)
                -----JOIN

                table.add(cpt1.table.get(step1) * cpt2.table.get(step2))
                step1 += count1
                step2 += count2
            else
                for k=0; k<cpt1.var_names.get(i).outcomes.len; k++
                    table.add(cpt1.table.get(step1) * cpt2.table.get(step2))
                    step1 += count1
                    step2 += count2



    count1++
    count2++
     */

//    private void table_filter(VariableNode v) {
//        ArrayList<Double> curr_table = v.getTable();
//        ArrayList<VariableNode> curr_children = v.getChildren(); // need to remove from children tables
//        for (VariableNode child : curr_children) {
//            if (child.isEvidence()) {
//
//
//            }
//        }
//
//    }

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

