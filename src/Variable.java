import java.util.ArrayList;

public class Variable {
    private String name;
    private ArrayList<String> outcome;
    private boolean isEvidence;

    public Variable(String name, ArrayList<String> outcome, Boolean isEvidence) {
        this.name = name;
        this.outcome = outcome;
        this.isEvidence = isEvidence;
    }

    public String toString() {
        String s = "{NAME: " + name + ", OUTCOME:";
        for (String value : outcome) {
            s = s + " " + value;
        }
        return s + "}";
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getOutcome() {
        return outcome;
    }

    public boolean isEvidence() {
        return isEvidence;
    }

    public void setEvidence(boolean evidence) {
        isEvidence = evidence;
    }
}
