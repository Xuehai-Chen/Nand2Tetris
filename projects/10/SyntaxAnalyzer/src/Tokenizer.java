import org.w3c.dom.*;

import javax.xml.parsers.*;
import java.io.*;

public class Tokenizer {

    private File file;
    private String currentToken;

    public enum TokenType {
        KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST
    }

    public enum KeyWord {
        CLASS, METHOD, FUNCTION, CONSTRUCTOR, INT, BOOLEAN, CHAR, VOID, VAR, STATIC,
        FIELD, LET, DO, IF, ELSE, WHILE, RETURN, TRUE, FALSE, NULL, THIS
    }

    public Tokenizer(String filename) {
        file = new File(filename);
    }

    public Boolean hasMoreTokens() {
        return true;
    }

    public void advance() {

    }

    public TokenType tokenType() {
        return TokenType.KEYWORD;
    }

    public KeyWord keyWord() {
        return KeyWord.METHOD;
    }

    public char symbol() {
        return '+';
    }

    public String identifier() {
        return "var";
    }

    public int intVal() {
        return 1;
    }

    public String stringVal() {
        return "string";
    }
}
