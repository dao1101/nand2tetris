import java.util.HashMap;

/**
 * Keeps a mapping between symbolic names (labels and variables)
 * and their RAM/ROM addresses.
 */
public class SymbolTable {
 
    private final HashMap<String, Integer> table;
 
    public SymbolTable() {
        table = new HashMap<>();
        // Predefined symbols required by the Hack spec
        table.put("SP", 0);
        table.put("LCL", 1);
        table.put("ARG", 2);
        table.put("THIS", 3);
        table.put("THAT", 4);
        table.put("SCREEN", 16384);
        table.put("KBD", 24576);
        for (int i = 0; i <= 15; i++) {
            table.put("R" + i, i);
        }
    }
 
    /** Adds a new symbol -> address pair to the table. */
    public void addEntry(String symbol, int address) {
        table.put(symbol, address);
    }
 
    /** Returns true if the symbol is already in the table. */
    public boolean contains(String symbol) {
        return table.containsKey(symbol);
    }
 
    /** Returns the address associated with the symbol. */
    public int getAddress(String symbol) {
        return table.get(symbol);
    }
}