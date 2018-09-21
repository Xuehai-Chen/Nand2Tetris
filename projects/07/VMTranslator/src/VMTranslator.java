import java.io.*;
import java.util.HashMap;

public class VMTranslator {

    private Parser parser;
    private CodeWriter codeWriter;

    private VMTranslator(String file) throws Exception {
        String fileName = file.split("\\.vm")[0];
        parser = new Parser(file);
        codeWriter = new CodeWriter();
        codeWriter.setFileName(fileName);
    }

    public enum CommandType {
        C_ARITHMETIC,
        C_PUSH, C_POP, C_LABEL, C_GOTO, C_IF, C_FUNCTION,
        C_RETURN, C_CALL
    }

    private class Parser {

        private String line;
        private BufferedReader vmFileReader;


        Parser(String file) throws Exception {
            File vmFile = new File(file);
            vmFileReader = new BufferedReader(new FileReader(vmFile));
        }

        boolean hasMoreCammands() throws Exception {
            return vmFileReader.ready();
        }

        void advance() throws Exception {
            line = vmFileReader.readLine();
            if (line.startsWith("//") || line.isEmpty()) {
                line = vmFileReader.readLine();
            }
        }

        CommandType commandType() throws Exception {
            String command = line.split(" ")[0];
            CommandType commandType;
            switch (command) {
                case "push":
                    commandType = CommandType.C_PUSH;
                    break;
                case "pop":
                    commandType = CommandType.C_POP;
                    break;
                case "label":
                    commandType = CommandType.C_LABEL;
                    break;
                case "goto":
                    commandType = CommandType.C_GOTO;
                    break;
                case "if":
                    commandType = CommandType.C_IF;
                    break;
                case "function":
                    commandType = CommandType.C_FUNCTION;
                    break;
                case "return":
                    commandType = CommandType.C_RETURN;
                    break;
                case "call":
                    commandType = CommandType.C_CALL;
                    break;
                default:
                    commandType = CommandType.C_ARITHMETIC;
            }
            return commandType;
        }

        String arg1() throws Exception {
            String arg1;
            if (commandType() == CommandType.C_ARITHMETIC) {
                arg1 = line.split(" ")[0];
            } else {
                arg1 = line.split(" ")[1];
            }
            return arg1;
        }

        int arg2() {
            return Integer.parseInt(line.split(" ")[2]);
        }
    }

    private class CodeWriter {

        private Integer JUMP_POINT_COUNT = 0;
        private BufferedWriter asmFileWriter;
        private HashMap<String, String> localMemoryTable;
        private HashMap<String, String> globalMemoryTable;

        CodeWriter() {
            localMemoryTable = new HashMap<>();
            localMemoryTable.put("local", "LCL");
            localMemoryTable.put("argument", "ARG");
            localMemoryTable.put("this", "THIS");
            localMemoryTable.put("that", "THAT");

            globalMemoryTable = new HashMap<>();
            globalMemoryTable.put("static", "16");
            globalMemoryTable.put("temp", "5");
        }

        void setFileName(String fileName) throws Exception {
            asmFileWriter = new BufferedWriter(new FileWriter(fileName + ".asm"));
        }

