import java.io.*;
import java.util.HashMap;

public class VMTranslator {

    private Parser parser;
    private CodeWriter codeWriter;

    private VMTranslator(String file) throws Exception {
        String fileName = file.split("\\.vm")[0];
        parser = new Parser(file);
        codeWriter = new CodeWriter();
        codeWriter.writeInit();
        codeWriter.setFileName(fileName);
    }

    public enum CommandType {
        C_ARITHMETIC,
        C_PUSH, C_POP, C_LABEL, C_GOTO, C_IF, C_FUNCTION,
        C_RETURN, C_CALL
    }

    private void process() throws Exception {
        while (parser.hasMoreCammands()) {
            parser.advance();
            CommandType commandType = parser.commandType();
            if (commandType == CommandType.C_ARITHMETIC) {
                codeWriter.writeArithmetic(parser.arg1());
            } else if (commandType == CommandType.C_PUSH || commandType == CommandType.C_POP) {
                codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
            } else if (commandType == CommandType.C_LABEL) {
                codeWriter.writeLabel(parser.arg1());
            } else if (commandType == CommandType.C_GOTO) {
                codeWriter.writeGoto(parser.arg1());
            } else if (commandType == CommandType.C_IF) {
                codeWriter.writeIf(parser.arg1());
            } else if (commandType == CommandType.C_CALL) {
                codeWriter.writeCall(parser.arg1(), parser.arg2());
            } else if (commandType == CommandType.C_RETURN) {
                codeWriter.writeReturn();
            } else if (commandType == CommandType.C_FUNCTION) {
                codeWriter.writeFunction(parser.arg1(), parser.arg2());
            } else {
                System.err.println("The command is not valid!");
            }
            System.out.println(commandType);
        }
        codeWriter.close();
    }

    public static void main(String[] args) throws Exception {
        VMTranslator translator = new VMTranslator(args[0]);
        translator.process();
    }
}
