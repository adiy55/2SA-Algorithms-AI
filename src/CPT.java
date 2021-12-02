import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CPT {
    private ArrayList<HashMap<String, String>> rows; // each HashMap is a row in the factor (contains one probability and the variable outcomes)
    private HashSet<String> varNames; // variable names (in the CPT)
    private int asciiVal; // sum of ascii values of the variable names in the CPT

    /**
     * CPT constructor.
     *
     * @param table         probabilities of the current node
     * @param variableNodes list of nodes in the CPT (the order is crucial for matching the probabilities to the correct outcomes!)
     */
    public CPT(ArrayList<Double> table, ArrayList<VariableNode> variableNodes) {
        this.varNames = new HashSet<>();
        this.asciiVal = 0;
        this.rows = new ArrayList<>();
        for (int i = 0; i < table.size(); i++) { // initialize the HashMaps according to the number of probabilities
            rows.add(new HashMap<>());
        }
        initRows(variableNodes, table); // call helper function
        initVars(variableNodes); // call helper function
    }

    /**
     * Empty CPT constructor.
     * Used in the Variable Elimination algorithm to return an empty factor.
     */
    public CPT() {
        varNames = new HashSet<>();
        asciiVal = 0;
        rows = new ArrayList<>();
    }

    /**
     * @param rows ArrayList of HashMaps containing the new rows
     */
    public void setRows(ArrayList<HashMap<String, String>> rows) {
        this.rows = rows;
    }

    /**
     * @param new_row HashMap containing the information of a single row
     */
    public void addRow(HashMap<String, String> new_row) {
        rows.add(new_row); // appends the new row to the end of the ArrayList
    }

    /**
     * Sets new variable names and calculates the sum of their ascii value.
     *
     * @param varNames HashSet containing the names of the variables in the CPT rows
     */
    public void setVarNames(HashSet<String> varNames) {
        this.asciiVal = 0;
        this.varNames = new HashSet<>(varNames);
        for (String name : varNames) {
            asciiVal += nameAsAscii(name);
        }
    }

    /*
    How to match the variables and their outcomes with the probabilities:

     How to match the variables and their outcomes with the probabilities of the node:
     - m = the multiplication of the variables in the list until the target variable is reached (the target variable outcomes are also multiplied)
     - step (number of iterations until the index of the outcomes list should be advanced): step = probabilities table size / m
        loop table size:
            j = current index of the table, outcome_index = current index in outcomes list
            - if ((j > 0) && (j % step)) == 0: advance outcome_index
            - if outcome_index == outcomes list size: outcome_index = 0 (restart from first outcome)

     The step size was calculated according to the order in the XML:
            - The given nodes of each variable according to the order in the file, then the variable itself.
            - The outcomes order is also according to the order in the file.
            - The first given node has the largest step, and the variable itself has the smallest step size (alternates every iteration).
     */

    /**
     * Initializes the rows with the probabilities from the table according using the list of variables.
     * The variables are in a list (maintains the inserted order) since their order is important to determine their
     * outcomes in each row!
     *
     * @param variableNodes ArrayList of VariableNodes
     * @param table         probabilities of the current variable
     */
    private void initRows(ArrayList<VariableNode> variableNodes, ArrayList<Double> table) {
        int div = 1, step;
        for (VariableNode v : variableNodes) {
            int outcome_index = 0; // outcome index = current index in the variable outcomes list
            div *= v.getOutcomes().size();
            step = table.size() / div;
            for (int j = 0; j < table.size(); j++) { // j = current index of the table
                if (j > 0 && j % step == 0) { // check if step size is reached
                    outcome_index++; // advance to next outcome
                }
                if (outcome_index >= v.getOutcomes().size()) { // if index is out of bounds restart from first outcome
                    outcome_index = 0;
                }
                rows.get(j).put(v.getName(), v.getOutcomes().get(outcome_index)); // add key (variable name) and value (outcome) in the jth hashmap
            }
        }
        for (int i = 0; i < table.size(); i++) { // add probability to each row
            rows.get(i).put("probability", table.get(i).toString());
        }
    }

    /**
     * Initializes variables from the CPT constructor input and calculates the sum of their ascii values.
     *
     * @param variableNodes ArrayList of VariableNodes
     */
    private void initVars(ArrayList<VariableNode> variableNodes) {
        for (VariableNode v : variableNodes) {
            varNames.add(v.getName());
            asciiVal += nameAsAscii(v.getName());
        }
    }

    /**
     * Calculates the ascii value of each character in a given string.
     *
     * @param var_name variable name
     * @return sum of ascii values of the variable name
     */
    public int nameAsAscii(String var_name) {
        char[] name = var_name.toCharArray();
        int ascii_value = 0;
        for (char c : name) {
            ascii_value += c;
        }
        return ascii_value;
    }

    /**
     * Used in the compare function in the Variable Elimination algorithm (which sorts a list of CPTs).
     * If the table (number of rows) of two CPTs are the same size, the ascii values of the variables each CPT contains
     * are compared.
     *
     * @return ascii value of the variable names in the CPT
     */
    public int getAsciiVal() {
        return asciiVal;
    }

    /**
     * @return CPT rows
     */
    public ArrayList<HashMap<String, String>> getRows() {
        return rows;
    }

    /**
     * @return names of the variables in the CPT
     */
    public HashSet<String> getVarNames() {
        return varNames;
    }
}