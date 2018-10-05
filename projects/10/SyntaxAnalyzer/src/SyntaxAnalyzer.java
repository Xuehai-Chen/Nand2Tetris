import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SyntaxAnalyzer {
    private String path;
    private ArrayList<File> files = new ArrayList<>();

    public SyntaxAnalyzer(String path) throws Exception {
        File file = new File(path);
        if (file.isDirectory()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    Pattern p = Pattern.compile(".*jack$");
                    Matcher m = p.matcher(name);
                    return m.matches();
                }
            };
            files.addAll(Arrays.asList(file.listFiles(filter)));
            this.path = path + "/";
        } else {
            files.add(file);
            this.path = path.substring(0, path.lastIndexOf("/") + 1);
        }
    }

    public void process() throws Exception {
        for (File singleFile : files) {
            String fileName = singleFile.getName();
            Tokenizer tokenizer = new Tokenizer(path + fileName);
            CompilationEngine compilationEngine = new CompilationEngine(tokenizer);
            compilationEngine.compileClass();
            compilationEngine.transform(fileName.split(".jack")[0] + ".xml");
        }
    }

    public static void main(String[] args) throws Exception {
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(args[0]);
        syntaxAnalyzer.process();
    }
}
