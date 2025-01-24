package semanticAnalyzer;

import semanticAnalyzer.Symbols.EntryTable;
import semanticAnalyzer.Symbols.Symbol;

import java.util.Stack;

public class ScopeStack {
    private Stack<ScopeTable> stack;
    private int size;

    public ScopeStack() {
        this.stack = new Stack<>();
    }

    public void push(ScopeTable scope) {
        this.stack.push(scope);
    }

    public void pop(){
        this.stack.pop();
    }

    public ScopeTable top(){
        return this.stack.peek();
    }

    public ScopeTable top(int index){
        return this.stack.elementAt(index);
    }

    public EntryTable lookUp(Symbol identifier){
        for (int i = this.stack.size() - 1; i >= 0; i--) {
            ScopeTable scopeTable = this.stack.get(i);
            if(scopeTable.containsSymbol(identifier)){
                return scopeTable.lookUpSymbol(identifier);
            }
        }
        return null; //Non trovato nello scoping
    }

    public int size(){
        return this.stack.size();
    }
}
