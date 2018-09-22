import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VMTranslator {

    private ArrayList<File> files = new ArrayList<>();

    private CodeWriter codeWriter;

    private VMTranslator(String path) throws Exception {
        String fileName = "";
        File file = new File(path);
        if (file.isDirectory()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    Pattern p = Pattern.compile(".*vm$");
                    Matcher m = p.matcher(name);
                    return m.matches();
                }
            };
            files.addAll(Arrays.asList(file.listFiles(filter)));
            fileName += path + "/" + file.getName();
        } else {
            files.add(file);
            fileName = path.split(".vm")[0];
        }
        codeWriter = new CodeWriter(fileName);
    }

    public enum CommandType {
        C_ARITHMETIC,
        C_PUSH, C_POP, C_LABEL, C_GOTO, C_IF, C_FUNCTION,
        C_RETURN, C_CALL
    }

    private void processSingleFile(Parser parser) throws Exception {
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
            //System.out.println(commandType);
        }
    }

    private void process() throws Exception {
        if (files.size() == 1) {
            Parser parser = new Parser(files.get(0));
            processSingleFile(parser);
        } else {
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                if (file.getName().equals("Sys.vm")) {
                    codeWriter.setFileName(file.getName().split(".vm")[0]);
                    codeWriter.writeInit();
                    processSingleFile(new Parser(file));
                    files.remove(file);
                }
            }
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                codeWriter.setFileName(file.getName().split(".vm")[0]);
                processSingleFile(new Parser(file));
            }
        }
        codeWriter.close();
    }

    public static void main(String[] args) throws Exception {
        VMTranslator translator = new VMTranslator(args[0]);
        translator.process();
    }
}
