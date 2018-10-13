import java.io.BufferedWriter;

import java.io.FileWriter;

public class CodeWriter {

    private BufferedWriter writer;

    public CodeWriter(String fileName) throws Exception {
        writer = new BufferedWriter(new FileWriter(fileName + ".vm"));
    }

    public void writePush(String segment, int index) throws Exception {

    }

    public void writePop(String segment, int index) throws Exception {

    }

    public void writeArithmetic(String command) throws Exception {

    }

    public void writeLabel(String label) throws Exception {

    }

    public void writeGoto(String label) throws Exception {

    }

    public void writeIf(String label) throws Exception {

    }

    public void writeCall(String label) throws Exception {

    }

    public void writeFunction(String label) throws Exception {

    }

    public void writeReturn() throws Exception {

    }

    public void close() throws Exception {
        writer.close();
    }
}
