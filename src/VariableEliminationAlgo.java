import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableEliminationAlgo implements NetworkAlgo {
    private HashMap<String, VariableNode> data;
    private String input;
    private String[] query;
    private String[] hidden;
    private ArrayList<CPT> factors;

    public VariableEliminationAlgo(HashMap<String, VariableNode> data, String input) {
        this.data = data;
        this.input = input;
        this.factors = new ArrayList<>();
        parseInput();
        filterEvidence();
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
                int new_ascii_val = v.getCpt().getAsciiVal() - v.getCpt().nameAsAscii(v.getName());
                v.getCpt().setAsciiVal(new_ascii_val);
                v.getCpt().getVarNames().remove(v.getName());
                for (int i = v.getCpt().getRows().size() - 1; i >= 0; i--) {
                    HashMap<String, String> currRow = v.getCpt().getRows().get(i);
                    if (currRow.get(v.getName()).equals(v.getEvidence())) {
                        currRow.remove(v.getName());
                    } else {
                        v.getCpt().getRows().remove(i); // delete irrelevant hashmap
                    }
                    if (currRow.size() == 1) {
                        currRow.clear();
                    }
                }
            }
        }
    }

    private ArrayList<CPT> getFactors(String hidden) {
        ArrayList<CPT> curr_factors = new ArrayList<>();
        VariableNode v = data.get(hidden);
        if (v.isEliminated()) { // add current hidden node cpt
            curr_factors.add(v.getCpt());
            v.setEliminated(true);
        }
        for (int i = 0; i < v.getChildren().size(); i++) { // add current nodes children cpts
            if (v.getChildren().get(i).isEliminated()) {
                curr_factors.add(v.getChildren().get(i).getCpt());
                v.getChildren().get(i).setEliminated(true);
            }
        }
        for (int i = 0; i < factors.size(); i++) { // add cpt results if contain current target node
            if (factors.get(i).getVarNames().contains(hidden)) {
                curr_factors.add(factors.remove(i));
            }
        }
        curr_factors.sort(this::compare);
        return curr_factors;
    }

    public int compare(CPT cpt1, CPT cpt2) {
        int diff = cpt1.getRows().size() - cpt2.getRows().size();
        if (diff == 0) {
            return Integer.compare(cpt1.getAsciiVal(), cpt2.getAsciiVal());
        } else {
            return Integer.compare(cpt1.getRows().size(), cpt2.getRows().size());
        }
    }

    public CPT join(CPT cpt1, CPT cpt2) { // cpt1 < cpt2
        HashSet<String> dups = getDuplicates(cpt1.getVarNames(), cpt2.getVarNames());
        CPT result_factor = new CPT();
        result_factor.setVarNames(dups);
        for (int i = 0; i < cpt1.getRows().size(); i++) {
            for (int j = 0; j < cpt2.getRows().size(); j++) {
                HashMap<String, String> row1 = cpt1.getRows().get(i);
                HashMap<String, String> row2 = cpt2.getRows().get(j);
                HashMap<String, String> new_row = new HashMap<>();
                for (String variable : dups) {
                    if (row1.get(variable).equals(row2.get(variable))) {
                        new_row.putAll(row1);
                        new_row.putAll(row2);
                        double mul = Double.parseDouble(row1.get("P")) * Double.parseDouble(row2.get("P"));
                        BigDecimal bd = new BigDecimal(mul).setScale(5, RoundingMode.HALF_UP);
                        new_row.put("P", bd.toString());
                        result_factor.addRow(new_row);
                    }
                }
            }
        }
        return result_factor;
    }


    private HashSet<String> getDuplicates(HashSet<String> hs1, HashSet<String> hs2) {
        HashSet<String> dups = new HashSet<>();
        for (String variable : hs1) {
            if (hs2.contains(variable)) {
                dups.add(variable);
            }
        }
        return dups;
    }


    public static void main(String[] args) {
        Network net = new Network("input.txt");
        String s = "P(B=T|J=T,M=T) A-E";
        VariableEliminationAlgo ve = new VariableEliminationAlgo(net.getNet(), s);
        for (VariableNode v : net.getNet().values()) {
            System.out.println(v.getCpt().getVarNames() + " " + v.getCpt().getRows());
        }
        ArrayList<CPT> res = ve.getFactors("A");
        for (CPT re : res) {
            System.out.println(re.getRows());
            System.out.println();
        }
        CPT c = ve.join(res.get(0), res.get(1));
        for (int i = 0; i < c.getRows().size(); i++) {
            System.out.println(c.getRows().get(i));
        }


    }


}

// small / large
// a b //


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

