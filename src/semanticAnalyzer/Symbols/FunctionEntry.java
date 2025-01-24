package semanticAnalyzer.Symbols;

import java.util.ArrayList;

public class FunctionEntry extends EntryTable{
    private ArrayList <VarEntry> vars;
    private final String returnType;

    public FunctionEntry(ArrayList <VarEntry> vars, String returnType) {
        this.vars = vars;
        this.returnType = returnType;
    }

    public ArrayList <VarEntry> getVars(){
        return this.vars;
    }

    public String getReturnType(){
        return this.returnType;
    }
}
