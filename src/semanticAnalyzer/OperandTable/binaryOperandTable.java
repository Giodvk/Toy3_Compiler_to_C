package semanticAnalyzer.OperandTable;

import java.util.Hashtable;

public class binaryOperandTable
{
    private Hashtable<Triple<String,String,String>, String> table;

    public binaryOperandTable(){
        this.table = new Hashtable<>();
        String[] operators = new String[4];
        operators[0] = "+";
        operators[1] = "-";
        operators[2] = "*";
        operators[3] = "/";
        for(int i=0;i<4;i++) {
            Triple<String,String,String> key1 = new Triple<>(operators[i],"integer", "integer");
            Triple<String,String,String> key2 = new Triple<>(operators[i],"integer", "double");
            Triple<String,String,String> key3 = new Triple<>(operators[i],"double", "integer");
            Triple<String,String,String> key4 = new Triple<>(operators[i],"double", "double");
            this.table.put(key1, "integer");
            this.table.put(key2, "double");
            this.table.put(key3, "double");
            this.table.put(key4, "double");
        }
        this.table.put(new Triple<>("+","string","string"),"string");
        this.table.put(new Triple<>("+","string","integer"),"string");
        this.table.put(new Triple<>("+","integer","string"),"string");
        this.table.put(new Triple<>("+","double","string"),"string");
        this.table.put(new Triple<>("+","string","double"),"string");
        operators = new String[6];
        operators[0] = "<";
        operators[1] = ">";
        operators[2] = "<=";
        operators[3] = ">=";
        operators[4] = "==";
        operators[5] = "<>";

        for(int i=0;i<6;i++) {
            Triple<String,String,String> key1 = new Triple<>(operators[i],"integer", "integer");
            Triple<String,String,String> key2 = new Triple<>(operators[i],"integer", "double");
            Triple<String,String,String> key3 = new Triple<>(operators[i],"double", "integer");
            Triple<String,String,String> key4 = new Triple<>(operators[i],"double", "double");
            Triple<String,String,String> key5 = new Triple<>(operators[i],"string", "string");
            this.table.put(key1, "bool");
            this.table.put(key2, "bool");
            this.table.put(key3, "bool");
            this.table.put(key4, "bool");
            this.table.put(key5, "bool");
        }
        this.table.put(new Triple<>("and","bool","bool"),"bool");
        this.table.put(new Triple<>("or","bool","bool"),"bool");
    }

    public Hashtable<Triple<String,String,String>, String> getTable() {
        return this.table;
    }
}
