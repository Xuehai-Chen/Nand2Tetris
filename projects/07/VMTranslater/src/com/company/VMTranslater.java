package com.company;

import java.io.*;
import java.util.HashMap;

public class VMTranslater {

    private String fileName;
    private File vmFile;
    private File formatedFile;
    private File asmFile;
    private Integer JUMP_POINT_COUNT = 0;
    private Integer SP = 256;
    private Integer LCL_BASE = 1;
    private Integer ARG_BASE = 2;
    private Integer THIS_BASE = 3;
    private Integer THAT_BASE = 4;
    private HashMap<String, String> commandTable;
    private HashMap<String, String> memorySegmentBaseTable;

    VMTranslater(String file) throws Exception {
        fileName = file.split("\\.vm")[0];
        vmFile = new File(file);
        asmFile = new File(fileName + ".asm");

        commandTable = new HashMap<>();
        commandTable.put("push", "push");
        commandTable.put("pop", "pop");
        commandTable.put("add", "+");
        commandTable.put("sub", "-");
        commandTable.put("neg", "-");
        commandTable.put("eq", "==");
        commandTable.put("gt", ">");
        commandTable.put("lt", "<");
        commandTable.put("and", "&");
        commandTable.put("or", "|");
        commandTable.put("not", "!");

        memorySegmentBaseTable = new HashMap<>();
        memorySegmentBaseTable.put("local", "R1");
        memorySegmentBaseTable.put("argument", "R2");
        memorySegmentBaseTable.put("this", "R3");
        memorySegmentBaseTable.put("that", "R4");
        memorySegmentBaseTable.put("temp", "R5");
    }

    private void format() throws Exception {
        BufferedReader vmFileReader = new BufferedReader(new FileReader(vmFile));
        formatedFile = new File(fileName + ".fmtd");
        BufferedWriter formatedFileWriter = new BufferedWriter(new FileWriter(formatedFile));
        int asmLineNum = 0;
        String line;
        while ((line = vmFileReader.readLine()) != null) {
            if (!line.startsWith("//") && !line.isEmpty()) {
                line = line.split("//")[0];
                asmLineNum++;
                if (asmLineNum == 1) {
                    formatedFileWriter.write(line);
                } else {
                    formatedFileWriter.newLine();
                    formatedFileWriter.write(line);
                }
            }
        }
        formatedFileWriter.flush();
        System.out.println(asmLineNum);
    }

