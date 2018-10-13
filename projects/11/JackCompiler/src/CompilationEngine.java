import java.util.HashMap;
import java.util.HashSet;

public class CompilationEngine {

    private Tokenizer tokenizer;
    private String className;
    private CodeWriter codeWriter;
    private SymbolTable symbolTable;
    private int labelCount;
    private HashMap<String, String> arithmeticSymbolMap;

    public CompilationEngine(Tokenizer tokenizer, CodeWriter codeWriter) throws Exception {
        this.tokenizer = tokenizer;
        this.codeWriter = codeWriter;
        symbolTable = new SymbolTable();
        arithmeticSymbolMap = new HashMap<>();
        arithmeticSymbolMap.put("+", "add");
        arithmeticSymbolMap.put("-", "sub");
        arithmeticSymbolMap.put("*", "call Math.multiply 2");
        arithmeticSymbolMap.put("/", "call Math.divide 2");
        arithmeticSymbolMap.put("&", "and");
        arithmeticSymbolMap.put("|", "or");
        arithmeticSymbolMap.put("<", "lt");
        arithmeticSymbolMap.put(">", "gt");
        arithmeticSymbolMap.put("=", "eq");
        arithmeticSymbolMap.put("~", "not");
    }

    public void compileClass() throws Exception {
        eat("keyword");
        className = eat("identifier");
        eat("symbol");
        while (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD && (tokenizer.getToken().equals("static") || tokenizer.getToken().equals("field"))) {
            compileClassVarDec();
        }
        while (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD && (tokenizer.getToken().equals("constructor") || tokenizer.getToken().equals("function") || tokenizer.getToken().equals("method"))) {
            compileSubroutine();
        }
        eat("symbol");
    }

