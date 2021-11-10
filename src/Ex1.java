import java.io.FileNotFoundException;
import java.util.HashMap;

public class Ex1 {

    public static void main(String[] args) throws FileNotFoundException {

        String path = "C:\\Users\\adiya\\Documents\\Documents\\Uni\\2SA\\AI\\Project\\input.txt";

        Network N = new Network(path);
        System.out.println(N.getReader().getBayesBallInput());
        System.out.println(N.getReader().getVarEliminationInput());
        System.out.println(N.getReader().getXml());
        System.out.println();
        System.out.println(N);

        HashMap map = new HashMap();
        map.values();
    }

}
