package com.company;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assembler {

    private String fileName;
    private HashMap<String, Integer> symbolTable = new HashMap<>();
    private HashMap<String, String> compTable = new HashMap<>();
    private HashMap<String, String> destTable = new HashMap<>();
    private HashMap<String, String> jumpTable = new HashMap<>();
    private File asmFile;
    private File formatedFile;
    private File preProcessedFile;
    private File hackFile;
    private int lastVariableAddress = 16;

    Assembler(String fileName) throws Exception {
        int asmLeftIndex = fileName.lastIndexOf("/");
        int asmRightIndex = fileName.lastIndexOf(".");
        this.fileName = fileName.substring(asmLeftIndex + 1, asmRightIndex);
        asmFile = new File(fileName);
        //initialize symbolTable
        symbolTable.put("R0", 0);
        symbolTable.put("R1", 1);
        symbolTable.put("R2", 2);
        symbolTable.put("R3", 3);
        symbolTable.put("R4", 4);
        symbolTable.put("R5", 5);
        symbolTable.put("R6", 6);
        symbolTable.put("R7", 7);
        symbolTable.put("R8", 8);
        symbolTable.put("R9", 9);
        symbolTable.put("R10", 10);
        symbolTable.put("R11", 11);
        symbolTable.put("R12", 12);
        symbolTable.put("R13", 13);
        symbolTable.put("R14", 14);
        symbolTable.put("R15", 15);
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
        //initialize compTable
        compTable.put("0", "101010");
        compTable.put("1", "111111");
        compTable.put("-1", "111010");
        compTable.put("D", "001100");
        compTable.put("A", "110000");
        compTable.put("!D", "001101");
        compTable.put("!A", "110001");
        compTable.put("-D", "001111");
        compTable.put("-A", "110011");
        compTable.put("D+1", "011111");
        compTable.put("1+D", "011111");
        compTable.put("A+1", "110111");
        compTable.put("1+A", "110111");
        compTable.put("D-1", "001110");
        compTable.put("A-1", "110010");
        compTable.put("D+A", "000010");
        compTable.put("A+D", "000010");
        compTable.put("D-A", "010011");
        compTable.put("A-D", "000111");
        compTable.put("D&A", "000000");
        compTable.put("A&D", "000000");
        compTable.put("D|A", "010101");
        compTable.put("A|D", "010101");
        compTable.put("M", "110000");
        compTable.put("!M", "110001");
        compTable.put("-M", "110011");
        compTable.put("M+1", "110111");
        compTable.put("1+M", "110111");
        compTable.put("M-1", "110010");
        compTable.put("D+M", "000010");
        compTable.put("M+D", "000010");
        compTable.put("D-M", "010011");
        compTable.put("M-D", "000111");
        compTable.put("D&M", "000000");
        compTable.put("M&D", "000000");
        compTable.put("D|M", "010101");
        compTable.put("M|D", "010101");
        //initialize destTable
        destTable.put("null", "000");
        destTable.put("M", "001");
        destTable.put("D", "010");
        destTable.put("MD", "011");
        destTable.put("A", "100");
        destTable.put("AM", "101");
        destTable.put("AD", "110");
        destTable.put("AMD", "111");
        //initialize jumpTable
        jumpTable.put("null", "000");
        jumpTable.put("JGT", "001");
        jumpTable.put("JEQ", "010");
        jumpTable.put("JGE", "011");
        jumpTable.put("JLT", "100");
        jumpTable.put("JNE", "101");
        jumpTable.put("JLE", "110");
        jumpTable.put("JMP", "111");
    }

    private void format() throws Exception {
        BufferedReader asmFileReader = new BufferedReader(new FileReader(asmFile));
        formatedFile = new File(fileName + ".fmtd");
        BufferedWriter formatedFileWriter = new BufferedWriter(new FileWriter(formatedFile));
        int asmLineNum = 0;
        String line;
        while ((line = asmFileReader.readLine()) != null) {
            if (!line.startsWith("//") && !line.isEmpty()) {
                line = line.split("//")[0];
                if (line.contains("(") && line.contains(")")) {
                    int leftParenthesisIndex = line.indexOf("(");
                    int rightParenthesisIndex = line.indexOf(")");
                    String label = line.substring(leftParenthesisIndex + 1, rightParenthesisIndex);
                    symbolTable.put(label, asmLineNum);
                    System.out.println("the label is " + label + " and current line num is " + asmLineNum);
                } else {
                    asmLineNum++;
                    if (asmLineNum == 1) {
                        line = line.replaceAll(" ", "");
                        formatedFileWriter.write(line);
                    } else {
                        line = line.replaceAll(" ", "");
                        formatedFileWriter.newLine();
                        formatedFileWriter.write(line);
                    }
                }
            }
        }
        formatedFileWriter.flush();
        System.out.println(asmLineNum);
    }

    private void preProcess() throws Exception {
        BufferedReader formatedFileReader = new BufferedReader(new FileReader(formatedFile));
        preProcessedFile = new File(fileName + ".prpcsd");
        BufferedWriter preProcessedFileWriter = new BufferedWriter(new FileWriter(preProcessedFile));
        int formatedFileLineNum = 0;
        String line;
        while ((line = formatedFileReader.readLine()) != null) {
            if (line.contains("@")) {
                String symbol = line.split("@")[1];
                int value;
                Pattern p = Pattern.compile("[0-9]*");
                Matcher m = p.matcher(symbol);
                if (m.matches()) {
                    value = Integer.parseInt(symbol);
                } else if (symbolTable.containsKey(symbol)) {
                    value = symbolTable.get(symbol);
                } else {
                    value = lastVariableAddress++;
                    symbolTable.put(symbol, value);
                }
                line = "@" + value;
            }
            if (formatedFileLineNum == 0) {
                preProcessedFileWriter.write(line);
            } else {
                preProcessedFileWriter.newLine();
                preProcessedFileWriter.write(line);
            }
            formatedFileLineNum++;
        }
        preProcessedFileWriter.flush();
    }

    private void process() throws Exception {
        hackFile = new File(fileName + ".hack");
        BufferedWriter hackFileWriter = new BufferedWriter(new FileWriter(hackFile));
        BufferedReader preProcessedFileReader = new BufferedReader(new FileReader(preProcessedFile));
        String line;
        int hackLineNum = 0;
        while ((line = preProcessedFileReader.readLine()) != null) {
            String binaryInstruction;
            if (line.startsWith("@")) {
                binaryInstruction = "0";
                int address = Integer.parseInt(line.split("@")[1]);
                String binaryAddress = Integer.toBinaryString(address);
                int size = binaryAddress.length();
                if (size < 15) {
                    for (int i = 0; i < 15 - size; i++) {
                        binaryInstruction += "0";
                    }
                } else if (address > 32768) {
                    System.out.println("the address " + address + " is out of boundary, binary address is " + binaryAddress);
                }
                binaryInstruction += binaryAddress;
                //System.out.println("the binary string is " + binaryInstruction);
                if (hackLineNum == 0) {
                    hackFileWriter.write(binaryInstruction);
                } else {
                    hackFileWriter.newLine();
                    hackFileWriter.write(binaryInstruction);
                }
                hackLineNum++;
            } else {
                binaryInstruction = "111";
                try {
                    String comp;
                    String jump;
                    String dest;
                    if (line.contains("=")) {
                        dest = line.split("=")[0];
                        if (line.contains(";")) {
                            comp = line.split("=")[1].split(";")[0];
                            jump = line.split("=")[1].split(";")[1];
                        } else {
                            jump = "null";
                            comp = line.split("=")[1];
                        }
                    } else {
                        dest = "null";
                        if (line.contains(";")) {
                            comp = line.split(";")[0];
                            jump = line.split(";")[1];
                        } else {
                            jump = "null";
                            comp = "0";
                        }
                    }
                    if (comp.contains("M")) {
                        binaryInstruction += "1";
                    } else {
                        binaryInstruction += "0";
                    }
                    if (compTable.containsKey(comp)) {
                        binaryInstruction += compTable.get(comp);
                    } else {
                        System.out.println("the comp cannot not be assembled: " + comp);
                    }
                    if (destTable.containsKey(dest)) {
                        binaryInstruction += destTable.get(dest);
                    } else {
                        System.out.println("the dest cannot not be assembled: " + dest);
                    }
                    if (jumpTable.containsKey(jump)) {
                        binaryInstruction += jumpTable.get(jump);
                    } else {
                        System.out.println("the jump cannot not be assembled: " + dest);
                    }
                } catch (Exception ex) {
                    System.out.println("the line cannot not be assembled: " + line + ex);
                }
                if (hackLineNum == 0) {
                    hackFileWriter.write(binaryInstruction);
                } else {
                    hackFileWriter.newLine();
                    hackFileWriter.write(binaryInstruction);
                }
                hackLineNum++;
            }
            System.out.println("the result of assembling line is " + binaryInstruction + ", line number is " + hackLineNum);
        }
        hackFileWriter.flush();
        hackFileWriter.close();
    }

    private static void printTables(Iterator it, HashMap table, String tableName) {
        System.out.println();
        System.out.println("==== Content of " + tableName + " ====");
        while (it.hasNext()) {
            String symbol = it.next().toString();
            System.out.println(symbol + "," + table.get(symbol));
        }
        System.out.println("==== Content of " + tableName + " ====");
        System.out.println();
    }

    public static void main(String[] args) throws Exception {
        // write your code here
        Assembler assembler = new Assembler(args[0]);
        Iterator it;
        it = assembler.symbolTable.keySet().iterator();
        printTables(it, assembler.symbolTable, "Symbol Table");
        it = assembler.compTable.keySet().iterator();
        printTables(it, assembler.compTable, "Comp Table");
        it = assembler.destTable.keySet().iterator();
        printTables(it, assembler.destTable, "Dest Table");
        it = assembler.jumpTable.keySet().iterator();
        printTables(it, assembler.jumpTable, "Jump Table");
        assembler.format();
        assembler.preProcess();
        assembler.process();
        assembler.formatedFile.delete();
        assembler.preProcessedFile.delete();
    }
}
