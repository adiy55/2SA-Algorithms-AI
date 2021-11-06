import java.util.ArrayList;

public class Network {
    fileParser fp;
    ArrayList<VariableNode> net;

    public Network(String filepath) {
        fp = new fileParser(filepath);
        net = fp.getData();
    }

}
