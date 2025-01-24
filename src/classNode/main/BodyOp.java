package classNode.main;

import classNode.main.statOp;
import classNode.DeclarationNode.varDeclOp;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

import java.util.List;

public class BodyOp {
    private List<statOp> statement;
    private List<varDeclOp> declaration;
    private ScopeTable bodyScope;

    public BodyOp(List<statOp> statement, List<varDeclOp> declaration) {
        this.statement = statement;
        this.declaration = declaration;
    }

    public List<statOp> getStatement() {
        return this.statement;
    }

    public List<varDeclOp> getDeclaration() {
        return this.declaration;
    }

    public ScopeTable getBodyScope() {
        return this.bodyScope;
    }

    public void setBodyScope(ScopeTable bodyScope) {
        this.bodyScope = bodyScope;
    }

    public void accept (visitor visitor) {
        visitor.visit(this);
    }
}
