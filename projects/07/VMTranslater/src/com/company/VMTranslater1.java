package com.company;

import java.io.*;
import java.util.HashMap;

public class VMTranslater1 {

    private String fileName;
    private File vmFile;
    private File formatedFile;
    private File asmFile;
    private Integer SP = 256;
    private Integer LCL_BASE = 1;
    private Integer ARG_BASE = 2;
    private Integer THIS_BASE = 3;
    private Integer THAT_BASE = 4;
    private HashMap<String, String> commandTable;
    private HashMap<String, String> memorySegmentBaseTable;

    VMTranslater1(String file) throws Exception {
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
        asmFileWriter.write("@256");
        asmFileWriter.write("\nD=A");
        asmFileWriter.write("\n@R0");
        asmFileWriter.write("\nM=D");
        while ((line = fmtdFileReader.readLine()) != null) {
            String[] commands = line.split(" ");
            if (commandTable.containsKey(commands[0])) {
                if (commands.length == 3) {
                    String command = commands[0];
                    String memorySegment = commands[1];
                    Integer i = Integer.parseInt(commands[2]);
                    if (command.equals("push")) {
                        String lineToWrite;
                        lineToWrite = "\n@" + i;
                        asmFileWriter.write(lineToWrite);
                        lineToWrite = "\nD=A";
                        if (memorySegmentBaseTable.containsKey(memorySegment)) {
                            asmFileWriter.write(lineToWrite);
                            lineToWrite = "\n@" + memorySegmentBaseTable.get(memorySegment);
                            asmFileWriter.write(lineToWrite);
                            lineToWrite = "\nA=M+D";
                            asmFileWriter.write(lineToWrite);
                            lineToWrite = "\nD=M";
                            asmFileWriter.write(lineToWrite);
                        }
                        lineToWrite = "\n@R0";
                        asmFileWriter.write(lineToWrite);
                        lineToWrite = "\nA=M";
                        asmFileWriter.write(lineToWrite);
                        lineToWrite = "\nM=D";
                        asmFileWriter.write(lineToWrite);
                        lineToWrite = "\n@R0";
                        asmFileWriter.write(lineToWrite);
                        lineToWrite = "\nM=M+1";
                        asmFileWriter.write(lineToWrite);
                    } else if (command.equals("pop")) {
                        String lineToWrite;
                        //TODO
                        lineToWrite = "\n@" + i;
                        asmFileWriter.write(lineToWrite);
                        lineToWrite = "\nD=A";
                        asmFileWriter.write(lineToWrite);
                        lineToWrite = "\n@" + memorySegmentBaseTable.get(memorySegment);
                        asmFileWriter.write(lineToWrite);
                        lineToWrite = "\nD=M+D";
                        asmFileWriter.write(lineToWrite);
                        lineToWrite = "\n@R0";
                        asmFileWriter.write(lineToWrite);
                        lineToWrite = "\nM=M-1";
                        asmFileWriter.write(lineToWrite);
                        lineToWrite = "\nA=M";
                        asmFileWriter.write(lineToWrite);
                        lineToWrite = "\n@R0";
                        asmFileWriter.write(lineToWrite);
                        lineToWrite = "\nD=M";
                        asmFileWriter.write(lineToWrite);
                    }
                } else if (commands.length == 1) {

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
        VMTranslater1 translater = new VMTranslater1(args[0]);
        translater.format();
        translater.process();
        //translater.formatedFile.delete();
    }
}
