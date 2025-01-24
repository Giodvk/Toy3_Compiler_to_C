package classNode.DeclarationNode;

import semanticAnalyzer.ScopeTable;
import visitors.visitor;

import java.util.List;

public class varDeclOp extends DeclOp{
    private List<varOptInitOp> varOptInits;
    private final Object type;
    private ScopeTable declScope;

    public varDeclOp(List<varOptInitOp> varOptInits, Object type) {
        this.varOptInits = varOptInits;
        this.type = type;
    }

    public List<varOptInitOp> getVarOptInits() {
        return this.varOptInits;
    }

    public Object getType() {
        return this.type;
    }

    public ScopeTable getDeclScope() {
        return this.declScope;
    }

    public void setDeclScope(ScopeTable declScope) {
        this.declScope = declScope;
    }

    @Override
    public void accept(visitor visitor){
        visitor.visit(this);
    }
}
