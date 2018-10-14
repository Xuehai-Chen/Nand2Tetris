import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashSet;

public class Tokenizer {

    private BufferedReader bufferedReader;
    private String currentLine;
    private int lineIndex = 0;
    private String currentToken;
    private TokenType currentType;

    public enum TokenType {
        KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST
    }

    private HashSet<String> keyWordSet;
    private HashSet<Character> symbolSet;

    public Tokenizer(String file) throws Exception {
        bufferedReader = new BufferedReader(new FileReader(file));
        keyWordSet = new HashSet<>();
        keyWordSet.add("class");
        keyWordSet.add("method");
        keyWordSet.add("function");
        keyWordSet.add("constructor");
        keyWordSet.add("int");
        keyWordSet.add("boolean");
        keyWordSet.add("char");
        keyWordSet.add("void");
        keyWordSet.add("var");
        keyWordSet.add("static");
        keyWordSet.add("field");
        keyWordSet.add("let");
        keyWordSet.add("do");
        keyWordSet.add("if");
        keyWordSet.add("else");
        keyWordSet.add("while");
        keyWordSet.add("return");
        keyWordSet.add("true");
        keyWordSet.add("false");
        keyWordSet.add("null");
        keyWordSet.add("this");
        symbolSet = new HashSet<>();
        symbolSet.add('{');
        symbolSet.add('}');
        symbolSet.add('(');
        symbolSet.add(')');
        symbolSet.add('[');
        symbolSet.add(']');
        symbolSet.add('.');
        symbolSet.add(',');
        symbolSet.add(';');
        symbolSet.add('+');
        symbolSet.add('-');
        symbolSet.add('*');
        symbolSet.add('/');
        symbolSet.add('&');
        symbolSet.add('|');
        symbolSet.add('<');
        symbolSet.add('>');
        symbolSet.add('=');
        symbolSet.add('~');
    }

    public Boolean hasMoreTokens() throws Exception {
        if (getLineReady()) {
            Character currentChar = currentLine.charAt(lineIndex);
            while (currentChar.equals(' ') || currentChar.equals('\t')) {
                lineIndex++;
                if (lineIndex <= currentLine.length() - 1) {
                    currentChar = currentLine.charAt(lineIndex);
                } else {
                    if (!getLineReady()) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void advance() throws Exception {
        StringBuilder s = new StringBuilder();
        Character currentChar = currentLine.charAt(lineIndex);
        if (symbolSet.contains(currentChar)) {
            s.append(currentChar);
            currentToken = s.toString();
            currentType = TokenType.SYMBOL;
            return;
        }

        if (currentChar.equals('"')) {
            currentType = TokenType.STRING_CONST;
            StringBuilder x = new StringBuilder();
            while (true) {
                lineIndex++;
                Character temp = currentLine.charAt(lineIndex);
                if (temp.equals('"')) {
                    currentToken = x.toString();
                    return;
                }
                x.append(temp);
            }
        }

        if (currentChar.toString().matches("[0-9]")) {
            while (currentChar.toString().matches("[0-9]")) {
                s.append(currentChar);
                lineIndex++;
                currentChar = currentLine.charAt(lineIndex);
            }
            lineIndex--;
            currentToken = s.toString();
            currentType = TokenType.INT_CONST;
            return;
        }

        while (true) {
            s.append(currentChar);
            if (keyWordSet.contains(s.toString())) {
                Character c = currentLine.charAt(lineIndex + 1);
                if (symbolSet.contains(c) || c.equals(' ')) {
                    currentToken = s.toString();
                    currentType = TokenType.KEYWORD;
                    return;
                }
            }
            if (symbolSet.contains(currentChar) || currentChar.equals(' ')) {
                lineIndex--;
                s.deleteCharAt(s.lastIndexOf(Character.toString(currentChar)));
                currentToken = s.toString();
                currentType = TokenType.IDENTIFIER;
                return;
            }
            lineIndex++;
            currentChar = currentLine.charAt(lineIndex);
        }
    }

    private boolean getLineReady() throws Exception {
        if (currentLine == null || lineIndex >= currentLine.length() - 1) {
            lineIndex = 0;
            currentLine = bufferedReader.readLine();
            if (currentLine == null) {
                return false;
            }
            while (currentLine.isEmpty() || currentLine.startsWith("//") || currentLine.startsWith("/*") || currentLine.matches("\\p{Blank}*") || currentLine.matches(".*/\\*\\*.*") || currentLine.matches("\\p{Blank}*\\*.*")) {
                if (!bufferedReader.ready()) {
                    return false;
                }
                currentLine = bufferedReader.readLine();
            }
            currentLine = currentLine.split("//")[0];
            System.out.println(currentLine);
        } else {
            lineIndex++;
        }
        return true;
    }

    public TokenType tokenType() {
        return currentType;
    }

    public String getToken() {
        return currentToken;
    }

    public static String getTag(Tokenizer tokenizer) {
        TokenType tokenType = tokenizer.tokenType();
        String tag;
        switch (tokenType) {
            case KEYWORD:
                tag = "keyword";
                break;
            case SYMBOL:
                tag = "symbol";

                break;
            case IDENTIFIER:
                tag = "identifier";
                break;
            case STRING_CONST:
                tag = "stringConstant";
                break;
            default:
                tag = "integerConstant";
        }
        return tag;
    }

    public static void main(String[] args) throws Exception {
        Tokenizer tokenizer = new Tokenizer(args[0]);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        Element rootElement = doc.createElement("tokens");
        doc.appendChild(rootElement);
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            String tag = Tokenizer.getTag(tokenizer);
            String value = tokenizer.getToken();
            Element element = doc.createElement(tag);
            element.appendChild(doc.createTextNode(value));
            rootElement.appendChild(element);
            System.out.println(tokenizer.tokenType());
            i++;
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(args[0].substring(args[0].lastIndexOf('/') + 1).split(".jack")[0] + "T.xml"));
        transformer.transform(source, result);
        System.out.println(i);
    }
}
