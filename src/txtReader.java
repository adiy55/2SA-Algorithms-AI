import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class txtReader {
    private String filename;
    private String xml;
    private ArrayList<String> bayesBallInput;
    private ArrayList<String> varEliminationInput;

    public txtReader(String filename) throws FileNotFoundException {
        this.filename = filename;
        this.bayesBallInput = new ArrayList<>();
        this.varEliminationInput = new ArrayList<>();
        readFile();
    }

    private void readFile() throws FileNotFoundException {
        Scanner sc = new Scanner(new File(filename));
        while (sc.hasNext()) {
            String currLine = sc.nextLine();
            if (currLine.contains(".xml")) {
                xml = currLine;
            }
            if (currLine.contains("P(")) {
                varEliminationInput.add(currLine);
            }
            if(!currLine.contains("P(") && !currLine.contains(".xml")){
                bayesBallInput.add(currLine);
            }
        }
    }

    public String getXml() {
        return xml;
    }

    public ArrayList<String> getBayesBallInput() {
        return bayesBallInput;
    }

    public ArrayList<String> getVarEliminationInput() {
        return varEliminationInput;
    }

    public static void main(String[] args) {
        String s = "P(B=T|E=T)";
        Pattern p = Pattern.compile("\\(([^P(]+)\\)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);
        m.find();
        String s2 = m.group(1);
        System.out.println(s2);
        Pattern p2 = Pattern.compile("\\(([^|]+)\\)", Pattern.CASE_INSENSITIVE);
        Matcher m2 = p2.matcher(s2);
//        m2.find();
//        System.out.println(m2.group(0));
        while (m2.find()) {
            System.out.println(m2.group(1));
        }
    }

}
