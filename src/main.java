import java.util.ArrayList;

public class main {

    public static void main(String[] args) {
//        ArrayList<String> outcome = new ArrayList<>();
//        outcome.add("T");
//        outcome.add("F");
//        Variable v = new Variable("E", outcome, false);
//
//        System.out.println();
//        System.out.println(v);
        myParser p = new myParser("C:\\Users\\adiya\\Desktop\\alarm_net.xml");
        p.parseXML();
        ArrayList<Definition> dd = p.getDefinitions();
        System.out.println(dd);
    }

}
