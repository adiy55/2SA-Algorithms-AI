import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableEliminationAlgo implements NetworkAlgo {
    private HashMap<String, VariableNode> data; // pointer to network
    private String[] query; // query variable [0]; query output [1]
    private ArrayList<String> hidden; // hidden variables (parsed from the input)
    private ArrayList<CPT> factors; // // stores CPTs of previously eliminated variables
    private int nMul; // counts number of multiplications
    private int nAdd; // counts number of additions

    /**
     * Variable Elimination Algorithm constructor.
     *
     * @param data  Network
     * @param input Query (parsed from the text file)
     */
    public VariableEliminationAlgo(HashMap<String, VariableNode> data, String input) {
        this.data = data;
        this.factors = new ArrayList<>();
        this.hidden = new ArrayList<>();
        nMul = nAdd = 0;
        parseInput(input);
        filterEvidence();
        filterIndependentNodes();
    }

    /**
     * Calls the functions in the order needed to run the algorithm.
     *
     * @return Formatted string: probability; number of additions; number of multiplications
     */
    @Override
    public String RunAlgo() {
        String res = "";

        for (String s : hidden) { // eliminate hidden variables
            ArrayList<CPT> curr_factors = getFactors(s);
            CPT cpt_eliminated = eliminate(runJoin(curr_factors), s);
            factors.add(cpt_eliminated);
        }

        // join and normalize the query factor (if needed)
        ArrayList<CPT> query_factors = getFactors(query[0]);
        CPT result = normalize(runJoin((query_factors)));

        for (int i = 0; i < result.getRows().size(); i++) { // find the row the contains the query outcome
            if (result.getRows().get(i).get(query[0]).equals(query[1])) {
                res = String.format("%s,%d,%d", result.getRows().get(i).get("probability"), nAdd, nMul); // save result
                break;
            }
        }
        ResetAttributes();

        return res;
    }

    /**
     * Reset the variable attributes used in this algorithm.
     */
    @Override
    public void ResetAttributes() {
        for (VariableNode v : data.values()) { // reset nodes for next query
            v.setCPTUsed(false);
            v.initCPT();
        }

    }

    /**
     * Extracts the current query variables from the string given input.
     */
    private void parseInput(String input) {
        String[] s = input.split(" ");
        if (s.length > 1) {
            List<String> list = new ArrayList<>(Arrays.asList(s[1].split("-")));
            hidden = new ArrayList<>(list);
        }
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

    /**
     * Keeps the rows that match the outcome of the evidence nodes from the node itself, and it's children.
     */
    private void filterEvidence() {
        for (VariableNode v : data.values()) {
            if (v.isEvidence()) {
                rowFilter(v, v.getName(), v.getEvidence()); // call helper function
                for (VariableNode child : v.getChildren()) {
                    rowFilter(child, v.getName(), v.getEvidence()); // call helper function
                }
            }
        }
    }

    /**
     * Deletes rows that don't match the outcome and removes the evidence variable from rows that match the outcome.
     *
     * @param v            VariableNode
     * @param evidence_var Name of evidence variable
     * @param var_outcome  Outcome of evidence variable
     */
    private void rowFilter(VariableNode v, String evidence_var, String var_outcome) {
        HashSet<String> new_vars = new HashSet<>(v.getCPT().getVarNames());
        new_vars.remove(evidence_var);
        v.getCPT().setVarNames(new_vars);
        for (int i = v.getCPT().getRows().size() - 1; i >= 0; i--) {
            HashMap<String, String> currRow = v.getCPT().getRows().get(i);
            if (currRow.get(evidence_var).equals(var_outcome)) {
                currRow.remove(evidence_var);
            } else {
                v.getCPT().getRows().remove(i); // delete irrelevant hashmap
            }
        }
        if (v.getCPT().getRows().size() == 1) {
            v.getCPT().setRows(new ArrayList<>());
        }
    }

    /**
     * Finds nodes that are not ancestors of the query or evidence nodes.
     * For each hidden node:
     * 1. Checks if hidden node is not an ancestor.
     * 2. If it is an ancestor, uses Bayes Ball to check if the hidden node is independent of the query given the evidence.
     * Lastly, each CPT that contains a variable that suits one of the conditions above is marked as used.
     * It is irrelevant to the query and marking it as used will ensure the CPT will not be used during Variable Elimination.
     */
    private void filterIndependentNodes() { // any node that is not an ancestor of the query or evidence nodes is irrelevant
        HashSet<String> ancestors = new HashSet<>();
        for (VariableNode v : data.values()) { // find all ancestor nodes
            if (v.isEvidence() || v.getName().equals(query[0])) {
                ancestors.add(v.getName());
                Queue<VariableNode> queue = new LinkedList<>(v.getParents());
                while (!queue.isEmpty()) {
                    VariableNode curr = queue.poll();
                    ancestors.add(curr.getName());
                    if (!curr.getParents().isEmpty()) {
                        queue.addAll(curr.getParents());
                    }
                }
            }
        }
        HashSet<String> independent_nodes = new HashSet<>();
        for (String hidden_var : hidden) { // find independent hidden nodes
            if (!ancestors.contains(hidden_var)) { // check if hidden is not an ancestor
                independent_nodes.add(hidden_var);
            } else { // use Bayes Ball to check if ancestor is independent of the query
                String[] input = new String[]{query[0], hidden_var};
                BayesBallAlgo bba = new BayesBallAlgo(data, input);
                String res = bba.RunAlgo();
                if (res.equals("yes")) { // yes == independent
                    independent_nodes.add(hidden_var);
                }
            }
        }
        for (String node : independent_nodes) { // mark a CPT as used if it contains an independent node
            for (VariableNode v : data.values()) {
                if (v.getCPT().getVarNames().contains(node)) {
                    v.setCPTUsed(true);
                }
            }
        }
    }


    /**
     * Gets all factors that contain the hidden variable and sorts them.
     * Potential factors are: CPT of the hidden node, CPT of the hidden node's children, CPT of previously eliminated variables.
     *
     * @param hidden name of current hidden node
     * @return sorted ArrayList containing CPT objects of the relevant factors
     */
    private ArrayList<CPT> getFactors(String hidden) {
        ArrayList<CPT> curr_factors = new ArrayList<>();
        VariableNode v = data.get(hidden);
        if (!v.isCPTUsed()) { // add current hidden node cpt
            curr_factors.add(v.getCPT());
            v.setCPTUsed(true);
        }
        for (int i = 0; i < v.getChildren().size(); i++) { // add current node's children CPTs
            if (!v.getChildren().get(i).isCPTUsed()) {
                curr_factors.add(v.getChildren().get(i).getCPT());
                v.getChildren().get(i).setCPTUsed(true);
            }
        }
        for (int i = factors.size() - 1; i >= 0; i--) { // add cpt of previously eliminated variables if contain current target node
            if (factors.get(i).getVarNames().contains(hidden)) {
                curr_factors.add(factors.remove(i));
            }
        }
        curr_factors.sort(this::compare);
        return curr_factors;
    }

    /**
     * Function runs join by adding the joined result of the two smallest CPTs to the list, then sorting it.
     *
     * @param curr_factors List of CPT factors
     * @return Joined CPT object
     */
    private CPT runJoin(ArrayList<CPT> curr_factors) {
        while (curr_factors.size() > 1) {
            CPT res = join(curr_factors.remove(1), curr_factors.remove(0));
            curr_factors.add(res);
            curr_factors.sort(this::compare);
        }
        if (curr_factors.size() == 1) {
            return curr_factors.get(0);
        }
        return new CPT(); // edge case: return empty CPT if there is none (i.e. no factors were found)
    }

    /**
     * Compare function to sort CPT lists.
     * Sorts by ASCII value if the number of rows are equal, otherwise sorts by row size.
     *
     * @param cpt1 CPT object
     * @param cpt2 CPT object
     * @return 0 if x == y, a value less than 0 if x < y, a value greater than 0 if x > y
     */
    private int compare(CPT cpt1, CPT cpt2) {
        int diff = cpt1.getRows().size() - cpt2.getRows().size();
        if (diff == 0) {
            return Integer.compare(cpt1.getAsciiVal(), cpt2.getAsciiVal());
        } else {
            return Integer.compare(cpt1.getRows().size(), cpt2.getRows().size());
        }
    }

    /**
     * Joins two CPTs by the outcomes of their joint variables.
     * The probabilities of rows with matching outcomes are multiplied.
     * The result CPT size: [cpt1 size (the larger CPT)] * [multiplied number of outcomes of each unique variable is cpt2]
     *
     * @param cpt1 CPT object (cpt1 >= cpt2)
     * @param cpt2 CPT object (cpt2 <= cpt1)
     * @return Joined CPT
     */
    private CPT join(CPT cpt1, CPT cpt2) { // cpt1 >= cpt2
        HashSet<String> duplicates = getDuplicates(cpt1.getVarNames(), cpt2.getVarNames()); // call getDuplicates helper function
        CPT result_factor = new CPT();
        HashSet<String> res_var_names = new HashSet<>(); // combine variable names of both factors (HashSet does not contain duplicates)
        res_var_names.addAll(cpt1.getVarNames());
        res_var_names.addAll(cpt2.getVarNames());
        result_factor.setVarNames(res_var_names);
        for (int i = 0; i < cpt2.getRows().size(); i++) { // loop small CPT
            for (int j = 0; j < cpt1.getRows().size(); j++) { // loop large CPT
                HashMap<String, String> row2 = cpt2.getRows().get(i);
                HashMap<String, String> row1 = cpt1.getRows().get(j);
                if (isRowMatch(duplicates, row1, row2)) { // call isRowMatch helper function
                    HashMap<String, String> new_row = new HashMap<>();
                    double res = Double.parseDouble(row1.get("probability")) * Double.parseDouble(row2.get("probability"));
                    new_row.putAll(row1); // add all keys and values from rows
                    new_row.putAll(row2);
                    new_row.put("probability", res + ""); // insert new probability, overwrites previous value
                    nMul++;
                    result_factor.addRow(new_row);
                }
            }
        }
        return result_factor;
    }

    /**
     * Given two HashSets, finds matching variable names.
     *
     * @param hs1 variable names of cpt1
     * @param hs2 variable names of cpt2
     * @return HashSet of duplicate variable names
     */
    private HashSet<String> getDuplicates(HashSet<String> hs1, HashSet<String> hs2) {
        HashSet<String> duplicates = new HashSet<>();
        for (String variable : hs1) {
            if (hs2.contains(variable)) {
                duplicates.add(variable);
            }
        }
        return duplicates;
    }

    /**
     * Checks duplicate key values are equal.
     *
     * @param duplicates Joint variables of two CPTs
     * @param row1       a row from the larger CPT
     * @param row2       a row from the smaller CPT
     * @return if joint variables outcomes match in both rows
     */
    private boolean isRowMatch(HashSet<String> duplicates, HashMap<String, String> row1, HashMap<String, String> row2) {
        for (String variable : duplicates) {
            if (!row1.get(variable).equals(row2.get(variable))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculate the sum of probabilities for matching rows (without matching the outcome of the hidden variable),
     * then insert the new probabilities in new rows without the hidden variable.
     *
     * @param curr_factor CPT object
     * @param hidden      name of hidden variable
     * @return CPT object without hidden variable
     */
    private CPT eliminate(CPT curr_factor, String hidden) {
        Map<String, Double> mapped_probabilities = new HashMap<>(); // map for saving the new probabilities
        CPT new_factor = new CPT();
        HashSet<String> new_vars = new HashSet<>(curr_factor.getVarNames());
        new_vars.remove(hidden);
        new_factor.setVarNames(new_vars); // set new factor variable names without hidden
        for (int i = 0; i < curr_factor.getRows().size(); i++) {
            HashMap<String, String> curr_row = curr_factor.getRows().get(i);
            HashMap<String, String> new_row = new HashMap<>(curr_row);
            new_row.remove(hidden); // remove hidden variable from HashMap
            double probability = Double.parseDouble(new_row.remove("probability")); // parse probability of old row
            String curr_key = new_row.toString(); // use new_row toString method as key for mapped_probabilities
            if (mapped_probabilities.containsKey(curr_key)) { // add probability to current value in map
                double curr = mapped_probabilities.remove(curr_key);
                mapped_probabilities.put(curr_key, curr + probability);
                nAdd++;
            } else { // init row variables and outcomes that have not been seen yet
                mapped_probabilities.put(curr_key, probability);
                new_factor.addRow(new_row); // avoid adding duplicate rows
            }
        }
        for (HashMap<String, String> row : new_factor.getRows()) { // insert calculated probabilities in new factor rows
            row.put("probability", mapped_probabilities.get(row.toString()).toString());
        }
        return new_factor;
    }

    /**
     * Normalize probabilities of the query variable to the range [0,1].
     *
     * @param query_factor CPT containing only the query variable (after eliminating all hidden variables )
     * @return CPT with normalized probabilities
     */
    private CPT normalize(CPT query_factor) {
        if (nMul == 0) { // edge case: the query is a single cell
            return query_factor; // the CPT was not changed so it is already normalized
        }
        double probabilities_sum = Double.parseDouble(query_factor.getRows().get(0).get("probability"));
        for (int i = 1; i < query_factor.getRows().size(); i++) { // calculate probabilities sum
            probabilities_sum += Double.parseDouble(query_factor.getRows().get(i).get("probability"));
            nAdd++;
        }
        for (int i = 0; i < query_factor.getRows().size(); i++) { // normalize probabilities by dividing each probability by the total sum
            double normalized = Double.parseDouble(query_factor.getRows().get(i).get("probability")) / probabilities_sum;
            // round to 5 digits after the decimal point (HALF_UP rounds fractions >=0.5 up, otherwise down)
            BigDecimal res = new BigDecimal(normalized).setScale(5, RoundingMode.HALF_UP).stripTrailingZeros();
            query_factor.getRows().get(i).put("probability", res.toString()); // insert normalized probability
        }
        return query_factor;
    }

}