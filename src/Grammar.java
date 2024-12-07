/** Created by Logan J. Stoltz for CSCD 420 - Compilers and Automata, 12/7/2024 **/
import java.util.ArrayList;
import java.util.List;

public class Grammar {
    // pieces of the output in list/String form
    public List<ProductionRule> rules = new ArrayList<>();
    public List<String> queries = new ArrayList<>();
    public String startSymbol = null;

    public void addProduction(String line) {
        // split the production rule into LHS and RHS.
        String[] parts = line.split(" --> ");
        String lhs = parts[0]; // Non-terminal on the left-hand side.
        String rhs = parts[1]; // Production on the right-hand side.

        // add the prodution rule to the rule list.
        rules.add(new ProductionRule(lhs, rhs));

        // start symbol should be in LHS of the first rule.
        if (startSymbol == null) {
            startSymbol = lhs;
        }
    }
}
