
public class SymbolTable {

    public enum IdentifierKind {
        STATIC, FIELD, ARG, VAR
    }

    public SymbolTable() {

    }

    public void startSubroutine() {

    }

    public void define(String name, String type, IdentifierKind kind) {

    }

    public int varCount(IdentifierKind kind) {
        return 0;
    }

    public IdentifierKind kindOf(String name) {
        return IdentifierKind.ARG;
    }

    public String typeOf(String name) {
        return "int";
    }

    public int indexOf(String name) {
        return 0;
    }
}
