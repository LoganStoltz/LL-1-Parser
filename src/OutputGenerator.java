/** Created by Logan J. Stoltz for CSCD 420 - Compilers and Automata, 12/5/2024 **/
import java.io.*;
import java.util.*;

public class OutputGenerator {

    public static void generateOutput(List<Grammar> grammars, String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

        for (Grammar grammar : grammars) {
            // Print the grammar rules
            for (ProductionRule rule : grammar.rules) {
                writer.write(rule.toString());
                writer.newLine();
            }

            writer.newLine(); // Blank line after grammar rules.

            // Calculate FIRST and FOLLOW sets
            Map<String, Set<String>> firstSets = LL1ParsingAlgorithm.calculateFirstSets(grammar);
            Map<String, Set<String>> followSets = LL1ParsingAlgorithm.calculateFollowSets(grammar, firstSets);

            // Print FIRST sets
            List<String> sortedNonTerminals = new ArrayList<>(firstSets.keySet());
            Collections.sort(sortedNonTerminals, (a, b) -> {
                if (a.equals("S")) return -1; // `S` comes first
                if (b.equals("S")) return 1;
                return a.compareTo(b); // Alphabetical order
            });

            for (String nonTerminal : sortedNonTerminals) {
                writer.write("FIRST(" + nonTerminal + ") = " + formatSet(firstSets.get(nonTerminal)));
                writer.newLine();
            }

            // Print FOLLOW sets
            for (String nonTerminal : sortedNonTerminals) {
                writer.write("FOLLOW(" + nonTerminal + ") = " + formatSet(followSets.get(nonTerminal)));
                writer.newLine();
            }

            writer.newLine(); // Blank line after FOLLOW sets.

            // Generate and print LL(1) parsing table
            Map<String, Map<String, String>> parsingTable = LL1ParsingAlgorithm.buildParsingTable(grammar, firstSets, followSets);

            writer.write(generateParsingTableOutput(parsingTable, grammar));
            writer.newLine(); // Blank line after parsing table.

            // Check if grammar is LL(1)
            if (!isLL1Grammar(parsingTable)) {
                writer.write("This grammar is LL(1).");
                writer.newLine();

                // Process and print query results
                for (String query : grammar.queries) {
                    boolean result = LL1ParsingAlgorithm.processQuery(query, grammar.startSymbol, parsingTable);
                    writer.write(query + (result ? " is in the language defined by the above grammar." : " is NOT in the language defined by the above grammar."));
                    writer.newLine();
                }
            } else {
                writer.write("This grammar is NOT LL(1).");
                writer.newLine();
            }

            writer.newLine(); // Separate grammars with a blank line.
            writer.newLine(); // Blank line after grammar rules.
        }

        writer.close();
    }


    private static String formatSet(Set<String> set) {;
        return  "{" + String.join(", ", set) + "}";
    }

    // Helper method to generate the LL(1) parsing table output
    private static String generateParsingTableOutput(Map<String, Map<String, String>> parsingTable, Grammar grammar) {
        StringBuilder sb = new StringBuilder();

        // Collect and sort terminals for consistent column order
        Set<String> terminals = new HashSet<>();
        for (Map<String, String> row : parsingTable.values()) {
            terminals.addAll(row.keySet());
        }

        List<String> terminalList = new ArrayList<>(terminals);
        Collections.sort(terminalList, (a ,b) -> {
            if (a.equals("$")) return 1;
            if (b.equals("$")) return -1;
            return a.compareTo(b);
        }); // Sort terminals alphabetically

        if(!terminalList.contains("$")) {
            terminalList.add("$");
        }

        // Step 1: Calculate the maximum width for each column
        Map<String, Integer> columnWidths = new HashMap<>();

        // Calculate the width needed for terminal headers
        for (String terminal : terminalList) {
            columnWidths.put(terminal, terminal.length());
        }

        // Calculate the maximum width for each column (including values in the parsing table)
        for (String nonTerminal : parsingTable.keySet()) {
            Map<String, String> row = parsingTable.get(nonTerminal);
            for (String terminal : terminalList) {
                String cell = row.getOrDefault(terminal, "");
                int maxWidth = Math.max(cell.length(), terminal.length()); // Compare cell size and terminal size
                columnWidths.put(terminal, Math.max(columnWidths.get(terminal), maxWidth)); // Update column width
            }
        }

        // Step 2: Generate table header with adjusted column width
        sb.append(" |");
        for (String terminal : terminalList) {
            int width = columnWidths.get(terminal);
            if(width < 3) {width = 3;}
            sb.append(String.format("%-" + width + "s|", terminal));
        }
        sb.append(System.lineSeparator());

        // Step 3: Generate table rows
        List<String> sortedNonTerminals = new ArrayList<>(parsingTable.keySet());
        Collections.sort(sortedNonTerminals, (a, b) -> {
            if (a.equals("S")) return -1; // `S` comes first
            if (b.equals("S")) return 1;
            return a.compareTo(b); // Alphabetical order
        });

        for (String nonTerminal : sortedNonTerminals) {
            sb.append(String.format("%s|", nonTerminal)); // Adjust width for non-terminal
            Map<String, String> row = parsingTable.get(nonTerminal);
            for (String terminal : terminalList) {
                String cell = row.getOrDefault(terminal, "");
                int width = columnWidths.get(terminal);
                if(width < 3) {width = 3;}
                sb.append(String.format("%-" + width + "s|", cell));
            }
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }


    // Check if the grammar is LL(1)
    private static boolean isLL1Grammar(Map<String, Map<String, String>> parsingTable) {
        for (Map<String, String> row : parsingTable.values()) {
            Set<String> seen = new HashSet<>();
            for (String production : row.values()) {
                if (production != null && !seen.add(production)) {
                    return false; // Conflict in the table
                }
            }
        }
        return true;
    }
}
