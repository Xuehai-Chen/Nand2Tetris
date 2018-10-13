import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class CompilationEngine {

    private Tokenizer tokenizer;
    private Document doc;
    private Element rootElement;

    public CompilationEngine(Tokenizer tokenizer) throws Exception {
        this.tokenizer = tokenizer;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        doc = dBuilder.newDocument();
        rootElement = doc.createElement("class");
        doc.appendChild(rootElement);
    }

    public void compileClass() throws Exception {
        eat("keyword", rootElement);
        eat("identifier", rootElement);
        eat("symbol", rootElement);
        while (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD && (tokenizer.getToken().equals("static") || tokenizer.getToken().equals("field"))) {
            compileClassVarDec(rootElement);
        }
        while (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD && (tokenizer.getToken().equals("constructor") || tokenizer.getToken().equals("function") || tokenizer.getToken().equals("method"))) {
            compileSubroutine(rootElement);
        }
        eat("symbol", rootElement);
    }

    public void compileClassVarDec(Element root) throws Exception {
        Element classVarDecElement = doc.createElement("classVarDec");
        eat("keyword", classVarDecElement);
        if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD) {
            eat("keyword", classVarDecElement);
        } else if (tokenizer.tokenType() == Tokenizer.TokenType.IDENTIFIER) {
            eat("identifier", classVarDecElement);
        }
        eat("identifier", classVarDecElement);
        while ((tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL) && tokenizer.getToken().equals(",")) {
            eat("symbol", classVarDecElement);
            eat("identifier", classVarDecElement);
        }
        eat("symbol", classVarDecElement);
        root.appendChild(classVarDecElement);
    }

    public void compileSubroutine(Element root) throws Exception {
        Element subroutineElement = doc.createElement("subroutineDec");
        eat("keyword", subroutineElement);
        if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD) {
            eat("keyword", subroutineElement);
        } else if (tokenizer.tokenType() == Tokenizer.TokenType.IDENTIFIER) {
            if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD) {
                eat("keyword", subroutineElement);
            } else if (tokenizer.tokenType() == Tokenizer.TokenType.IDENTIFIER) {
                eat("identifier", subroutineElement);
            }
        }
        eat("identifier", subroutineElement);
        eat("symbol", subroutineElement);
        compileParameterList(subroutineElement);
        eat("symbol", subroutineElement);
        Element subroutineBodyElement = doc.createElement("subroutineBody");
        eat("symbol", subroutineBodyElement);
        while ((tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD) && tokenizer.getToken().equals("var")) {
            compileVarDec(subroutineBodyElement);
        }
        compileStatements(subroutineBodyElement);
        eat("symbol", subroutineBodyElement);
        subroutineElement.appendChild(subroutineBodyElement);
        root.appendChild(subroutineElement);
    }

    public void compileParameterList(Element root) throws Exception {
        Element parameterListElement = doc.createElement("parameterList");
        if (tokenizer.getToken().equals("int") || tokenizer.getToken().equals("boolean") || tokenizer.getToken().equals("char") || tokenizer.tokenType() == Tokenizer.TokenType.IDENTIFIER) {
            if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD) {
                eat("keyword", parameterListElement);
                eat("identifier", parameterListElement);
            }
            while (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && tokenizer.getToken().equals(",")) {
                eat("symbol", parameterListElement);
                if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD) {
                    eat("keyword", parameterListElement);
                    eat("identifier", parameterListElement);
                }
            }
        } else {
            parameterListElement.appendChild(doc.createTextNode("\n"));
        }
        root.appendChild(parameterListElement);
    }

    public void compileVarDec(Element root) throws Exception {
        Element varDecElement = doc.createElement("varDec");
        eat("keyword", varDecElement);
        if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD) {
            eat("keyword", varDecElement);
        } else {
            eat("identifier", varDecElement);
        }
        eat("identifier", varDecElement);
        while (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && tokenizer.getToken().equals(",")) {
            eat("symbol", varDecElement);
            eat("identifier", varDecElement);
        }
        eat("symbol", varDecElement);
        root.appendChild(varDecElement);
    }

    public void compileStatements(Element root) throws Exception {
        Element statementsElement = doc.createElement("statements");
        while (true) {
            if (tokenizer.getToken().equals("let")) {
                compileLet(statementsElement);
            } else if (tokenizer.getToken().equals("if")) {
                compileIf(statementsElement);
            } else if (tokenizer.getToken().equals("while")) {
                compileWhile(statementsElement);
            } else if (tokenizer.getToken().equals("do")) {
                compileDo(statementsElement);
            } else if (tokenizer.getToken().equals("return")) {
                compileReturn(statementsElement);
            } else {
                break;
            }
        }
        root.appendChild(statementsElement);
    }

    public void compileDo(Element root) throws Exception {
        Element doElement = doc.createElement("doStatement");
        eat("keyword", doElement);
        eat("identifier", doElement);
        if (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && tokenizer.getToken().equals(".")) {
            eat("symbol", doElement);
            eat("identifier", doElement);
        }
        eat("symbol", doElement);
        compileExpressionList(doElement);
        eat("symbol", doElement);
        eat("symbol", doElement);
        root.appendChild(doElement);
    }

    public void compileLet(Element root) throws Exception {
        Element letElement = doc.createElement("letStatement");
        eat("keyword", letElement);
        eat("identifier", letElement);
        if (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && tokenizer.getToken().equals("[")) {
            eat("symbol", letElement);
            compileExpression(letElement);
            eat("symbol", letElement);
        }
        eat("symbol", letElement);
        compileExpression(letElement);
        eat("symbol", letElement);
        root.appendChild(letElement);
    }

    public void compileWhile(Element root) throws Exception {
        Element whileElement = doc.createElement("whileStatement");
        eat("keyword", whileElement);
        eat("symbol", whileElement);
        compileExpression(whileElement);
        eat("symbol", whileElement);
        eat("symbol", whileElement);
        compileStatements(whileElement);
        eat("symbol", whileElement);
        root.appendChild(whileElement);
    }

    public void compileReturn(Element root) throws Exception {
        Element returnElement = doc.createElement("returnStatement");
        eat("keyword", returnElement);
        if (!tokenizer.getToken().equals(";")) {
            compileExpression(returnElement);
        }
        eat("symbol", returnElement);
        root.appendChild(returnElement);
    }

    public void compileIf(Element root) throws Exception {
        Element ifElement = doc.createElement("ifStatement");
        eat("keyword", ifElement);
        eat("symbol", ifElement);
        compileExpression(ifElement);
        eat("symbol", ifElement);
        eat("symbol", ifElement);
        compileStatements(ifElement);
        eat("symbol", ifElement);
        if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD && tokenizer.getToken().equals("else")) {
            eat("keyword", ifElement);
            eat("symbol", ifElement);
            compileStatements(ifElement);
            eat("symbol", ifElement);
        }
        root.appendChild(ifElement);
    }

    public void compileExpression(Element root) throws Exception {
        Element expressionElement = doc.createElement("expression");
        compileTerm(expressionElement);
        while (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && (tokenizer.getToken().equals("+") || tokenizer.getToken().equals("-") || tokenizer.getToken().equals("*") || tokenizer.getToken().equals("/") || tokenizer.getToken().equals("&") || tokenizer.getToken().equals("|") || tokenizer.getToken().equals(">") || tokenizer.getToken().equals("<") || tokenizer.getToken().equals("="))) {
            eat("symbol", expressionElement);
            compileTerm(expressionElement);
        }
        root.appendChild(expressionElement);
    }

    public void compileTerm(Element root) throws Exception {
        Element termElement = doc.createElement("term");
        if (tokenizer.tokenType() == Tokenizer.TokenType.INT_CONST) {
            eat("integerConstant", termElement);
        } else if (tokenizer.tokenType() == Tokenizer.TokenType.STRING_CONST) {
            eat("stringConstant", termElement);
        } else if (tokenizer.tokenType() == Tokenizer.TokenType.KEYWORD && (tokenizer.getToken().equals("true") || tokenizer.getToken().equals("false") || tokenizer.getToken().equals("null") || tokenizer.getToken().equals("this"))) {
            eat("keyword", termElement);
        } else if (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && tokenizer.getToken().equals("(")) {
            eat("symbol", termElement);
            compileExpression(termElement);
            eat("symbol", termElement);
        } else if (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && (tokenizer.getToken().equals("-") || tokenizer.getToken().equals("~"))) {
            eat("symbol", termElement);
            compileTerm(termElement);
        } else if (tokenizer.tokenType() == Tokenizer.TokenType.IDENTIFIER) {
            eat("identifier", termElement);
            if (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && tokenizer.getToken().equals("[")) {
                eat("symbol", termElement);
                compileExpression(termElement);
                eat("symbol", termElement);
            } else if (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && (tokenizer.getToken().equals(".") || tokenizer.getToken().equals("("))) {
                if (tokenizer.getToken().equals(".")) {
                    eat("symbol", termElement);
                    eat("identifier", termElement);
                }
                eat("symbol", termElement);
                compileExpressionList(termElement);
                eat("symbol", termElement);
            }
        }
        root.appendChild(termElement);
    }

    public void compileExpressionList(Element root) throws Exception {
        Element expressionListElement = doc.createElement("expressionList");
        if (!tokenizer.getToken().equals(")")) {
            compileExpression(expressionListElement);
            while (tokenizer.tokenType() == Tokenizer.TokenType.SYMBOL && tokenizer.getToken().equals(",")) {
                eat("symbol", expressionListElement);
                compileExpression(expressionListElement);
            }
        } else {
            expressionListElement.appendChild(doc.createTextNode("\n"));
        }
        root.appendChild(expressionListElement);
    }

    private void eat(String expectationType, Element rootElement) throws Exception {
        if (tokenizer.tokenType() == null && tokenizer.hasMoreTokens()) {
            tokenizer.advance();
        }
        String tag = Tokenizer.getTag(tokenizer);
        String value = tokenizer.getToken();
        if (!tag.equals(expectationType)) {
            System.out.println("======== the program has a syntax error: " + expectationType + "  " + value);
        } else {
            Element classElement = doc.createElement(tag);
            classElement.appendChild(doc.createTextNode(value));
            rootElement.appendChild(classElement);
            if (tokenizer.hasMoreTokens()) {
                tokenizer.advance();
            }
        }
    }

    public void transform(String output) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(output));
        transformer.transform(source, result);
    }
}
