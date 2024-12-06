/** Created by Logan J. Stoltz for CSCD 420 - Compilers and Automata, 12/4/2024 **/
public class ProductionRule
{
    public String lhs;
    public String rhs;

    public ProductionRule(String lhs, String rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public String toString() {
        return lhs + " --> " + rhs;
    }
}