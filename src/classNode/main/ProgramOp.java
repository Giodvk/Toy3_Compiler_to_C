package classNode.main;

import classNode.DeclarationNode.DeclOp;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

import java.util.List;

public class ProgramOp {
    private List<DeclOp> defs;
    private BeginEndOp program;
    private ScopeTable globalScope;

    public ProgramOp(List<DeclOp> defs, BeginEndOp program) {
        this.defs = defs;
        this.program = program;
    }

    public List<DeclOp> getDefs() {
        return this.defs;
    }

    public BeginEndOp getProgram() {
        return this.program;
    }

    public ScopeTable getGlobalScope() {
        return this.globalScope;
    }

    public void setGlobalScope(ScopeTable globalScope) {
        this.globalScope = globalScope;
    }

    public void accept (visitor visitor){
        visitor.visit(this);
    }

}
