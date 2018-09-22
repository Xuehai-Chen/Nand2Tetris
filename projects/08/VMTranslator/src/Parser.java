import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

class Parser {

    private String line;
    private BufferedReader vmFileReader;


    Parser(String file) throws Exception {
        File vmFile = new File(file);
        vmFileReader = new BufferedReader(new FileReader(vmFile));
    }

    boolean hasMoreCammands() throws Exception {
        return vmFileReader.ready();
    }

    void advance() throws Exception {
        line = vmFileReader.readLine();
        if (line.startsWith("//") || line.isEmpty()) {
            line = vmFileReader.readLine();
        }
    }

    VMTranslator.CommandType commandType() throws Exception {
        String command = line.split(" ")[0];
        VMTranslator.CommandType commandType;
        switch (command) {
            case "push":
                commandType = VMTranslator.CommandType.C_PUSH;
                break;
            case "pop":
                commandType = VMTranslator.CommandType.C_POP;
                break;
            case "label":
                commandType = VMTranslator.CommandType.C_LABEL;
                break;
            case "goto":
                commandType = VMTranslator.CommandType.C_GOTO;
                break;
            case "if-goto":
                commandType = VMTranslator.CommandType.C_IF;
                break;
            case "function":
                commandType = VMTranslator.CommandType.C_FUNCTION;
                break;
            case "return":
                commandType = VMTranslator.CommandType.C_RETURN;
                break;
            case "call":
                commandType = VMTranslator.CommandType.C_CALL;
                break;
            default:
                commandType = VMTranslator.CommandType.C_ARITHMETIC;
        }
        return commandType;
    }

    String arg1() throws Exception {
        String arg1;
        if (commandType() == VMTranslator.CommandType.C_ARITHMETIC) {
            arg1 = line.split(" ")[0];
        } else {
            arg1 = line.split(" ")[1];
        }
        return arg1;
    }

    int arg2() {
        String l = line.split(" ")[2];
        l = l.replaceAll("[^0-9]", "");
        return Integer.parseInt(l);
    }
}