        void writeArithmetic(String command) throws Exception {
            String lineToWrite = "";
            switch (command) {
                case "add":
                    lineToWrite += "@SP\n";
                    lineToWrite += "AM=M-1\n";
                    lineToWrite += "D=M\n";
                    lineToWrite += "A=A-1\n";
                    lineToWrite += "M=D+M\n";
                    asmFileWriter.write(lineToWrite);
                    break;
                case "sub":
                    lineToWrite += "@SP\n";
                    lineToWrite += "AM=M-1\n";
                    lineToWrite += "D=M\n";
                    lineToWrite += "A=A-1\n";
                    lineToWrite += "M=M-D\n";
                    asmFileWriter.write(lineToWrite);
                    break;
                case "neg":
                    lineToWrite += "@SP\n";
                    lineToWrite += "A=M-1\n";
                    lineToWrite += "M=-M\n";
                    asmFileWriter.write(lineToWrite);
                    break;
                case "eq":
                    lineToWrite += "@SP\n";
                    lineToWrite += "AM=M-1\n";
                    lineToWrite += "D=M\n";
                    lineToWrite += "A=A-1\n";
                    lineToWrite += "D=M-D\n";
                    lineToWrite += "@JUMP_POINT_" + JUMP_POINT_COUNT + "\n";
                    lineToWrite += "D;JEQ\n";
                    lineToWrite += "@SP\n";
                    lineToWrite += "A=M-1\n";
                    lineToWrite += "M=0\n";
                    lineToWrite += "@JUMP_POINT_" + (JUMP_POINT_COUNT + 1) + "\n";
                    lineToWrite += "0;JMP\n";
                    lineToWrite += "(JUMP_POINT_" + JUMP_POINT_COUNT + ")\n";
                    JUMP_POINT_COUNT++;
                    lineToWrite += "@SP\n";
                    lineToWrite += "A=M-1\n";
                    lineToWrite += "M=-1\n";
                    lineToWrite += "(JUMP_POINT_" + JUMP_POINT_COUNT + ")\n";
                    JUMP_POINT_COUNT++;
                    asmFileWriter.write(lineToWrite);
                    break;
                case "lt":
                    lineToWrite += "@SP\n";
                    lineToWrite += "AM=M-1\n";
                    lineToWrite += "D=M\n";
                    lineToWrite += "A=A-1\n";
                    lineToWrite += "D=M-D\n";
                    lineToWrite += "@JUMP_POINT_" + JUMP_POINT_COUNT + "\n";
                    lineToWrite += "D;JLT\n";
                    lineToWrite += "@SP\n";
                    lineToWrite += "A=M-1\n";
                    lineToWrite += "M=0\n";
                    lineToWrite += "@JUMP_POINT_" + (JUMP_POINT_COUNT + 1) + "\n";
                    lineToWrite += "0;JMP\n";
                    lineToWrite += "(JUMP_POINT_" + JUMP_POINT_COUNT + ")\n";
                    JUMP_POINT_COUNT++;
                    lineToWrite += "@SP\n";
                    lineToWrite += "A=M-1\n";
                    lineToWrite += "M=-1\n";
                    lineToWrite += "(JUMP_POINT_" + JUMP_POINT_COUNT + ")\n";
                    JUMP_POINT_COUNT++;
                    asmFileWriter.write(lineToWrite);
                    break;
                case "gt":
                    lineToWrite += "@SP\n";
                    lineToWrite += "AM=M-1\n";
                    lineToWrite += "D=M\n";
                    lineToWrite += "A=A-1\n";
                    lineToWrite += "D=M-D\n";
                    lineToWrite += "@JUMP_POINT_" + JUMP_POINT_COUNT + "\n";
                    lineToWrite += "D;JGT\n";
                    lineToWrite += "@SP\n";
                    lineToWrite += "A=M-1\n";
                    lineToWrite += "M=0\n";
                    lineToWrite += "@JUMP_POINT_" + (JUMP_POINT_COUNT + 1) + "\n";
                    lineToWrite += "0;JMP\n";
                    lineToWrite += "(JUMP_POINT_" + JUMP_POINT_COUNT + ")\n";
                    JUMP_POINT_COUNT++;
                    lineToWrite += "@SP\n";
                    lineToWrite += "A=M-1\n";
                    lineToWrite += "M=-1\n";
                    lineToWrite += "(JUMP_POINT_" + JUMP_POINT_COUNT + ")\n";
                    JUMP_POINT_COUNT++;
                    asmFileWriter.write(lineToWrite);
                    break;
                case "and":
                    lineToWrite += "@SP\n";
                    lineToWrite += "AM=M-1\n";
                    lineToWrite += "D=M\n";
                    lineToWrite += "A=A-1\n";
                    lineToWrite += "M=D&M\n";
                    asmFileWriter.write(lineToWrite);
                    break;
                case "or":
                    lineToWrite += "@SP\n";
                    lineToWrite += "AM=M-1\n";
                    lineToWrite += "D=M\n";
                    lineToWrite += "A=A-1\n";
                    lineToWrite += "M=D|M\n";
                    asmFileWriter.write(lineToWrite);
                    break;
                case "not":
                    lineToWrite += "@SP\n";
                    lineToWrite += "A=M-1\n";
                    lineToWrite += "M=!M\n";
                    asmFileWriter.write(lineToWrite);
                    break;
            }
            asmFileWriter.flush();
        }

