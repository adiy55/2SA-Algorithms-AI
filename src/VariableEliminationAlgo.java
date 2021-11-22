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

//how to find index of num in table:
//- find num of outputs until given variable (multiply them -> m = ans)
//- step = table size / m
//step will determine where each output starts and ends (<= len(table))
//- multiply according to variable outcome