    private void process() throws Exception {
        BufferedReader fmtdFileReader = new BufferedReader(new FileReader(formatedFile));
        BufferedWriter asmFileWriter = new BufferedWriter(new FileWriter(asmFile));
        String line;
        int asmLineNum = 0;
//        asmFileWriter.write("@256\n");
//        asmFileWriter.write("D=A\n");
//        asmFileWriter.write("@R0\n");
//        asmFileWriter.write("M=D\n");
        while ((line = fmtdFileReader.readLine()) != null) {
            String[] commands = line.split(" ");
            if (commandTable.containsKey(commands[0])) {
                if (commands.length == 3) {
                    String command = commands[0];
                    String memorySegment = commands[1];
                    Integer i = Integer.parseInt(commands[2]);
                    if (command.equals("push")) {
                        String lineToWrite = "";
                        lineToWrite += "@" + i + "\n";
                        lineToWrite += "D=A\n";
                        if (memorySegmentBaseTable.containsKey(memorySegment)) {
                            lineToWrite += "@" + memorySegmentBaseTable.get(memorySegment) + "\n";
                            lineToWrite += "A=M+D\n";
                            lineToWrite += "D=M\n";
                        }
                        lineToWrite += "@R0\n";
                        lineToWrite += "A=M\n";
                        lineToWrite += "M=D\n";
                        lineToWrite += "@R0\n";
                        lineToWrite += "M=M+1\n";
                        asmFileWriter.write(lineToWrite);
                    } else if (command.equals("pop")) {
                        String lineToWrite = "";
                        lineToWrite += "@" + i + "\n";
                        lineToWrite += "D=A\n";
                        lineToWrite += "@" + memorySegmentBaseTable.get(memorySegment) + "\n";
                        //TODO map temp,static,constant,pointer to something else
                        lineToWrite += "D=M+D\n";
                        lineToWrite += "@R13\n";
                        lineToWrite += "M=D\n";
                        lineToWrite += "@R0\n";
                        lineToWrite += "AM=M-1\n";
                        lineToWrite += "D=M\n";
                        lineToWrite += "@R13\n";
                        lineToWrite += "A=M\n";
                        lineToWrite += "M=D\n";
                        asmFileWriter.write(lineToWrite);
                    }
                } else if (commands.length == 1) {
                    String lineToWrite = "";
                    switch (commands[0]) {
                        case "add":
                            lineToWrite += "@R0\n";
                            lineToWrite += "AM=M-1\n";
                            lineToWrite += "D=M\n";
                            lineToWrite += "A=A-1\n";
                            lineToWrite += "M=D+M\n";
                            asmFileWriter.write(lineToWrite);
                            break;
                        case "sub":
                            lineToWrite += "@R0\n";
                            lineToWrite += "AM=M-1\n";
                            lineToWrite += "D=M\n";
                            lineToWrite += "A=A-1\n";
                            lineToWrite += "M=M-D\n";
                            asmFileWriter.write(lineToWrite);
                            break;
                        case "neg":
                            lineToWrite += "@R0\n";
                            lineToWrite += "A=M-1\n";
                            lineToWrite += "M=-M\n";
                            asmFileWriter.write(lineToWrite);
                            break;
                        case "eq":
                            lineToWrite += "@R0\n";
                            lineToWrite += "AM=M-1\n";
                            lineToWrite += "D=M\n";
                            lineToWrite += "A=A-1\n";
                            lineToWrite += "D=M-D\n";
                            lineToWrite += "@JUMP_POINT_" + JUMP_POINT_COUNT + "\n";
                            lineToWrite += "D;JEQ\n";
                            lineToWrite += "@R0\n";
                            lineToWrite += "A=M-1\n";
                            lineToWrite += "M=0\n";
                            lineToWrite += "@JUMP_POINT_" + (JUMP_POINT_COUNT + 1) + "\n";
                            lineToWrite += "0;JMP\n";
                            lineToWrite += "(JUMP_POINT_" + JUMP_POINT_COUNT + ")\n";
                            JUMP_POINT_COUNT++;
                            lineToWrite += "@R0\n";
                            lineToWrite += "A=M-1\n";
                            lineToWrite += "M=-1\n";
                            lineToWrite += "(JUMP_POINT_" + JUMP_POINT_COUNT + ")\n";
                            JUMP_POINT_COUNT++;
                            asmFileWriter.write(lineToWrite);
                            break;
                        case "lt":
                            lineToWrite += "@R0\n";
                            lineToWrite += "AM=M-1\n";
                            lineToWrite += "D=M\n";
                            lineToWrite += "A=A-1\n";
                            lineToWrite += "D=M-D\n";
                            lineToWrite += "@JUMP_POINT_" + JUMP_POINT_COUNT + "\n";
                            lineToWrite += "D;JLT\n";
                            lineToWrite += "@R0\n";
                            lineToWrite += "A=M-1\n";
                            lineToWrite += "M=0\n";
                            lineToWrite += "@JUMP_POINT_" + (JUMP_POINT_COUNT + 1) + "\n";
                            lineToWrite += "0;JMP\n";
                            lineToWrite += "(JUMP_POINT_" + JUMP_POINT_COUNT + ")\n";
                            JUMP_POINT_COUNT++;
                            lineToWrite += "@R0\n";
                            lineToWrite += "A=M-1\n";
                            lineToWrite += "M=-1\n";
                            lineToWrite += "(JUMP_POINT_" + JUMP_POINT_COUNT + ")\n";
                            JUMP_POINT_COUNT++;
                            asmFileWriter.write(lineToWrite);
                            break;
                        case "gt":
                            lineToWrite += "@R0\n";
                            lineToWrite += "AM=M-1\n";
                            lineToWrite += "D=M\n";
                            lineToWrite += "A=A-1\n";
                            lineToWrite += "D=M-D\n";
                            lineToWrite += "@JUMP_POINT_" + JUMP_POINT_COUNT + "\n";
                            lineToWrite += "D;JGT\n";
                            lineToWrite += "@R0\n";
                            lineToWrite += "A=M-1\n";
                            lineToWrite += "M=0\n";
                            lineToWrite += "@JUMP_POINT_" + (JUMP_POINT_COUNT + 1) + "\n";
                            lineToWrite += "0;JMP\n";
                            lineToWrite += "(JUMP_POINT_" + JUMP_POINT_COUNT + ")\n";
                            JUMP_POINT_COUNT++;
                            lineToWrite += "@R0\n";
                            lineToWrite += "A=M-1\n";
                            lineToWrite += "M=-1\n";
                            lineToWrite += "(JUMP_POINT_" + JUMP_POINT_COUNT + ")\n";
                            JUMP_POINT_COUNT++;
                            asmFileWriter.write(lineToWrite);
                            break;
                        case "and":
                            lineToWrite += "@R0\n";
                            lineToWrite += "AM=M-1\n";
                            lineToWrite += "D=M\n";
                            lineToWrite += "A=A-1\n";
                            lineToWrite += "M=D&M\n";
                            asmFileWriter.write(lineToWrite);
                            break;
                        case "or":
                            lineToWrite += "@R0\n";
                            lineToWrite += "AM=M-1\n";
                            lineToWrite += "D=M\n";
                            lineToWrite += "A=A-1\n";
                            lineToWrite += "M=D|M\n";
                            asmFileWriter.write(lineToWrite);
                            break;
                        case "not":
                            lineToWrite += "@R0\n";
                            lineToWrite += "A=M-1\n";
                            lineToWrite += "M=!M\n";
                            asmFileWriter.write(lineToWrite);
                            break;
                    }
                }
            } else {
                System.out.println("the command is not defined:" + line);
            }
        }
        asmFileWriter.flush();
        fmtdFileReader.close();
        asmFileWriter.close();
    }

    public static void main(String[] args) throws Exception {
        VMTranslater translater = new VMTranslater(args[0]);
        translater.format();
        translater.process();
        translater.formatedFile.delete();
    }
}
