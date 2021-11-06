import java.util.ArrayList;

public class main {

    public static void main(String[] args) {
//        ArrayList<String> outcome = new ArrayList<>();
//        outcome.add("T");
//        outcome.add("F");
//        drafts.Variable v = new drafts.Variable("E", outcome, false);
//
//        System.out.println();
//        System.out.println(v);
        String file = "C:\\Users\\adiya\\Desktop\\alarm_net.xml";
//        drafts.myParser p = new drafts.myParser(file);
//        p.parseXML();
//        ArrayList<drafts.Definition> dd = p.getDefinitions();
//        System.out.println(dd.get(0));
//        System.out.println(dd.get(0).getVar().getVariableDef("A", dd));

        fileParser fp = new fileParser(file);
        fp.parseXML();
        ArrayList<VariableNode> vn = fp.getData();
        System.out.println(vn);
        System.out.println(vn.get(2).getParents().get(1).getChildren());
        System.out.println(vn.get(3).isRootNode());

    }

}
