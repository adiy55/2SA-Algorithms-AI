import java.util.HashMap;
import java.util.Stack;

public class bayesBallAlgo {
    private HashMap<String, VariableNode> data;
    private String[] input;

    public bayesBallAlgo(HashMap<String, VariableNode> data, String[] input) {
        this.data = data;
        this.input = input;
    }

    private String search() { // DFS
        Stack<VariableNode> s = new Stack<>();
        s.push(data.get(input[0]));
        while (!s.isEmpty()) {
            VariableNode v = s.pop();
            if (v.getName().equals(input[1])) {
                return "no";
            }
            if (!v.isEvidence()) {
                if (v.isFromChild()) {
                    for (int i = 0; i < v.getParents().size(); i++) {
                        VariableNode currParent = v.getParents().get(i);
                        currParent.setFromChild(true);
                        s.push(currParent);
                    }
                } else { // can go to any child
                    for (int i = 0; i < v.getChildren().size(); i++) {
                        VariableNode currChild = v.getChildren().get(i);
                        currChild.setFromParent(true);
                        s.push(currChild);
                    }
                }
            }
            if (v.isEvidence() && v.isFromParent()) {
                for (int i = 0; i < v.getParents().size(); i++) {
                    VariableNode currParent = v.getParents().get(i);
                    currParent.setFromChild(true);
                    s.push(currParent);
                }
            }
        }
        return "yes";
    }

    public static void main(String[] args) {
        xpathParser xp = new xpathParser("alarm_net.xml");
        // B-E|
        // B-E|J=T
        String[] input = new String[]{"B", "E"};
        VariableNode v = xp.getData().get("J");
        v.setEvidence("T");


        bayesBallAlgo bb = new bayesBallAlgo(xp.getData(), input);
        System.out.println(bb.search());

        // ---------------------

        xpathParser xp2 = new xpathParser("network.xml");
        String[] input2 = new String[]{"L", "B"};
//        VariableNode v2 = (VariableNode) xp2.getData().get("T'");
//        v2.setEvidence("T'");
//        v2 = (VariableNode) xp2.getData().get("R");
//        v2.setEvidence("T");

        bayesBallAlgo bb2 = new bayesBallAlgo(xp2.getData(), input2);
        System.out.println(bb2.search());
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
