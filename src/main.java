import java.util.ArrayList;

public class main {

    public static void main(String[] args) {
        String file1 = "src/alarm_net.xml";
        String file2 = "src/big_net.xml";

//        fileParser fp1 = new fileParser(file1);
//        fp1.parseXML();
//        ArrayList<VariableNode> vn = fp1.getData();
//        System.out.println(vn);
//        System.out.println(vn.get(2).getParents().get(1).getChildren());
//        System.out.println(vn.get(3).isRootNode());
//
//        System.out.println("--------------------------------");
//
//        fileParser fp2 = new fileParser(file2);
//        fp2.parseXML();
//        ArrayList<VariableNode> vn2 = fp2.getData();
//        System.out.println(vn2);
        String path = "C:\\Users\\adiya\\Documents\\Documents\\Uni\\2SA\\AI\\Project\\input.txt";

        Network N = new Network(path, "alarm net");
        ArrayList<VariableNode> variables = N.getNet();
        System.out.println(N);

        int i = N.findNodeIndex("A");
        System.out.println(i);


        inputReader ip = new inputReader(path);
       // System.out.println(ip.bayesBallInput);
       // System.out.println(ip.bayesBallEvidence);


    }

}
