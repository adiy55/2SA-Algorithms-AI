import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableEliminationAlgo implements NetworkAlgo {
    private HashMap<String, VariableNode> data;
    private String input;
    private String[] query;
    private String[] hidden;

    public VariableEliminationAlgo(HashMap<String, VariableNode> data, String input) {
        this.data = data;
        this.input = input;
        parseInput();
//        filterEvidence();
    }

    @Override
    public String RunAlgo() {

        return null;
    }

    private void parseInput() {
        String[] s = input.split(" ");
        hidden = s[1].split("-");
        Pattern p = Pattern.compile("\\(([^P(]+)\\)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s[0]);
        String inside_parenthesis = "";
        while (m.find()) {
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

    private void filterEvidence() {
        for (VariableNode v : data.values()) {
            if (v.isEvidence()) {
                for (int i = 0; i < v.getCpt().getRows().size(); i++) {
                    HashMap<String, String> currRow = v.getCpt().getRows().get(i);
                    if (currRow.containsKey(v.getName()) && currRow.get(v.getName()).equals(v.getEvidence())) {
                        currRow.remove(v.getName());
                        v.getCpt().getVarNames().remove(v.getName());
                    }
                    System.out.println(currRow.keySet());
//                    else {
//                        for (String key : currRow.get(i)) {
//                            currRow.remove(key);
//                        }
//                    }
//                    System.out.println(newRow);
                }
            }
        }
    }

    private void join(CPT cpt1, CPT cpt2, String hidden) {
        Queue<HashMap<String, String>> factors = new LinkedList<>();
        for (VariableNode v : data.values()) {
            if (v.getCpt().getVarNames().contains(hidden)) {

            }
        }
    }

    public static void main(String[] args) {
        Network net = new Network("input.txt");
        String s = "P(B=T|J=T,M=T) A-E";
        VariableEliminationAlgo ve = new VariableEliminationAlgo(net.getNet(), s);
        for (VariableNode v : net.getNet().values()) {
            System.out.println(v.getCpt().getVarNames() + " " + v.getCpt().getRows());
        }
    }


}

//    private ArrayList<Double> join(CPT cpt1, CPT cpt2, String hidden) { // cpt1.table.size <= cpt2.table.size
//        // find step for the hidden variable in each cpt table
//        int count1 = calcStepSize(cpt1, hidden), count2 = calcStepSize(cpt2, hidden);
////        count1 = count2 = data.get(hidden).getOutcomes().size();
////        for (int i = 0; i < cpt1.getVar_names().indexOf(hidden); i++) {
////            count1 *= data.get(cpt1.getVar_names().get(i)).getOutcomes().size();
////        }
////        for (int i = 0; i < cpt2.getVar_names().indexOf(hidden); i++) {
////            count2 *= data.get(cpt2.getVar_names().get(i)).getOutcomes().size();
////        }
//        // multiply according to the alternations in the step size
//        int table1 = cpt1.getTable().size();
//        int table2 = cpt2.getTable().size();
//        int step1 = table1 / count1;
//        int step2 = table2 / count2;
//        int counter = 0;
//        ArrayList<Double> new_table = new ArrayList<>();
//        for (int i = 0; i < table2; i++) {
//            if (i > 0 && i % step2 == 0) {
//                counter += step1;
//                if (counter >= table1) {
//                    counter = 0;
//                }
//            }
//            double res = cpt2.getTable().get(i) * cpt1.getTable().get(counter);
//            BigDecimal bd = new BigDecimal(res);
//            bd = bd.setScale(5, RoundingMode.HALF_UP);
//            new_table.add(bd.doubleValue());
//        }
//        return new_table;
//    }
//
//    private ArrayList<Double> eliminate(CPT cpt, String hidden, boolean nextIsQ) {
////        int index_hidden = cpt.getVar_names().indexOf(hidden);
//        int step;
//        if (nextIsQ) {
//            step = cpt.getTable().size() / calcStepSize(cpt, "E");
//        } else {
//            int index = 0;
//            for (int i = 0; i < cpt.getVar_names().size() - 1; i++) {
//                if (cpt.getVar_names().get(i + 1).equals(hidden)) {
//                    index = i;
//                    break;
//                }
//            }
//            step = cpt.getTable().size() / calcStepSize(cpt, cpt.getVar_names().get(index));
//        }
//
//        ArrayList<Double> res = new ArrayList<>();
//        double sum = 0;
//        for (int i = 0; i < cpt.getTable().size(); i++) {
//            sum += cpt.getTable().get(i);
//            if (i > 0 && (i + 1) % step == 0) {
//                BigDecimal bd = new BigDecimal(sum);
//                bd = bd.setScale(5, RoundingMode.HALF_UP);
//                res.add(bd.doubleValue());
//                sum = 0;
//            }
//        }
//        return res;
//    }


    /*
    variable node of table that needs to be updated
    name of node needed (current or parent)
    outcome string


    if current -> alternate outcomes

    if parent -> get parents, multiply by the number of outcomes until parent is found





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

