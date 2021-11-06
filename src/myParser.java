import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class myParser {
    private String file;
    private ArrayList<Variable> variables;
    private ArrayList<Definition> definitions;

    public myParser(String file) {
        this.file = file;
        this.variables = new ArrayList<>();
        this.definitions = new ArrayList<>();
    }

    public void parseXML() {
        try {
            Scanner sc = new Scanner(new File(this.file));
            while (sc.hasNext()) {
                String s = sc.nextLine();
                if (s.contains("<VARIABLE>")) {
                    Variable v = parseVariable(sc);
                    variables.add(v);
                }
                if (s.contains("<DEFINITION>")) {
                    Definition d = parseDefinition(sc);
                    definitions.add(d);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Variable parseVariable(Scanner sc) {
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
        return new Variable(name, lst, false);
    }

    private Definition parseDefinition(Scanner sc) {
        String currLine = "";
        String varName;
        int varIndex = -1;
        ArrayList<Variable> parents = new ArrayList<>();
        ArrayList<Double> table = new ArrayList<>();
        while (sc.hasNext() && !(currLine.contains("</DEFINITION>"))) {
            currLine = sc.nextLine();
            if (currLine.contains("FOR")) {
                varName = currLine.split(">")[1].split("<")[0];
                varIndex = findVariable(varName);
            }
            if (currLine.contains("GIVEN")) {
                String curr = currLine.split(">")[1].split("<")[0];
                int index = findVariable(curr);
                if (index != -1) {
                    parents.add(variables.get(index));
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
        return new Definition(variables.get(varIndex), parents, table);
    }

    private int findVariable(String varName) {
        int varIndex = -1;
        for (int i = 0; i < variables.size(); i++) {
            Variable currVar = variables.get(i);
            if (currVar.getName().equals(varName)) {
                varIndex = i;
                break;
            }
        }
        return varIndex;
    }

    public ArrayList<Definition> getDefinitions() {
        return definitions;
    }
}
