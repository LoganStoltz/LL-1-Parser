import java.util.*;

public class LL1ParsingAlgorithm {

    // Parse a query string using LL(1) algorithm
    public static boolean parse(String query, Grammar grammar) {
        Map<String, Set<String>> firstSets = calculateFirstSets(grammar);
        Map<String, Set<String>> followSets = calculateFollowSets(grammar, firstSets);
        Map<String, Map<String, String>> parsingTable = buildParsingTable(grammar, firstSets, followSets);

        return processQuery(query, grammar.startSymbol, parsingTable);
    }

    // Step 1: Calculate FIRST sets
    public static Map<String, Set<String>> calculateFirstSets(Grammar grammar) {
        Map<String, Set<String>> firstSets = new HashMap<>();

        // Initialize FIRST sets for all non-terminals
        for (ProductionRule rule : grammar.rules) {
            firstSets.putIfAbsent(rule.lhs, new HashSet<>());
        }

        boolean changed;
        do {
            changed = false;
            for (ProductionRule rule : grammar.rules) {
                String lhs = rule.lhs;
                String rhs = rule.rhs;

                Set<String> rhsFirstSet = computeFirst(rhs, firstSets);
                if (firstSets.get(lhs).addAll(rhsFirstSet)) {
                    changed = true;
                }
            }
        } while (changed);

        return firstSets;
    }

    // Compute FIRST set for a given string (right-hand side)
    private static Set<String> computeFirst(String rhs, Map<String, Set<String>> firstSets) {
        Set<String> result = new HashSet<>();
        boolean isNullable = true;

        for (char symbol : rhs.toCharArray()) {
            if (Character.isLowerCase(symbol)) {
                // Terminal symbol, add it to FIRST
                result.add(String.valueOf(symbol));
                isNullable = false;
                break;
            } else if (Character.isUpperCase(symbol)) {
                // Non-terminal, add its FIRST set to the result
                result.addAll(firstSets.get(String.valueOf(symbol)));
                if (!firstSets.get(String.valueOf(symbol)).contains("λ")) {
                    isNullable = false;
                    break;
                }
            }
        }

        // If rhs is nullable, add "ε" to the FIRST set
        if (isNullable) {
            result.add("λ");
        }

        return result;
    }

    // Step 2: Calculate FOLLOW sets
    public static Map<String, Set<String>> calculateFollowSets(Grammar grammar, Map<String, Set<String>> firstSets) {
        Map<String, Set<String>> followSets = new HashMap<>();

        // Initialize FOLLOW sets for all non-terminals
        for (ProductionRule rule : grammar.rules) {
            followSets.putIfAbsent(rule.lhs, new HashSet<>());
        }

        // Start symbol has '$' (end of input) in its FOLLOW set
        followSets.get(grammar.startSymbol).add("$");

        boolean changed;
        do {
            changed = false;
            for (ProductionRule rule : grammar.rules) {
                String lhs = rule.lhs;
                String rhs = rule.rhs;

                Set<String> trailer = new HashSet<>(followSets.get(lhs));
                // Process RHS from right to left
                for (int i = rhs.length() - 1; i >= 0; i--) {
                    char symbol = rhs.charAt(i);

                    if (Character.isUpperCase(symbol)) {
                        String nonTerminal = String.valueOf(symbol);
                        // Add the trailer to the FOLLOW set of nonTerminal
                        if (followSets.get(nonTerminal).addAll(trailer)) {
                            changed = true;
                        }

                        // If the non-terminal can produce ε, add FIRST set of the non-terminal to trailer
                        if (firstSets.get(nonTerminal).contains("λ")) {
                            trailer.addAll(firstSets.get(nonTerminal));
                            trailer.remove("λ");
                        } else {
                            trailer = new HashSet<>(firstSets.get(nonTerminal));
                        }
                    } else {
                        // Terminal symbol, reset trailer to be just this symbol
                        trailer.clear();
                        trailer.add(String.valueOf(symbol));
                    }
                }
            }
        } while (changed);

        return followSets;
    }

    // Step 3: Build LL(1) parsing table and check for conflicts
    public static Map<String, Map<String, String>> buildParsingTable(Grammar grammar, Map<String, Set<String>> firstSets, Map<String, Set<String>> followSets) {
        Map<String, Map<String, String>> table = new HashMap<>();
        Set<String> conflictMessages = new HashSet<>();

        // Always ensure '$' is in the terminals list
        Set<String> terminals = new HashSet<>();
        for (ProductionRule rule : grammar.rules) {
            for (char symbol : rule.rhs.toCharArray()) {
                if (Character.isLowerCase(symbol)) {
                    terminals.add(String.valueOf(symbol));
                }
            }
        }
        terminals.add("$"); // Include the $ terminal

        for (ProductionRule rule : grammar.rules) {
            String lhs = rule.lhs;
            String rhs = rule.rhs;

            Set<String> firstSet = computeFirst(rhs, firstSets);
            // Process the first set
            for (String terminal : firstSet) {
                if (!terminal.equals("λ")) {
                    Map<String, String> row = table.computeIfAbsent(lhs, k -> new HashMap<>());
                    if (row.putIfAbsent(terminal, rhs) != null) {
                        conflictMessages.add("Conflict at (" + lhs + ", " + terminal + "): Multiple productions." + rule);
                        String existingString = row.get(terminal);
                        String modifiedString = existingString + "," + rhs;

                        // Update the value in the HashMap
                        row.put(terminal, modifiedString);
                    }
                }

            }

            // Process ε (nullable) case, and use FOLLOW sets
            if (firstSet.contains("λ")) {
                for (String follow : followSets.get(lhs)) {
                    Map<String, String> row = table.computeIfAbsent(lhs, k -> new HashMap<>());
                    if (row.putIfAbsent(follow, rhs) != null) {
                        conflictMessages.add("Conflict at (" + lhs + ", " + follow + "): Multiple productions.");
                    }
                }
            }

        }

        // Report any conflicts found
        if (!conflictMessages.isEmpty()) {
            for (String conflict : conflictMessages) {
                System.out.println(conflict);  // Print the conflict message
            }
        }

        System.out.println(table);
        return table;
    }

    // Step 4: Process query string using the LL(1) parsing table
    public static boolean processQuery(String query, String startSymbol, Map<String, Map<String, String>> parsingTable) {
        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push(startSymbol);

        int index = 0;
        while (!stack.isEmpty()) {
            String top = stack.pop();

            if (index < query.length() && top.equals(String.valueOf(query.charAt(index)))) {
                index++;  // Match terminal
            } else if (parsingTable.containsKey(top)) {
                String nextInput = index < query.length() ? String.valueOf(query.charAt(index)) : "$";
                String production = parsingTable.getOrDefault(top, new HashMap<>()).get(nextInput);

                if (production == null) {
                    return false; // No matching production in the table
                }

                // Push RHS of production onto stack (reversed)
                for (int i = production.length() - 1; i >= 0; i--) {
                    if (production.charAt(i) != 'λ') {
                        stack.push(String.valueOf(production.charAt(i)));
                    }
                }
            } else {
                return false; // Error: mismatch or missing rule
            }
        }

        return index == query.length(); // Query matches if we've consumed all input
    }
}
