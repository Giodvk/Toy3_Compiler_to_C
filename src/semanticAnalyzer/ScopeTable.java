package semanticAnalyzer;



import semanticAnalyzer.Symbols.EntryTable;
import semanticAnalyzer.Symbols.Symbol;

import java.util.Hashtable;

public class ScopeTable {
    private String context;
    private Hashtable<Symbol, EntryTable> symbolTable ;
    private ScopeTable parentScope;

    public ScopeTable(String context, ScopeTable parentScope) {
        this.context = context;
        this.symbolTable = new Hashtable<Symbol,EntryTable>();
        this.parentScope = parentScope;
    }

    public Hashtable<Symbol, EntryTable> getSymbolTable() {
        return this.symbolTable;
    }

    public void addSymbol(EntryTable value, Symbol information) throws SemanticError{
            if (this.containsSymbol(information) && this.symbolTable.get(information).getClass() == value.getClass()) {
                throw new SemanticError(0,
                        "Errore semantico : simbolo \"" + information.getId() + "\" già dichiarato",0);
            }
            this.symbolTable.put(information, value);
    }

    public EntryTable lookUpSymbol(Symbol id) {
        if (this.symbolTable.containsKey(id)) {
            return this.symbolTable.get(id);
        } else if (this.parentScope != null) {
            return this.parentScope.lookUpSymbol(id);
        }
        return null;
    }

    public boolean containsSymbol(Symbol id){
        return this.symbolTable.containsKey(id);
    }

    public ScopeTable getParentScope() {
        return this.parentScope;
    }

    public String getContext() {
        return this.context;
    }

    // Metodo per copiare la ScopeTable
    public ScopeTable copy() {
        // Crea una nuova ScopeTable con lo stesso nome e la stessa parentScope
        ScopeTable copy = new ScopeTable(this.context, this.parentScope);

        // Copia tutti i simboli dalla symbolTable originale alla nuova tabella
        copy.symbolTable.putAll(this.symbolTable);

        return copy;
    }

    public void printTable() {
        for(Symbol symbol : this.symbolTable.keySet()){
            System.out.println(symbol + " -> " + this.symbolTable.get(symbol));
        }

    }
}


