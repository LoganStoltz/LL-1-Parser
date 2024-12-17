This program uses the LL(1) algorithm to process text input containing context-free grammars (CFGs) and evaluates strings to determine whether they belong to the languages defined by the corresponding CFGs. It can compute the FIRST and FOLLOWS for each non-terminal, constructing an LL(1) Parse Table from them to compute whether the grammar is in LL(1), and then applying language membership tests based on the LL(1) parsing algorithm.

This will have an input text file that can have multiple Context-free Grammars each will multiple string to test and this will determine if any of them are accepted or not.

Input: a text file called "input.txt" located in the project's base directory (use a relative path to open)
- The first line is a single integer with the number of grammars with lists of queries to follow
- The second line is blank
- The next n lines are productions of the first grammar (you can assume each production is on its own line, so no A -> aA | a)
- The next line is a blank line
- The next line contains a single inter m, the number of query strings for the first grammar
- The next m lines contain query strings for the first grammar, one per line
- The next line is blank
- If there is a second grammar, the next lines will contain the productions
- (Continue this progression until end-of-file)
- Example input.txt is in the repo.

This program assumes that:
- All nonterminals will be a single uppercase letter
- All terminals will be a single lowercase letter
- Each production is on its own line. Meaning you should not use | to seperate productions from the same Nonterminal, put eash production on a sepearte line.
- Arrows will be made up of the exact string "-->"
- The first grammar production left hand side is the start nonterminal (even if it is not 'S')
- The text file input.txt follows the input inscructions.
