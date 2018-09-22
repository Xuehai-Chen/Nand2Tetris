import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;

class CodeWriter {

    private String vmFileName;
    private Integer JUMP_POINT_COUNT = 0;
    private Integer RETURN_ADDRESS_COUNT = 0;
    private BufferedWriter asmFileWriter;
    private HashMap<String, String> localMemoryTable;

    CodeWriter(String asmFileName) throws Exception {
        asmFileWriter = new BufferedWriter(new FileWriter(asmFileName + ".asm"));
        localMemoryTable = new HashMap<>();
        localMemoryTable.put("local", "LCL");
        localMemoryTable.put("argument", "ARG");
        localMemoryTable.put("this", "THIS");
        localMemoryTable.put("that", "THAT");
    }

    void writeInit() throws Exception {
        String lineToWrite = "";
        lineToWrite += "@256\n";
        lineToWrite += "D=A\n";
        lineToWrite += "@SP\n";
        lineToWrite += "M=D\n";
        asmFileWriter.write(lineToWrite);
        asmFileWriter.flush();
        writeCall("Sys.init", 0);
    }

    void setFileName(String vmFileName) {
        this.vmFileName = vmFileName;
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

    void writePushPop(VMTranslator.CommandType commandType, String segment, int index) throws Exception {
        if (commandType == VMTranslator.CommandType.C_PUSH) {
            String lineToWrite = "";
            lineToWrite = getMemorySegmentAddressForPush(lineToWrite, segment, index);
            lineToWrite += "@SP\n";
            lineToWrite += "A=M\n";
            lineToWrite += "M=D\n";
            lineToWrite += "@SP\n";
            lineToWrite += "M=M+1\n";
            asmFileWriter.write(lineToWrite);
        } else if (commandType == VMTranslator.CommandType.C_POP) {
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

    private String getMemorySegmentAddressForPush(String lineToWrite, String segment, int index) throws Exception {
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
        } else if (segment.equals("static")) {
            lineToWrite += "@" + vmFileName + "." + index + "\n";
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
        } else if (segment.equals("temp")) {
            lineToWrite += "@5\n";
            lineToWrite += "A=D+A\n";
            lineToWrite += "D=M\n";
        }
        return lineToWrite;
    }

    private String getMemorySegmentAddressForPop(String lineToWrite, String segment, int index) throws Exception {
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
        } else if (segment.equals("static")) {
            lineToWrite += "@" + vmFileName + "." + index + "\n";
            lineToWrite += "D=A\n";
            return lineToWrite;
        }
        lineToWrite += "@" + index + "\n";
        lineToWrite += "D=A\n";
        if (localMemoryTable.containsKey(segment)) {
            lineToWrite += "@" + localMemoryTable.get(segment) + "\n";
            lineToWrite += "D=D+M\n";
        } else if (segment.equals("temp")) {
            lineToWrite += "@5\n";
            lineToWrite += "D=D+A\n";
        }
        return lineToWrite;
    }

    void writeLabel(String label) throws Exception {
        String lineToWrite = "";
        lineToWrite += "(" + label + ")\n";
        asmFileWriter.write(lineToWrite);
        asmFileWriter.flush();
    }

    void writeGoto(String label) throws Exception {
        String lineToWrite = "";
        lineToWrite += "@" + label + "\n";
        lineToWrite += "0;JMP\n";
        asmFileWriter.write(lineToWrite);
        asmFileWriter.flush();
    }

    void writeIf(String label) throws Exception {
        String lineToWrite = "";
        lineToWrite += "@SP\n";
        lineToWrite += "AM=M-1\n";
        lineToWrite += "D=M\n";
        lineToWrite += "@" + label + "\n";
        lineToWrite += "D;JNE\n";
        asmFileWriter.write(lineToWrite);
        asmFileWriter.flush();
    }

    void writeCall(String functionName, int numArgs) throws Exception {
        String lineToWrite = "";
        //PUSH returnAddress,LCL,ARG,THIS,THAT
        lineToWrite = pushRegister(lineToWrite, "returnAddress");
        lineToWrite = pushRegister(lineToWrite, "LCL");
        lineToWrite = pushRegister(lineToWrite, "ARG");
        lineToWrite = pushRegister(lineToWrite, "THIS");
        lineToWrite = pushRegister(lineToWrite, "THAT");
        //ARG=SP-5-numArgs
        lineToWrite += "@5\n";
        lineToWrite += "D=A\n";
        lineToWrite += "@SP\n";
        lineToWrite += "D=M-D\n";
        lineToWrite += "@" + numArgs + "\n";
        lineToWrite += "D=D-A\n";
        lineToWrite += "@ARG\n";
        lineToWrite += "M=D\n";
        //LCL=SP
        lineToWrite += "@SP\n";
        lineToWrite += "D=M\n";
        lineToWrite += "@LCL\n";
        lineToWrite += "M=D\n";
        //goto functionName
        lineToWrite += "@" + functionName + "\n";
        lineToWrite += "0;JMP\n";
        //set returnAddress;
        lineToWrite += "(RETURN_ADDRESS_" + RETURN_ADDRESS_COUNT + ")\n";
        RETURN_ADDRESS_COUNT++;
        asmFileWriter.write(lineToWrite);
        asmFileWriter.flush();
    }

    private String pushRegister(String lineToWrite, String registerName) {
        if (registerName.equals("returnAddress")) {
            lineToWrite += "@RETURN_ADDRESS_" + RETURN_ADDRESS_COUNT + "\n";
            lineToWrite += "D=A\n";
        } else {
            lineToWrite += "@" + registerName + "\n";
            lineToWrite += "D=M\n";
        }
        lineToWrite += "@SP\n";
        lineToWrite += "A=M\n";
        lineToWrite += "M=D\n";
        lineToWrite += "@SP\n";
        lineToWrite += "M=M+1\n";
        return lineToWrite;
    }

    void writeReturn() throws Exception {
        String lineToWrite = "";
        // endFrame = LCL
        lineToWrite += "@LCL\n";
        lineToWrite += "D=M\n";
        lineToWrite += "@R13\n";
        lineToWrite += "M=D\n";
        //returnAddress = *(endFrame -5)
        lineToWrite += "@5\n";
        lineToWrite += "A=D-A\n";
        lineToWrite += "D=M\n";
        lineToWrite += "@R14\n";
        lineToWrite += "M=D\n";
        //*ARG=pop()
        lineToWrite += "@SP\n";
        lineToWrite += "AM=M-1\n";
        lineToWrite += "D=M\n";
        lineToWrite += "@ARG\n";
        lineToWrite += "A=M\n";
        lineToWrite += "M=D\n";
        //SP=ARG+1
        lineToWrite += "@ARG\n";
        lineToWrite += "D=M+1\n";
        lineToWrite += "@SP\n";
        lineToWrite += "M=D\n";
        //recover registers
        lineToWrite = popRegister(lineToWrite, "THAT", 1);
        lineToWrite = popRegister(lineToWrite, "THIS", 2);
        lineToWrite = popRegister(lineToWrite, "ARG", 3);
        lineToWrite = popRegister(lineToWrite, "LCL", 4);
        //goto returnAddress
        lineToWrite += "@R14\n";
        lineToWrite += "A=M\n";
        lineToWrite += "0;JMP\n";
        asmFileWriter.write(lineToWrite);
        asmFileWriter.flush();
    }

    private String popRegister(String lineToWrite, String registerName, int index) {
        lineToWrite += "@R13\n";
        lineToWrite += "D=M\n";
        lineToWrite += "@" + index + "\n";
        lineToWrite += "A=D-A\n";
        lineToWrite += "D=M\n";
        lineToWrite += "@" + registerName + "\n";
        lineToWrite += "M=D\n";
        return lineToWrite;
    }

    void writeFunction(String functionName, int numLocals) throws Exception {
        String lineToWrite = "";
        lineToWrite += "(" + functionName + ")\n";
        for (int i = 0; i < numLocals; i++) {
            lineToWrite += "@SP\n";
            lineToWrite += "A=M\n";
            lineToWrite += "M=0\n";
            lineToWrite += "@SP\n";
            lineToWrite += "M=M+1\n";
        }
        asmFileWriter.write(lineToWrite);
        asmFileWriter.flush();
    }

    void close() throws Exception {
        asmFileWriter.close();
    }
}
