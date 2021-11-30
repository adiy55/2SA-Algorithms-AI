import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Ex1 {

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
