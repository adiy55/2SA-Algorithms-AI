import java.util.HashMap;
import java.util.Stack;

public class BayesBallAlgo {
    private HashMap<String, VariableNode> data;
    private String[] input;

    public BayesBallAlgo(HashMap<String, VariableNode> data, String[] input) {
        this.data = data;
        this.input = input;
    }

    public String search() { // DFS
        Stack<VariableNode> s = new Stack<>();
        VariableNode v = data.get(input[0]);
        s.push(v);
        add_children(s, v);
        add_parents(s, v);
        while (!s.isEmpty()) {
            v = s.pop();
            if (v.getName().equals(input[1])) {
                return "no";
            }
            if (!v.isEvidence()) {
                if (v.isFromChild()) {
                    add_parents(s, v);
                } else {
                    add_children(s, v);
                }
            }
            if (v.isEvidence() && v.isFromParent()) {
                add_parents(s, v);
            }
        }
        return "yes";
    }

    private void add_parents(Stack<VariableNode> s, VariableNode v) {
        for (int i = 0; i < v.getParents().size(); i++) {
            VariableNode currParent = v.getParents().get(i);
            currParent.setFromChild(true);
            s.push(currParent);
        }
    }

    private void add_children(Stack<VariableNode> s, VariableNode v) {
        for (int i = 0; i < v.getChildren().size(); i++) {
            VariableNode currChild = v.getChildren().get(i);
            currChild.setFromParent(true);
            s.push(currChild);
        }
    }

    public static void main(String[] args) {
        xpathParser xp = new xpathParser("alarm_net.xml");
        // B-E|
        // B-E|J=T
        String[] input = new String[]{"B", "E"};
//        xp.getData().get("J").setEvidence("T");

//        BayesBallAlgo bb = new BayesBallAlgo(xp.getData(), input);
//        System.out.println(bb.search());
//        System.out.println(bb.search2());

        // ---------------------

        xpathParser xp2 = new xpathParser("network.xml");
        String[] input2 = new String[]{"L", "B"};
        xp2.getData().get("D").setEvidence("T");
//        xp2.getData().get("T").setEvidence("T");
//        xp2.getData().get("R").setEvidence("T");


        BayesBallAlgo bb2 = new BayesBallAlgo(xp2.getData(), input2);
//        System.out.println(bb2.search4());
        System.out.println(bb2.search());
    }

}

//    private String search4() {
//        Queue<VariableNode> s = new LinkedList<>();
//        s.add(data.get(input[0]));
//        while (!s.isEmpty()) {
//            VariableNode v = s.poll();
//            if (v.getName().equals(input[1])) {
//                return "no";
//            }
//            if (!v.isEvidence()) {
//                if (v.isFromChild()) { // fromChild is true when v is the parent of an evidence node (can't go down)
//                    for (int i = 0; i < v.getParents().size(); i++) {
//                        VariableNode currParent = v.getParents().get(i);
//                        currParent.setFromChild(true);
//                        s.add(currParent);
//                    }
//                } else { // v is the start node or not an evidence node (can only go down)
//                    for (int i = 0; i < v.getChildren().size(); i++) {
//                        VariableNode currChild = v.getChildren().get(i);
//                        currChild.setFromParent(true);
//                        s.add(currChild);
//                    }
//                }
//            }
//            if (v.isEvidence() && v.isFromParent()) {
//                for (int i = 0; i < v.getParents().size(); i++) {
//                    VariableNode currParent = v.getParents().get(i);
//                    currParent.setFromChild(true);
//                    s.add(currParent);
//                }
//            }
//        }
//        return "yes";
//    }
