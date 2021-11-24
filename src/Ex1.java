import java.util.ArrayList;
import java.util.HashMap;

public class Ex1 {


    public static void main(String[] args) {
//        Network n = new Network("./input.txt");
//        System.out.println(n);
//        System.out.println(n.getResults());
        FileParser parser = new FileParser("./input3.txt");
        HashMap<String, VariableNode> net = parser.getData();
        ArrayList<String> queries = parser.getQueries();
        ArrayList<String> results = new ArrayList<>();
        for (String curr_query : queries) {
            if (curr_query.contains("P(")) {
                VariableEliminationAlgo vea = new VariableEliminationAlgo(net, curr_query);
                String res = vea.RunAlgo();
                results.add(res);
                System.out.println(res);
            } else {
                BayesBallAlgo bba = new BayesBallAlgo(net, curr_query);
                String res = bba.RunAlgo();
                results.add(res);
                System.out.println(res);
            }
            for (VariableNode v : net.values()) {
                v.setEvidence(null);
            }
        }
        System.out.println(results);
    }

}
