/** Created by Logan J. Stoltz for CSCD 420 - Compilers and Automata, 12/4/2024 **/
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Grammar> grammars = ReadInput.readInput("input.txt");
        OutputGenerator.generateOutput(grammars, "output.txt");
    }
}
