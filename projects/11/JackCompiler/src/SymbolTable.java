import java.util.HashMap;

public class SymbolTable {

    private HashMap<String, Object[]> classSymbolTable;
    private HashMap<String, Object[]> subroutineSymbolTable;
    private int staticCount = 0;
    private int fieldCount = 0;
    private int argCount = 0;
    private int varCount = 0;

    public enum IdentifierKind {
        STATIC, FIELD, ARG, VAR, UNDEFINED
    }

    public SymbolTable() {
        classSymbolTable = new HashMap<>();
        subroutineSymbolTable = new HashMap<>();
    }

    public void startSubroutine() {
        subroutineSymbolTable = new HashMap<>();
        argCount = 0;
        varCount = 0;
    }

    public void define(String name, String type, IdentifierKind kind) {
        if (kind == IdentifierKind.STATIC) {
            Object[] entity = {type, kind, staticCount};
            classSymbolTable.put(name, entity);
            staticCount++;
        } else if (kind == IdentifierKind.FIELD) {
            Object[] entity = {type, kind, fieldCount};
            classSymbolTable.put(name, entity);
            fieldCount++;
        } else if (kind == IdentifierKind.ARG) {
            Object[] entity = {type, kind, argCount};
            subroutineSymbolTable.put(name, entity);
            argCount++;
        } else if (kind == IdentifierKind.VAR) {
            Object[] entity = {type, kind, varCount};
            subroutineSymbolTable.put(name, entity);
            varCount++;
        } else {
            System.out.println("The identifier kind is undefined.");
        }
    }

    public int varCount(IdentifierKind kind) {
        switch (kind) {
            case STATIC:
                return staticCount;
            case FIELD:
                return fieldCount;
            case ARG:
                return argCount;
            case VAR:
                return varCount;
            default:
                return 0;
        }
    }

    public IdentifierKind kindOf(String name) {
        if (subroutineSymbolTable.containsKey(name)) {
            return (IdentifierKind) subroutineSymbolTable.get(name)[1];
        } else if (classSymbolTable.containsKey(name)) {
            return (IdentifierKind) classSymbolTable.get(name)[1];
        }
        System.out.println("The identifier is undefined.");
        return IdentifierKind.UNDEFINED;
    }

    public String typeOf(String name) {
        if (subroutineSymbolTable.containsKey(name)) {
            return (String) subroutineSymbolTable.get(name)[0];
        } else if (classSymbolTable.containsKey(name)) {
            return (String) classSymbolTable.get(name)[0];
        }
        System.out.println("The identifier is undefined.");
        return "UNDEFINED";
    }

    public int indexOf(String name) {
        if (subroutineSymbolTable.containsKey(name)) {
            return (int) subroutineSymbolTable.get(name)[2];
        } else if (classSymbolTable.containsKey(name)) {
            return (int) classSymbolTable.get(name)[2];
        }
        System.out.println("The identifier is undefined.");
        return 0;
    }
}
