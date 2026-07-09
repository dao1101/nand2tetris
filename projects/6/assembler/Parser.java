import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a .asm file, strips comments/whitespace, and exposes
 * each instruction one at a time via advance()/hasMoreLines().
 */
public class Parser {

    public enum InstructionType {
        A_INSTRUCTION, // @xxx  (xxx is a number or a symbol)
        C_INSTRUCTION, // dest=comp;jump
        L_INSTRUCTION  // (LABEL)
    }

    private final List<String> lines;
    private int currentIndex;      // index of the current instruction in `lines`
    private String currentLine;    // the current cleaned instruction

    public Parser(String filePath) throws IOException {
        lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String raw;
            while ((raw = reader.readLine()) != null) {
                String cleaned = clean(raw);
                if (!cleaned.isEmpty()) {
                    lines.add(cleaned);
                }
            }
        }
        currentIndex = -1;
        currentLine = null;
    }

    /** Strips comments (// ...) and surrounding whitespace from a raw line. */
    private String clean(String raw) {
        int commentIndex = raw.indexOf("//");
        String noComment = (commentIndex >= 0) ? raw.substring(0, commentIndex) : raw;
        return noComment.trim();
    }

    public boolean hasMoreLines() {
        return currentIndex + 1 < lines.size();
    }

    /** Moves to the next instruction. Should only be called if hasMoreLines() is true. */
    public void advance() {
        currentIndex++;
        currentLine = lines.get(currentIndex);
    }

    /** Resets the parser back to the beginning, for a second pass over the file. */
    public void reset() {
        currentIndex = -1;
        currentLine = null;
    }

    public InstructionType instructionType() {
        if (currentLine.startsWith("@")) {
            return InstructionType.A_INSTRUCTION;
        } else if (currentLine.startsWith("(")) {
            return InstructionType.L_INSTRUCTION;
        } else {
            return InstructionType.C_INSTRUCTION;
        }
    }

    /** For A_INSTRUCTION: returns the symbol or number (without '@'). 
     *  For L_INSTRUCTION: returns the label (without parens). */
    public String symbol() {
        if (instructionType() == InstructionType.A_INSTRUCTION) {
            return currentLine.substring(1);
        } else { // L_INSTRUCTION
            return currentLine.substring(1, currentLine.length() - 1);
        }
    }

    /** For C_INSTRUCTION: returns the dest mnemonic, or "" if none. */
    public String dest() {
        int eqIndex = currentLine.indexOf('=');
        if (eqIndex < 0) {
            return "";
        }
        return currentLine.substring(0, eqIndex);
    }

    /** For C_INSTRUCTION: returns the comp mnemonic. */
    public String comp() {
        int eqIndex = currentLine.indexOf('=');
        int semiIndex = currentLine.indexOf(';');
        int start = (eqIndex >= 0) ? eqIndex + 1 : 0;
        int end = (semiIndex >= 0) ? semiIndex : currentLine.length();
        return currentLine.substring(start, end);
    }

    /** For C_INSTRUCTION: returns the jump mnemonic, or "" if none. */
    public String jump() {
        int semiIndex = currentLine.indexOf(';');
        if (semiIndex < 0) {
            return "";
        }
        return currentLine.substring(semiIndex + 1);
    }
}