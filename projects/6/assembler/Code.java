import java.util.Map;

/**
 * Translates Hack assembly mnemonics (dest, comp, jump) into their
 * binary representations. All methods are static since this class
 * has no state of its own.
 */
public class Code {
    //static final variables are constants that are shared across all instances of the class. They are initialized once and cannot be changed.
    //You can call the static methods of the Code class without creating an instance of the class. 
    //For example, you can call Code.comp("D+1") to get the binary representation of the comp mnemonic "D+1".
    private static final Map<String, String> COMP_TABLE = Map.ofEntries(
        //Map.ofEntries allows us to create a map with multiple entries in a more readable way
        //Map.ofEntries() 是一个方法，它接收一个可变参数列表（Java 里叫 varargs，类似数组/列表），然后返回一个字典
        Map.entry("0",   "0101010"), //A key-value tuple
        Map.entry("1",   "0111111"),
        Map.entry("-1",  "0111010"),
        Map.entry("D",   "0001100"),
        Map.entry("A",   "0110000"),
        Map.entry("!D",  "0001101"),
        Map.entry("!A",  "0110001"),
        Map.entry("-D",  "0001111"),
        Map.entry("-A",  "0110011"),
        Map.entry("D+1", "0011111"),
        Map.entry("A+1", "0110111"),
        Map.entry("D-1", "0001110"),
        Map.entry("A-1", "0110010"),
        Map.entry("D+A", "0000010"),
        Map.entry("D-A", "0010011"),
        Map.entry("A-D", "0000111"),
        Map.entry("D&A", "0000000"),
        Map.entry("D|A", "0010101"),
        Map.entry("M",   "1110000"),
        Map.entry("!M",  "1110001"),
        Map.entry("-M",  "1110011"),
        Map.entry("M+1", "1110111"),
        Map.entry("M-1", "1110010"),
        Map.entry("D+M", "1000010"),
        Map.entry("D-M", "1010011"),
        Map.entry("M-D", "1000111"),
        Map.entry("D&M", "1000000"),
        Map.entry("D|M", "1010101")
    );

    private static final Map<String, String> DEST_TABLE = Map.of(
        "",    "000",
        "M",   "001",
        "D",   "010",
        "MD",  "011",
        "A",   "100",
        "AM",  "101",
        "AD",  "110",
        "AMD", "111"
    );

    private static final Map<String, String> JUMP_TABLE = Map.of(
        "",    "000",
        "JGT", "001",
        "JEQ", "010",
        "JGE", "011",
        "JLT", "100",
        "JNE", "101",
        "JLE", "110",
        "JMP", "111"
    );

    /** Returns the 7-bit binary code for a comp mnemonic (includes the 'a' bit). */
    public static String comp(String mnemonic) {
        String result = COMP_TABLE.get(mnemonic);
        if (result == null) {
            throw new IllegalArgumentException("Invalid comp mnemonic: " + mnemonic);
        }
        return result;
    }

    /** Returns the 3-bit binary code for a dest mnemonic. */
    public static String dest(String mnemonic) {
        String result = DEST_TABLE.get(mnemonic);
        if (result == null) {
            throw new IllegalArgumentException("Invalid dest mnemonic: " + mnemonic);
        }
        return result;
    }

    /** Returns the 3-bit binary code for a jump mnemonic. */
    public static String jump(String mnemonic) {
        String result = JUMP_TABLE.get(mnemonic);
        if (result == null) {
            throw new IllegalArgumentException("Invalid jump mnemonic: " + mnemonic);
        }
        return result;
    }
}