import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class bayesBallAlgo {
    private HashMap data;
    private String[] input;
    private ArrayList<String> given;

    public bayesBallAlgo(HashMap data, String[] input, ArrayList<String> given) {
        this.data = data;
        this.input = input;
        this.given = given;
    }

    private String search() { // DFS
        Stack s = new Stack();
        s.push(data.get(input[0]));
        while (!s.isEmpty()) {
            VariableNode v = (VariableNode) s.pop();
            if (v.getName().equals(input[1])) {
                return "yes";
            }
            if (!v.isEvidence()) {
                if (v.isFromChild()) {
                    for (int i = 0; i < v.getParents().size(); i++) {
                        VariableNode currParent = v.getParents().get(i);
                        currParent.setFromChild(true);
                        s.push(currParent);
                    }
                }  else { //if (v.isFromParent() && (!v.isFromChild())) {
                    for (int i = 0; i < v.getChildren().size(); i++) {
                        VariableNode currChild = v.getChildren().get(i);
                        currChild.setFromParent(true);
                        s.push(currChild);
                    }
                }
            }
            if (v.isEvidence()) {
                if (v.isFromParent()) {
                    for (int i = 0; i < v.getParents().size(); i++) {
                        VariableNode currParent = v.getParents().get(i);
                        currParent.setFromChild(true);
                        s.push(currParent);
                    }
                }
            }
        }
        return "no";
    }

    public static void main(String[] args) {
        xpathParser xp = new xpathParser("src/alarm_net.xml");
        // B-E|
        // B-E|J=T
        String[] input = new String[]{"B", "E"};
        ArrayList<String> given = new ArrayList<>();
        given.add("J");
        VariableNode v = (VariableNode) xp.getData().get("J");
        v.setEvidence("T");


        bayesBallAlgo bb = new bayesBallAlgo(xp.getData(), input, given);
        System.out.println(bb.search());
    }

//    private int findNodeIndex() { // returns first index found
//        for (int i = 0; i < data.size(); i++) {
//            if (data.get(i).getName().equals(input[0]) || data.get(i).getName().equals(input[1])) {
//                return i;
//            }
//        }
//        return -1;
//    }

}
