package classNode.main;

import classNode.DeclarationNode.varDeclOp;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

import java.util.List;

public class BeginEndOp {
    private List<varDeclOp> declOps;
    private List<statOp> statOps;
    private ScopeTable mainScope;

    public BeginEndOp(List<varDeclOp> declOps, List<statOp> statOps){
        this.declOps = declOps;
        this.statOps = statOps;
    }

    public List<varDeclOp> getDeclOps() {
        return this.declOps;
    }

    public List<statOp> getStatOps() {
        return this.statOps;
    }

    public ScopeTable getMainScope() {
        return this.mainScope;
    }

    public void setMainScope(ScopeTable mainScope) {
        this.mainScope = mainScope;
    }

    public void accept (visitor visitor){
        visitor.visit(this);
    }
}
