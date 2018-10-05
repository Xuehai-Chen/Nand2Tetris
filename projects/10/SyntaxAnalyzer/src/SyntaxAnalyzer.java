public class SyntaxAnalyzer {
    private String fileName;
    private Tokenizer tokenizer;
    private CompilationEngine compilationEngine;

    public SyntaxAnalyzer(String fileName) throws Exception {
        this.fileName = fileName;
        tokenizer = new Tokenizer(fileName);
        compilationEngine = new CompilationEngine(tokenizer,fileName);
    }

    public void process() throws Exception {
        compilationEngine.compileClass();
        compilationEngine.transform(fileName.substring(fileName.lastIndexOf('/') + 1).split(".jack")[0] + ".xml");
    }

    public static void main(String[] args) throws Exception {
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(args[0]);
        syntaxAnalyzer.process();
    }
}
