/** Created by Logan J. Stoltz for CSCD 420 - Compilers and Automata, 12/4/2024 **/
import java.io.*;
import java.util.*;

public class OutputGenerator {

    public static void generateOutput(List<Grammar> grammars, String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

        // iterate through every grammar.
        for (Grammar grammar : grammars) {
            // Step 1: write the production rules of the curent grammar.
            for (ProductionRule rule : grammar.rules) {
                writer.write(rule.toString());
                writer.newLine();
            }

            writer.newLine(); // add a blank line to separate grammar rules from query results.

            // Step 2: write the results of the queries for the current grammar.
            for (String query : grammar.queries) {
                // call the CYK algorithm to check if the query is in the language.
                boolean result = CYKAlgorithm.parse(query, grammar);

                writer.write(query + (result ? " is in the language." : " is NOT in the language."));
                writer.newLine();
            }
            writer.newLine(); // blank line to separate outputs for different grammars.
        }
        writer.close();
    }
}
