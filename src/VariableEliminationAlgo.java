import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class VariableEliminationAlgo {
    private HashMap<String, VariableNode> data;
    private String[] input;
    private Queue<VariableNode> cpt;

    public VariableEliminationAlgo(HashMap<String, VariableNode> data, String[] input) {
        this.data = data;
        this.input = input;
        this.cpt = new LinkedList<>();
    }

/*

* todo: check variable elimination order (ascii)

input: query node name, queue with elimination order
output: answer (5 digits after decimal),num additions, num multiplications

if isEvidence == true:
    if isRootNode == true: remove factor (is only one value)
    else: check parent (given) nodes-
        if at least one parent is hidden: add table
        if all parents are evidence: remove factor (is only one value)

    while there are still hidden variables (not Q or evidence):
        take the first name out of the queue
        join all factors containing curr name
        eliminate (sum out) curr
        if factor is one value -> remove

    join all remaining factors
    normalize


how to find index of num in table:
- find num of outputs until given variable (multiply them -> m = ans)
- step = table size / m
step will determine where each output starts and ends (<= len(table))
- multiply according to variable outcome

insert table to list and continue

*** use hashmap?? <String (name), int (step)>

 */

}
