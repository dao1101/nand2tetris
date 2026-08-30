import java.io.BufferedWriter;
import java.io.IOException;

public class CodeWriter {

    private final BufferedWriter writer;
    private final String fileName;
    private int labelCount = 0;

    public CodeWriter(BufferedWriter writer, String fileName) {
        this.writer = writer;
        this.fileName = fileName;
    }

    public void writeArithmetic(String command) throws IOException {
        switch (command) {
            case "add":  writeBinary("D+M"); break;
            case "sub":  writeBinary("M-D"); break;
            case "and":  writeBinary("D&M"); break;
            case "or":   writeBinary("D|M"); break;
            case "neg":  writeUnary("-M");   break;
            case "not":  writeUnary("!M");   break;
            case "eq":   writeComparison("JEQ"); break;
            case "gt":   writeComparison("JGT"); break;
            case "lt":   writeComparison("JLT"); break;
        }
    }

    public void writePushPop(String command, String segment, int index) throws IOException {
        if (command.equals("C_PUSH")) {
            writePush(segment, index);
        } else {
            writePop(segment, index);
        }
    }

    private void writePush(String segment, int index) throws IOException {
        switch (segment) {
            case "constant":
                emit("@" + index);
                emit("D=A");
                break;
            case "local":
            case "argument":
            case "this":
            case "that":
                emit("@" + segmentName(segment));
                emit("D=M");
                emit("@" + index);
                emit("A=D+A");
                emit("D=M");
                break;
            case "temp":
                emit("@" + (5 + index));
                emit("D=M");
                break;
            case "pointer":
                emit("@" + (index == 0 ? "THIS" : "THAT"));
                emit("D=M");
                break;
            case "static":
                emit("@" + fileName + "." + index);
                emit("D=M");
                break;
        }
        pushD();
    }

    private void writePop(String segment, int index) throws IOException {
        switch (segment) {
            case "local":
            case "argument":
            case "this":
            case "that":
                emit("@" + segmentName(segment));
                emit("D=M");
                emit("@" + index);
                emit("D=D+A");
                emit("@R13");
                emit("M=D");
                popD();
                emit("@R13");
                emit("A=M");
                emit("M=D");
                break;
            case "temp":
                popD();
                emit("@" + (5 + index));
                emit("M=D");
                break;
            case "pointer":
                popD();
                emit("@" + (index == 0 ? "THIS" : "THAT"));
                emit("M=D");
                break;
            case "static":
                popD();
                emit("@" + fileName + "." + index);
                emit("M=D");
                break;
        }
    }

    private void writeBinary(String operation) throws IOException {
        popD();
        emit("A=A-1");
        emit("M=" + operation);
    }

    private void writeUnary(String operation) throws IOException {
        emit("@SP");
        emit("A=M-1");
        emit("M=" + operation);
    }

    private void writeComparison(String jump) throws IOException {
        String label = "LABEL_" + (labelCount++);
        popD();
        emit("@SP");
        emit("M=M-1");
        emit("A=M");
        emit("D=M-D");
        emit("@" + label);
        emit("D;" + jump);
        emit("D=0");
        emit("@END_" + (labelCount - 1));
        emit("0;JMP");
        emit("(" + label + ")");
        emit("D=-1");
        emit("(END_" + (labelCount - 1) + ")");
        pushD();
    }

    private void pushD() throws IOException {
        emit("@SP");
        emit("A=M");
        emit("M=D");
        emit("@SP");
        emit("M=M+1");
    }

    private void popD() throws IOException {
        emit("@SP");
        emit("M=M-1");
        emit("A=M");
        emit("D=M");
    }

    private String segmentName(String segment) {
        switch (segment) {
            case "local":    return "LCL";
            case "argument": return "ARG";
            case "this":     return "THIS";
            case "that":     return "THAT";
            default:         return segment;
        }
    }

    private void emit(String instruction) throws IOException {
        writer.write(instruction);
        writer.newLine();
    }

    public void close() throws IOException {
        writer.close();
    }
}
