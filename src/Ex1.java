import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Ex1 {
    /*
    The fundamental data structures used:
    1. HashMap<String, VariableNode>: the key is the variable name, the value is a VariableNode object with attributes that represent each node
    2. ArrayList<HashMap<String,String>>: the list represents the entire table, each HashMap is a row in the CPT.
        They key may be the variable name and contain the outcome as its value. If the key name is "probability", the value is the probability of the row

    The main advantages of a HashMap:
    * There are no duplicates (each node is unique).
    * The information can be accessed easily (returns a value in O(1)).
    * Looping the keys or values of a HashMap is linear (O(n) runtime).
     */

    public static void main(String[] args) {
        FileParser parser = new FileParser("input.txt");
        HashMap<String, VariableNode> net = parser.getData();
        ArrayList<String> queries = parser.getQueries();
        ArrayList<String> results = new ArrayList<>();
        String res;
        for (String curr_query : queries) {
            if (curr_query.contains("P(")) {
                VariableEliminationAlgo vea = new VariableEliminationAlgo(net, curr_query);
                try { // ensures the program keeps running even if there is a problem with a query
                    res = vea.RunAlgo();
                } catch (Exception e) {
                    res = "";
                    vea.ResetAttributes();
                }
            } else {
                BayesBallAlgo bba = new BayesBallAlgo(net, curr_query);
                try {  // ensures the program keeps running even if there is a problem with a query
                    res = bba.RunAlgo();
                } catch (Exception e) {
                    res = "";
                    bba.ResetAttributes();
                }
            }
            results.add(res);
            for (VariableNode v : net.values()) { // reset evidence for next query
                v.setEvidence(null);
            }
        }
        try { // write query results to a text file
            File output = new File("output.txt");
            FileWriter writer = new FileWriter(output);
            for (int i = 0; i < results.size(); i++) {
                if (i == results.size() - 1) {
                    writer.write(results.get(i));
                } else {
                    writer.write(results.get(i) + System.lineSeparator());
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
