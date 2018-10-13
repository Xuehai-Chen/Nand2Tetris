import java.io.BufferedWriter;

import java.io.FileWriter;

public class CodeWriter {

    private BufferedWriter writer;

    public CodeWriter(String fileName) throws Exception {
        writer = new BufferedWriter(new FileWriter(fileName + ".vm"));
    }

    public void writePush(String segment, int index) throws Exception {
        writer.write("push " + segment + " " + index);
        writer.newLine();
    }

    public void writePop(String segment, int index) throws Exception {
        writer.write("pop " + segment + " " + index);
        writer.newLine();
    }

    public void writeArithmetic(String command) throws Exception {
        writer.write(command);
        writer.newLine();
    }

    public void writeLabel(String label) throws Exception {
        writer.write("(" + label + ")");
        writer.newLine();
    }

    public void writeGoto(String label) throws Exception {
        writer.write("goto " + label);
        writer.newLine();
    }

    public void writeIf(String label) throws Exception {
        writer.write("if-goto " + label);
        writer.newLine();
    }

    public void writeCall(String label) throws Exception {
        writer.write("call " + label);
        writer.newLine();
    }

    public void writeFunction(String label) throws Exception {
        writer.write("function " + label);
        writer.newLine();
    }

    public void writeReturn() throws Exception {
        writer.write("return");
        writer.newLine();
    }

    public void close() throws Exception {
        writer.close();
    }
}
