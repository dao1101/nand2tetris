import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;



/**
 * Entry point. Usage: java Main Prog.asm
 * Produces Prog.hack in the same directory.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <file.asm>");
            return;
        }

        String inputPath = args[0];
        if (!inputPath.endsWith(".asm")) {
            System.out.println("Input file must end with .asm");
            return;
        }
        String outputPath = inputPath.substring(0, inputPath.length() - 4) + ".hack";

        try {
            SymbolTable symbolTable = new SymbolTable();

            // ---------- Pass 1: build the symbol table for labels ----------
            firstPass(inputPath, symbolTable);

            // ---------- Pass 2: translate instructions, resolving symbols ----------
            secondPass(inputPath, outputPath, symbolTable);

            System.out.println("Assembled: " + outputPath);
        } catch (IOException e) {
            System.out.println("Error reading/writing file: " + e.getMessage());
        }
    }

    /** First pass: scan for (LABEL) declarations and record their ROM addresses. */
    private static void firstPass(String inputPath, SymbolTable symbolTable) throws IOException {
        Parser parser = new Parser(inputPath);
        int romAddress = 0; // address of the NEXT instruction to be written

        while (parser.hasMoreLines()) {
            parser.advance();
            if (parser.instructionType() == Parser.InstructionType.L_INSTRUCTION) {
                symbolTable.addEntry(parser.symbol(), romAddress);
                // labels don't occupy an instruction slot, so romAddress doesn't advance
            } else {
                romAddress++;
            }
        }
    }

    /** Second pass: translate each A/C instruction into binary and write it out. */
    private static void secondPass(String inputPath, String outputPath, SymbolTable symbolTable)
            throws IOException {
        Parser parser = new Parser(inputPath);
        int nextVariableAddress = 16; // RAM addresses 0-15 are reserved

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            while (parser.hasMoreLines()) {
                parser.advance();
                Parser.InstructionType type = parser.instructionType();

                if (type == Parser.InstructionType.L_INSTRUCTION) {
                    continue; // already handled in pass 1, nothing to emit
                }

                String binaryLine;

                if (type == Parser.InstructionType.A_INSTRUCTION) {
                    String symbol = parser.symbol();
                    int address;
                    if (symbol.chars().allMatch(Character::isDigit)) {
                        address = Integer.parseInt(symbol);
                    } else if (symbolTable.contains(symbol)) {
                        address = symbolTable.getAddress(symbol);
                    } else {
                        address = nextVariableAddress;
                        symbolTable.addEntry(symbol, nextVariableAddress);
                        nextVariableAddress++;
                    }
                    String bin = Integer.toBinaryString(address);
                    binaryLine = "0" + "0".repeat(15 - bin.length()) + bin;
                } else { // C_INSTRUCTION
                    String comp = Code.comp(parser.comp());
                    String dest = Code.dest(parser.dest());
                    String jump = Code.jump(parser.jump());
                    binaryLine = "111" + comp + dest + jump;
                }

                writer.write(binaryLine);
                writer.newLine();
            }
        }
    }
}