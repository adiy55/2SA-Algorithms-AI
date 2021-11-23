import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableEliminationAlgo implements NetworkAlgo {
    private HashMap<String, VariableNode> data;
    private String input;
    private String[] query;
    private ArrayList<String> hidden;
    private ArrayList<CPT> factors;
    private int nMul;
    private int nAdd;

    public VariableEliminationAlgo(HashMap<String, VariableNode> data, String input) {
        this.data = data;
        this.input = input;
        this.factors = new ArrayList<>();
        this.hidden = new ArrayList<>();
        nMul = nAdd = 0;
        parseInput();
        filterEvidence();
    }

    @Override
    public String RunAlgo() {
        for (int i = hidden.size() - 1; i >= 0; i--) {
            String[] input = new String[]{query[0], hidden.get(i)};
            BayesBallAlgo bba = new BayesBallAlgo(data, input);
            String res = bba.RunAlgo();
            if (res.equals("yes")) {
                data.get(hidden.get(i)).setCPTUsed(true);
                hidden.remove(i);
            }
        }
        for (String s : hidden) {
            ArrayList<CPT> curr_factors = getFactors(s);
            join_eliminate_normalize(curr_factors, s, false);
        }
        ArrayList<CPT> query_factors = getFactors(query[0]);
        join_eliminate_normalize(query_factors, query[0], true);
        for (int i = 0; i < factors.get(0).getRows().size(); i++) {
            if (factors.get(0).getRows().get(i).get(query[0]).equals(query[1])) {
                String res = factors.get(0).getRows().get(i).get("P") + "," + nAdd + "," + nMul;
                return res;
            }
        }
        return "not found";
    }

    private void parseInput() {
        String[] s = input.split(" ");
        hidden = new ArrayList<>(Arrays.stream(s[1].split("-")).toList());
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
                rowFilter(v, v.getName(), v.getEvidence());
                for (VariableNode child : v.getChildren()) {
                    rowFilter(child, v.getName(), v.getEvidence());
                }
            }
        }
    }

    private void rowFilter(VariableNode v, String evidence_var, String var_outcome) {
        v.getCPT().getVarNames().remove(evidence_var);
        v.getCPT().calcAsciiVal();
        for (int i = v.getCPT().getRows().size() - 1; i >= 0; i--) {
            HashMap<String, String> currRow = v.getCPT().getRows().get(i);
            if (currRow.get(evidence_var).equals(var_outcome)) {
                currRow.remove(evidence_var);
            } else {
                v.getCPT().getRows().remove(i); // delete irrelevant hashmap
            }
            if (currRow.size() == 1) {
                currRow.clear();
            }
        }
    }

    private ArrayList<CPT> getFactors(String hidden) {
        ArrayList<CPT> curr_factors = new ArrayList<>();
        VariableNode v = data.get(hidden);
        if (!v.isCPTUsed()) { // add current hidden node cpt
            curr_factors.add(v.getCPT());
            v.setCPTUsed(true);
        }
        for (int i = 0; i < v.getChildren().size(); i++) { // add current nodes children cpts
            if (!v.getChildren().get(i).isCPTUsed()) {
                curr_factors.add(v.getChildren().get(i).getCPT());
                v.getChildren().get(i).setCPTUsed(true);
            }
        }
        for (int i = 0; i < factors.size(); i++) { // add cpt results if contain current target node
            if (factors.get(i).getVarNames().contains(hidden)) {
                curr_factors.add(factors.remove(i));
            }
        }
        curr_factors.sort(this::compare);
//        System.out.println("Hidden: " + hidden);
//        for (CPT cpt : curr_factors) {
//            for (int i = 0; i < cpt.getRows().size(); i++) {
//                System.out.println(cpt.getRows().get(i));
//            }
//        }
//        System.out.println("-----");
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

    private CPT join(CPT cpt1, CPT cpt2) { // cpt1 > cpt2
        HashSet<String> dups = getDuplicates(cpt1.getVarNames(), cpt2.getVarNames());
        CPT result_factor = new CPT();
        HashSet<String> new_var_names = new HashSet<>();
        new_var_names.addAll(cpt1.getVarNames());
        new_var_names.addAll(cpt2.getVarNames());
        result_factor.setVarNames(new_var_names);
        for (int i = 0; i < cpt2.getRows().size(); i++) {
            for (int j = 0; j < cpt1.getRows().size(); j++) {
                HashMap<String, String> row1 = cpt2.getRows().get(i);
                HashMap<String, String> row2 = cpt1.getRows().get(j);
                HashMap<String, String> new_row = new HashMap<>();
                for (String variable : dups) {
                    if (row1.get(variable).equals(row2.get(variable))) {
                        new_row.putAll(row1);
                        new_row.putAll(row2);
                        BigDecimal bd1 = new BigDecimal(row1.get("P"));
                        BigDecimal bd2 = new BigDecimal(row2.get("P"));
                        new_row.put("P", bd1.multiply(bd2).toString());
                        nMul++;
                        result_factor.addRow(new_row);
                    }
                }
            }
        }
        return result_factor;
    }

    private void join_eliminate_normalize(ArrayList<CPT> curr_factors, String hidden, boolean is_query) {
        while (curr_factors.size() > 1) {
            CPT res = join(curr_factors.remove(1), curr_factors.remove(0));
            curr_factors.add(res);
            curr_factors.sort(this::compare);
        }
        if (curr_factors.size() == 1 && !is_query) {
            factors.add(eliminate(curr_factors.remove(0), hidden));
        } else if (curr_factors.size() == 1) {
            factors.add(0, normalize(curr_factors.remove(0)));
        }
    }

    private CPT normalize(CPT query_factor) {
        double rows_sum = Double.parseDouble(query_factor.getRows().get(0).get("P"));
        for (int i = 1; i < query_factor.getRows().size(); i++) {
            rows_sum += Double.parseDouble(query_factor.getRows().get(i).get("P"));
            nAdd++;
        }
        for (int i = 0; i < query_factor.getRows().size(); i++) {
            double normalized = Double.parseDouble(query_factor.getRows().get(i).get("P")) / rows_sum;
            BigDecimal bd = new BigDecimal(normalized).setScale(5, RoundingMode.HALF_UP);
            query_factor.getRows().get(i).put("P", bd.toString());
        }
        return query_factor;
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

    private CPT eliminate(CPT curr_factor, String hidden) {
        Map<String, Double> mapped_probabilities = new HashMap<>();
        CPT new_factor = new CPT();
        curr_factor.getVarNames().remove(hidden);
        new_factor.setVarNames(curr_factor.getVarNames());
        for (int i = 0; i < curr_factor.getRows().size(); i++) {
            HashMap<String, String> curr_row = curr_factor.getRows().get(i);
            curr_row.remove(hidden);
            double probability = Double.parseDouble(curr_row.remove("P"));
            String curr_key = curr_row.toString();
            if (mapped_probabilities.containsKey(curr_key)) {
                mapped_probabilities.put(curr_key, mapped_probabilities.get(curr_key) + probability);
                nAdd++;
            } else {
                mapped_probabilities.put(curr_key, probability);
                new_factor.addRow(curr_row);
            }
        }
        for (HashMap<String, String> row : new_factor.getRows()) {
            row.put("P", mapped_probabilities.get(row.toString()).toString());
        }
        return new_factor;
    }

    // hidden outcomes == number of rows to add
    // find rows with the same value according to the variables

    public static void main(String[] args) {
        Network net = new Network("input.txt");
        String s = "P(B=T|J=T,M=T) A-E";
        VariableEliminationAlgo ve = new VariableEliminationAlgo(net.getNet(), s);
//        for (VariableNode v : net.getNet().values()) {
//            System.out.println(v.getCPT().getVarNames() + " " + v.getCPT().getRows());
//        }
//        System.out.println(ve.RunAlgo());
        s = "P(J=T|B=T) A-E-M";
        net.resetVariables();
        ve = new VariableEliminationAlgo(net.getNet(), s);
        System.out.println(ve.RunAlgo());
    }

}


//how to find index of num in table:
//- find num of outputs until given variable (multiply them -> m = ans)
//- step = table size / m
//step will determine where each output starts and ends (<= len(table))
//- multiply according to variable outcome


// todo: test functions (input parsers), output to text file, documentation