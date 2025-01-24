package semanticAnalyzer.Symbols;

public class VarEntry extends EntryTable{
    private final String type;
    private final boolean ref;

    public VarEntry(String type, String ref) {
        this.type = type;
        this.ref = ref != null;
    }

    public String getType() {
        return type;
    }

    public boolean isRef() {
        return ref;
    }
}
