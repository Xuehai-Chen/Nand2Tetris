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
        writer.flush();
    }

    public void writePop(String segment, int index) throws Exception {
        writer.write("pop " + segment + " " + index);
        writer.newLine();
        writer.flush();
    }

    public void writeArithmetic(String command) throws Exception {
        writer.write(command);
        writer.newLine();
        writer.flush();
    }

    public void writeLabel(int labelCount) throws Exception {
        writer.write("label L" + labelCount);
        writer.newLine();
        writer.flush();
    }

    public void writeGoto(int labelCount) throws Exception {
        writer.write("goto L" + labelCount);
        writer.newLine();
        writer.flush();
    }

    public void writeIf(int labelCount) throws Exception {
        writer.write("if-goto L" + labelCount);
        writer.newLine();
        writer.flush();
    }

    public void writeCall(String label, int nArgs) throws Exception {
        writer.write("call " + label + " " + nArgs);
        writer.newLine();
        writer.flush();
    }

    public void writeFunction(String label, int nLocals) throws Exception {
        writer.write("function " + label + " " + nLocals);
        writer.newLine();
        writer.flush();
    }

    public void writeReturn() throws Exception {
        writer.write("return");
        writer.newLine();
        writer.flush();
    }

    public void close() throws Exception {
        writer.close();
    }
}