        void writePushPop(CommandType commandType, String segment, int index) throws Exception {
            if (commandType == CommandType.C_PUSH) {
                String lineToWrite = "";

                lineToWrite = getMemorySegmentAddressForPush(lineToWrite, segment, index);
                lineToWrite += "@SP\n";
                lineToWrite += "A=M\n";
                lineToWrite += "M=D\n";
                lineToWrite += "@SP\n";
                lineToWrite += "M=M+1\n";
                asmFileWriter.write(lineToWrite);
            } else if (commandType == CommandType.C_POP) {
                String lineToWrite = "";
                lineToWrite = getMemorySegmentAddressForPop(lineToWrite, segment, index);
                lineToWrite += "@R13\n";
                lineToWrite += "M=D\n";
                lineToWrite += "@SP\n";
                lineToWrite += "AM=M-1\n";
                lineToWrite += "D=M\n";
                lineToWrite += "@R13\n";
                lineToWrite += "A=M\n";
                lineToWrite += "M=D\n";
                asmFileWriter.write(lineToWrite);
            }
            asmFileWriter.flush();
        }

        String getMemorySegmentAddressForPush(String lineToWrite, String segment, int index) throws Exception {
            if (segment.equals("pointer")) {
                if (index == 0) {
                    lineToWrite += "@THIS\n";

                } else if (index == 1) {
                    lineToWrite += "@THAT\n";
                } else {
                    System.err.println("The index for push pointer is not valid!");
                    return lineToWrite;
                }
                lineToWrite += "D=M\n";
                return lineToWrite;
            }
            lineToWrite += "@" + index + "\n";
            lineToWrite += "D=A\n";
            if (segment.equals("constant")) {
                return lineToWrite;
            } else if (localMemoryTable.containsKey(segment)) {
                lineToWrite += "@" + localMemoryTable.get(segment) + "\n";
                lineToWrite += "A=D+M\n";
                lineToWrite += "D=M\n";
            } else if (globalMemoryTable.containsKey(segment)) {
                lineToWrite += "@" + globalMemoryTable.get(segment) + "\n";
                lineToWrite += "A=D+A\n";
                lineToWrite += "D=M\n";
            }
            return lineToWrite;
        }

        String getMemorySegmentAddressForPop(String lineToWrite, String segment, int index) throws Exception {
            if (segment.equals("pointer")) {
                if (index == 0) {
                    lineToWrite += "@THIS\n";

                } else if (index == 1) {
                    lineToWrite += "@THAT\n";
                } else {
                    System.err.println("The index for pop pointer is not valid!");
                    return lineToWrite;
                }
                lineToWrite += "D=A\n";
                return lineToWrite;
            }
            lineToWrite += "@" + index + "\n";
            lineToWrite += "D=A\n";
            if (localMemoryTable.containsKey(segment)) {
                lineToWrite += "@" + localMemoryTable.get(segment) + "\n";
                lineToWrite += "D=D+M\n";
            } else if (globalMemoryTable.containsKey(segment)) {
                lineToWrite += "@" + globalMemoryTable.get(segment) + "\n";
                lineToWrite += "D=D+A\n";
            }
            return lineToWrite;
        }

        void close() throws Exception {
            asmFileWriter.close();
        }
    }

    private void process() throws Exception {
        while (parser.hasMoreCammands()) {
            parser.advance();
            CommandType commandType = parser.commandType();
            if (commandType == CommandType.C_ARITHMETIC) {
                codeWriter.writeArithmetic(parser.arg1());
            } else if (commandType == CommandType.C_PUSH || commandType == CommandType.C_POP) {
                codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
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
