import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class fileParser {
    private String filename;
    private ArrayList<VariableNode> data;

    public fileParser(String path) {
        filename = path;
        data = new ArrayList<>();
        parseXML();
    }

    private void parseXML() {
        try {
            Scanner sc = new Scanner(new File(filename));
            while (sc.hasNext()) {
                String s = sc.nextLine();
                if (s.contains("<VARIABLE>")) {
                    VariableNode v = parseVariable(sc);
                    data.add(v);
                }
                if (s.contains("<DEFINITION>")) {
                    parseDefinition(sc);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private VariableNode parseVariable(Scanner sc) {
        String name = "";
        String currLine = "";
        ArrayList<String> lst = new ArrayList<>();
        while (sc.hasNext() && !(currLine.contains("</VARIABLE>"))) {
            currLine = sc.nextLine();
            if (currLine.contains("NAME")) {
                name = currLine.split(">")[1].split("<")[0];
            }
            if (currLine.contains("OUTCOME")) {
                String curr = currLine.split(">")[1].split("<")[0];
                lst.add(curr);
            }
        }
        return new VariableNode(name, lst);
    }

    private void parseDefinition(Scanner sc) {
        String currLine = "";
        String varName;
        int varIndex = -1;
        ArrayList<VariableNode> parents = new ArrayList<>();
        ArrayList<Double> table = new ArrayList<>();
        while (sc.hasNext() && !(currLine.contains("</DEFINITION>"))) {
            currLine = sc.nextLine();
            if (currLine.contains("FOR")) {
                varName = currLine.split(">")[1].split("<")[0];
                varIndex = findVariableNode(varName);
            }
            if (currLine.contains("GIVEN")) {
                String curr = currLine.split(">")[1].split("<")[0];
                int index = findVariableNode(curr);
                if (index != -1) {
                    parents.add(data.get(index));
                    data.get(index).addChild(data.get(varIndex));
                }
            }
            if (currLine.contains("TABLE")) {
                String[] nums = currLine.split(">")[1].split("<")[0].split(" ");
                for (String num : nums) {
                    double n = Double.parseDouble(num);
                    table.add(n);
                }
            }
        }
        data.get(varIndex).setParents(parents);
        data.get(varIndex).setTable(table);
    }

    private int findVariableNode(String varName) {
        int varIndex = -1;
        for (int i = 0; i < data.size(); i++) {
            VariableNode currVar = data.get(i);
            if (currVar.getName().equals(varName)) {
                varIndex = i;
                break;
            }
        }
        return varIndex;
    }

    public ArrayList<VariableNode> getData() {
        return data;
    }
}
