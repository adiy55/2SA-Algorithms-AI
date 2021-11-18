import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableEliminationAlgo {
    private HashMap<String, VariableNode> data;
    private String input;
    private String[] query;
//    private String[] evidence;
    private String[] hidden;

    //P(B=T|J=T,M=T) A-E

    public VariableEliminationAlgo(HashMap<String, VariableNode> data, String input) {
        this.data = data; // deep copy of data
        this.input = input;
        parseInput();
    }

    private void parseInput() {
        String[] s = input.split(" ");
        hidden = s[1].split("-");
        Pattern p = Pattern.compile("\\(([^P(]+)\\)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s[0]);
        String inside_parenthesis = "";
        while(m.find()){
            inside_parenthesis = m.group(1);
        }
        String[] query_evidence = inside_parenthesis.split("\\|");
        query = query_evidence[0].split("=");
        if (query_evidence.length > 1) {
            String[] given_nodes = query_evidence[1].split(",");
            for (String given_node : given_nodes) {
                String[] tmp = given_node.split("=");
                data.get(tmp[0]).setEvidence(tmp[1]);
            }
        }
    }
//
//    private void set_evidence() {
//        for (String s : evidence) {
//            String[] curr = s.split("=");
//            VariableNode curr_var = data.get(curr[0]);
//            curr_var.setEvidence(curr[1]);
////            update_tables(curr[0]);
//        }
//    }

//    private void update_tables(String var_name) { // var_name of evidence node
//        VariableNode v = data.get(var_name);
//        if (!v.isRootNode()) {
//            int outcome_index = -1;
//            for (int i = 0; i < v.getOutcomes().size(); i++) {
//                if (v.getOutcomes().get(i).equals(v.getEvidence())) { // find index of evidence in outcomes list
//                    outcome_index = i;
//                    break;
//                }
//            }
//            if (outcome_index > -1) {
//                ArrayList<Integer> res = get_table_indices(v, outcome_index);
//                ArrayList<Double> new_table = new ArrayList<>();
//                for (Integer re : res) {
//                    new_table.add(v.getTable().get(re));
//                }
//                v.setTable(new_table);
//            }
//        }
//    }


    /*
    variable node of table that needs to be updated
    name of node needed (current or parent)
    outcome string


    if current -> alternate outcomes

    if parent -> get parents, multiply by the number of outcomes until parent is found




     */

    public ArrayList<Integer> get_table_indices(VariableNode v, int outcome_index) { // v is the current node, outcome_index is the outcome needed
        ArrayList<Integer> res = new ArrayList<>();
        int div = 1;
        for (int i = 0; i < v.getParents().size(); i++) {
            VariableNode parent = v.getParents().get(i);
            div *= parent.getOutcomes().size();
            for (int j = 0; j < parent.getChildren().size(); j++) {
                if (parent.getChildren().get(j).getName().equals(v.getName())) {
                    break;
                }
                div *= parent.getOutcomes().size();
            }

            // need to get indices of current list too!
            int step = v.getTable().size() / div;
            int index = outcome_index * step; // start index of outcome in table
            while (index < v.getTable().size()) {
                res.add(index);
                index++;
                if (index % step == 0) {
                    index += (step * (v.getOutcomes().size() - 1));
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        xpathParser xp = new xpathParser("alarm_net.xml");
        String[] query = new String[]{"B=T"};
        String[] evidence = new String[]{"E=T", "M=T"};
        String[] hidden = new String[]{"A", "J"};
        String s= "P(B=T|J=T,M=T) A-E";
        VariableEliminationAlgo ve = new VariableEliminationAlgo(xp.getData(), s);
        System.out.println(ve.data);
    }

//    private void is_independent() {
//        VariableNode q = data.get(query[0]);
//        for (int i = 0; i < hidden.length; i += 2) {
//            VariableNode h = data.get(hidden[i]);
//            String result = new BayesBallAlgo(data, new String[]{q.getName(), h.getName()}).search();
//
//        }
//    }

    /*

    if variable is evidence -> don't need to use it as a cpt. go to children and update their tables (keep only the evidence outcome)

     */

/*

* todo: check variable elimination order (ascii)

input: query node name, queue with elimination order
output: answer (5 digits after decimal),num additions, num multiplications

if isEvidence == true:
    if isRootNode == true: remove factor (is only one value)
    else: check parent (given) nodes-
        if at least one parent is hidden: add table
        if all parents are evidence: remove factor (is only one value)

    while there are still hidden variables (not Q or evidence):
        take the first name out of the queue
        join all factors containing curr name
        eliminate (sum out) curr
        if factor is one value -> remove

    join all remaining factors
    normalize


how to find index of num in table:
- find num of outputs until given variable (multiply them -> m = ans)
- step = table size / m
step will determine where each output starts and ends (<= len(table))
- multiply according to variable outcome

insert table to list and continue

*** use hashmap?? <String (name), int (step)>

 */

}
