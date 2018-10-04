public class SyntaxAnalyzer {
    private Tokenizer tokenizer;
    private Parser parser;

    public SyntaxAnalyzer(String fileName) {
        tokenizer = new Tokenizer(fileName);
        parser = new Parser(fileName, fileName);
    }

    public void process() {
        parser.compileClass();
    }

    public static void main(String[] args) {
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(args[0]);
        syntaxAnalyzer.process();
    }
}
