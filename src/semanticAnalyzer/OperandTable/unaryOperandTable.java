package semanticAnalyzer.OperandTable;
import java.util.Hashtable;

public class unaryOperandTable {
    private final Hashtable<Triple<String,String,String>, String> unaryOperandTable;

    public unaryOperandTable() {
        this.unaryOperandTable = new Hashtable<>();
        Triple<String,String,String> not = new Triple<>("not","bool",null);
        Triple<String,String,String> minus = new Triple<>("-","integer",null);
        Triple<String,String,String> minus2 = new Triple<>("-","double",null);

        this.unaryOperandTable.put(not,"bool");
        this.unaryOperandTable.put(minus,"integer");
        this.unaryOperandTable.put(minus2,"double");
    }

    public Hashtable<Triple<String,String,String>, String> getUnaryOperandTable() {
        return this.unaryOperandTable;
    }
}
