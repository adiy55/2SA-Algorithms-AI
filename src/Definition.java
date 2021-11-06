import java.util.ArrayList;

public class Definition {
    private Variable var;
    private ArrayList<Variable> parents; // given
    //    ArrayList<Variable> children;
    private ArrayList<Double> table;

    public Definition(Variable var, ArrayList<Variable> given, ArrayList<Double> table) {
        this.var = var;
        this.parents = given;
        this.table = table;
    }

    public String toString() {
        String s = "\nVARIABLE: " + var;
        if (!parents.isEmpty()) {
            s += ", {GIVEN:";
            for (Variable parent : parents) {
                s += " " + parent.getName();
            }
            s += "}";
        }
        s += ", TABLE:" + table;
        return s;
    }

    public Variable getVar() {
        return var;
    }

    public ArrayList<Double> getTable() {
        return table;
    }

    public ArrayList<Variable> getParents() {
        return parents;
    }
}
