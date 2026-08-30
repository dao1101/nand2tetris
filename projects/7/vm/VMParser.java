import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VMParser {

    private final List<String> lines;
    private int currentIndex;
    private String currentLine;

    public VMParser(String filePath) throws IOException {
        lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String raw;
            while ((raw = reader.readLine()) != null) {
                int commentIdx = raw.indexOf("//");
                String cleaned = (commentIdx >= 0) ? raw.substring(0, commentIdx).trim() : raw.trim();
                if (!cleaned.isEmpty()) {
                    lines.add(cleaned);
                }
            }
        }
        currentIndex = -1;
    }

    public boolean hasMoreLines() {
        return currentIndex + 1 < lines.size();
    }

    public void advance() {
        currentIndex++;
        currentLine = lines.get(currentIndex);
    }

    public String commandType() {
        if (currentLine.startsWith("push")) return "C_PUSH";
        if (currentLine.startsWith("pop")) return "C_POP";
        return "C_ARITHMETIC";
    }

    public String arg1() {
        String[] parts = currentLine.split("\\s+");
        if (commandType().equals("C_ARITHMETIC")) {
            return parts[0];
        }
        return parts[1];
    }

    public int arg2() {
        String[] parts = currentLine.split("\\s+");
        return Integer.parseInt(parts[2]);
    }
}
