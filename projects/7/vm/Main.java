import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <file.vm>");
            return;
        }

        String inputPath = args[0];
        if (!inputPath.endsWith(".vm")) {
            System.out.println("Input file must end with .vm");
            return;
        }

        String outputPath = inputPath.substring(0, inputPath.length() - 3) + ".asm";
        String fileName = inputPath.substring(inputPath.lastIndexOf('\\') + 1, inputPath.length() - 3);

        try {
            VMParser parser = new VMParser(inputPath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
            CodeWriter codeWriter = new CodeWriter(writer, fileName);

            while (parser.hasMoreLines()) {
                parser.advance();
                String type = parser.commandType();

                if (type.equals("C_ARITHMETIC")) {
                    codeWriter.writeArithmetic(parser.arg1());
                } else {
                    codeWriter.writePushPop(type, parser.arg1(), parser.arg2());
                }
            }

            codeWriter.close();
            System.out.println("Translated: " + outputPath);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
