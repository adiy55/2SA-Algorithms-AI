import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class inputReader {
    private String filepath;
    private fileParser fp;
    private ArrayList<VariableNode> data;
    private ArrayList<String> bayesBallInput;
    private ArrayList<String> bayesBallEvidence;
    // ArrayList<String> varEliminationInput;

    public inputReader(String filepath) {
        this.filepath = filepath;
        this.fp = new fileParser();
        this.bayesBallInput = new ArrayList<>();
        this.bayesBallEvidence = new ArrayList<>();
        setBayesBallInput();
    }

    private void setBayesBallInput() {
        try {
            Scanner sc = new Scanner(new File(filepath));
            while (sc.hasNext()) {
                String currLine = sc.nextLine();
                if (currLine.contains(".xml")) {
                    data = fp.parseXML("src/" + currLine); // TODO: fix path
                }
                if (!currLine.contains("P(") && !currLine.contains(".xml")) {
                    String[] s = currLine.split("\\|");
                    bayesBallInput.add(s[0]);
                    if (s.length > 1) {
                        bayesBallEvidence.add(s[1]);
                    }


                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<VariableNode> getData() {
        return data;
    }
}
