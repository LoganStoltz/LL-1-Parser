/** Created by Logan J. Stoltz for CSCD 420 - Compilers and Automata, 12/4/2024 **/
import java.io.*;
import java.util.*;

public class ReadInput {

    public static List<Grammar> readInput(String filename) throws IOException {
        List<Grammar> grammars = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        // read the number of grammars in the file.
        int grammarCount = Integer.parseInt(reader.readLine().trim());
        reader.readLine(); // blank line after the grammar count.

        // go through each grammar.
        for (int i = 0; i < grammarCount; i++) {
            Grammar grammar = new Grammar(); // create new Grammar object for each.
            String line;

            // Step 1: add production rules for the current grammar until blank line encountered.
            while (!(line = reader.readLine()).isEmpty()) {
                grammar.addProduction(line.trim());
            }

            // Step 2: read the number of queries for the current grammar.
            int queryCount = Integer.parseInt(reader.readLine().trim());

            // Step 3: read each query and add it to the grammar's query list.
            for (int j = 0; j < queryCount; j++) {
                grammar.queries.add(reader.readLine().trim());
            }

            // add to the list of grammars.
            grammars.add(grammar);

            reader.readLine(); // blank line separating grammars.
        }
        reader.close();

        return grammars;
    }
}