    public void compileClassVarDec() throws Exception {
        String identifierKind = eat("keyword");
        String type = "";
        if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD) {
            type = eat("keyword");
        } else if (tokenizer.tokenType() == Tokenizer.TokenType.IDENTIFIER) {
            type = eat("identifier");
        }
        String name = eat("identifier");
        symbolTable.define(name, type, identifierKind);
        while ((tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL) && tokenizer.getToken().equals(",")) {
            eat("symbol");
            name = eat("identifier");
            symbolTable.define(name, type, identifierKind);
        }
        eat("symbol");
    }

    public void compileSubroutine() throws Exception {
        symbolTable.startSubroutine();
        String subroutineType = eat("keyword");
        if (subroutineType.equals("method")) {
            codeWriter.writePush("argument", 0);
            codeWriter.writePop("pointer", 0);
        } else if (subroutineType.equals("constructor")) {
            codeWriter.writePush("constant", symbolTable.varCount("field") + symbolTable.varCount("static"));
            codeWriter.writeCall("Memory.alloc", 1);
        }
        boolean isVoid = false;
        if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD) {
            isVoid = eat("keyword").equals("void");
        } else if (tokenizer.tokenType() == Tokenizer.TokenType.IDENTIFIER) {
            eat("identifier");
        }
        String name = eat("identifier");
        eat("symbol");
        compileParameterList();
        eat("symbol");
        eat("symbol");
        while ((tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD) && tokenizer.getToken().equals("var")) {
            compileVarDec();
        }
        codeWriter.writeFunction(className + "." + name, symbolTable.varCount("var"));
        compileStatements(isVoid);
        eat("symbol");
    }

    public void compileParameterList() throws Exception {
        //symbolTable.define(className, className, "argument");
        if (tokenizer.getToken().equals("int") || tokenizer.getToken().equals("boolean") || tokenizer.getToken().equals("char") || tokenizer.tokenType() == Tokenizer.TokenType.IDENTIFIER) {
            String type = "";
            String name = "";
            if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD) {
                type = eat("keyword");
            } else if (tokenizer.tokenType() == Tokenizer.TokenType.IDENTIFIER) {
                type = eat("identifier");
            }
            name = eat("identifier");
            symbolTable.define(name, type, "argument");
            while (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && tokenizer.getToken().equals(",")) {
                eat("symbol");
                if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD) {
                    type = eat("keyword");
                } else if (tokenizer.tokenType() == Tokenizer.TokenType.IDENTIFIER) {
                    type = eat("identifier");
                }
                name = eat("identifier");
                symbolTable.define(name, type, "argument");
            }
        }
    }

    public void compileVarDec() throws Exception {
        eat("keyword");
        String type = "";
        String name = "";
        if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD) {
            type = eat("keyword");
        } else {
            type = eat("identifier");
        }
        name = eat("identifier");
        symbolTable.define(name, type, "var");
        while (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && tokenizer.getToken().equals(",")) {
            eat("symbol");
            name = eat("identifier");
            symbolTable.define(name, type, "var");
        }
        eat("symbol");
    }

    public void compileStatements(boolean isVoid) throws Exception {
        while (true) {
            if (tokenizer.getToken().equals("let")) {
                compileLet();
            } else if (tokenizer.getToken().equals("if")) {
                compileIf();
            } else if (tokenizer.getToken().equals("while")) {
                compileWhile();
            } else if (tokenizer.getToken().equals("do")) {
                compileDo();
            } else if (tokenizer.getToken().equals("return")) {
                if (isVoid) {
                    codeWriter.writePush("constant", 0);
                }
                compileReturn();
            } else {
                break;
            }
        }
    }

    public void compileDo() throws Exception {
        eat("keyword");
        String funcName = eat("identifier");
        if (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && tokenizer.getToken().equals(".")) {
            funcName += eat("symbol");
            funcName += eat("identifier");
        }
        eat("symbol");
        int count = compileExpressionList();
        eat("symbol");
        eat("symbol");
        codeWriter.writeCall(funcName, count);
        codeWriter.writePop("temp", 0);
    }

    public void compileLet() throws Exception {
        eat("keyword");
        String variable = eat("identifier");
        if (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && tokenizer.getToken().equals("[")) {
            eat("symbol");
            compileExpression();
            eat("symbol");
            //TODO
        }
        eat("symbol");
        compileExpression();
        eat("symbol");
        codeWriter.writePop(symbolTable.kindOf(variable), symbolTable.indexOf(variable));
    }

    public void compileWhile() throws Exception {
        codeWriter.writeLabel(labelCount);
        labelCount++;
        eat("keyword");
        eat("symbol");
        compileExpression();
        codeWriter.writeArithmetic("not");
        int localLabelCount = labelCount;
        codeWriter.writeIf(localLabelCount);
        labelCount++;
        eat("symbol");
        eat("symbol");
        compileStatements(false);
        eat("symbol");
        codeWriter.writeGoto(localLabelCount - 1);
        codeWriter.writeLabel(localLabelCount);
    }

    public void compileReturn() throws Exception {
        eat("keyword");
        if (!tokenizer.getToken().equals(";")) {
            compileExpression();
        }
        codeWriter.writeReturn();
        eat("symbol");
    }

    public void compileIf() throws Exception {
        eat("keyword");
        eat("symbol");
        compileExpression();
        codeWriter.writeArithmetic("not");
        int localLabelCount = labelCount;
        codeWriter.writeIf(labelCount);
        labelCount++;
        eat("symbol");
        eat("symbol");
        compileStatements(false);
        eat("symbol");
        if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD && tokenizer.getToken().equals("else")) {
            codeWriter.writeGoto(labelCount + 1);
            codeWriter.writeLabel(localLabelCount);
            labelCount++;
            localLabelCount = labelCount;
            eat("keyword");
            eat("symbol");
            compileStatements(false);
            eat("symbol");
        }
        codeWriter.writeLabel(localLabelCount);
    }

    public void compileExpression() throws Exception {
        compileTerm();
        while (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && (tokenizer.getToken().equals("+") || tokenizer.getToken().equals("-") || tokenizer.getToken().equals("*") || tokenizer.getToken().equals("/") || tokenizer.getToken().equals("&") || tokenizer.getToken().equals("|") || tokenizer.getToken().equals(">") || tokenizer.getToken().equals("<") || tokenizer.getToken().equals("="))) {
            String command = eat("symbol");
            compileTerm();
            codeWriter.writeArithmetic(arithmeticSymbolMap.get(command));
        }
    }

    public void compileTerm() throws Exception {
        if (tokenizer.tokenType() == Tokenizer.TokenType.INT_CONST) {
            String constant = eat("integerConstant");
            codeWriter.writePush("constant", Integer.parseInt(constant));
        } else if (tokenizer.tokenType() == Tokenizer.TokenType.STRING_CONST) {
            eat("stringConstant");
        } else if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD && (tokenizer.getToken().equals("true") || tokenizer.getToken().equals("false") || tokenizer.getToken().equals("null") || tokenizer.getToken().equals("this"))) {
            String value = eat("keyword");
            if (value.equals("true")) {
                codeWriter.writePush("constant", 1);
                codeWriter.writeArithmetic("neg");
            } else if (value.equals("false")) {
                codeWriter.writePush("constant", 0);
            }
        } else if (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && tokenizer.getToken().equals("(")) {
            eat("symbol");
            compileExpression();
            eat("symbol");
        } else if (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && (tokenizer.getToken().equals("-") || tokenizer.getToken().equals("~"))) {
            String command = eat("symbol");
            compileTerm();
            codeWriter.writeArithmetic(command.equals("-") ? "neg" : "not");
        } else if (tokenizer.tokenType() == Tokenizer.TokenType.IDENTIFIER) {
            String name = "";
            name = eat("identifier");
            if (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && tokenizer.getToken().equals("[")) {
                eat("symbol");
                compileExpression();
                eat("symbol");
            } else if (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && (tokenizer.getToken().equals(".") || tokenizer.getToken().equals("("))) {
                if (tokenizer.getToken().equals(".")) {
                    if (!symbolTable.kindOf(name).equals("undefined")) {
                        codeWriter.writePush(symbolTable.kindOf(name), symbolTable.indexOf(name));
                    }
                    name += eat("symbol");
                    name += eat("identifier");
                }
                eat("symbol");
                int count = compileExpressionList();
                eat("symbol");
                codeWriter.writeCall(name, count);
            } else {
                codeWriter.writePush(symbolTable.kindOf(name), symbolTable.indexOf(name));
            }

        }
    }

    public int compileExpressionList() throws Exception {
        int count = 0;
        if (!tokenizer.getToken().equals(")")) {
            compileExpression();
            count++;
            while (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && tokenizer.getToken().equals(",")) {
                eat("symbol");
                compileExpression();
                count++;
            }
        }
        return count;
    }

    private String eat(String expectationType) throws Exception {
        if (tokenizer.tokenType() == null && tokenizer.hasMoreTokens()) {
            tokenizer.advance();
        }
        String tag = Tokenizer.getTag(tokenizer);
        String value = tokenizer.getToken();
        if (!tag.equals(expectationType)) {
            System.out.println("======== the program has a syntax error: " + expectationType + "  " + value);
        } else {
            if (tokenizer.hasMoreTokens()) {
                tokenizer.advance();
            }
        }
        return value;
    }
}
