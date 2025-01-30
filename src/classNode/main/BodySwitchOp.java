package classNode.main;

import semanticAnalyzer.ScopeTable;
import visitors.visitor;

import java.util.List;

public class BodySwitchOp {
    private final List<CaseOp> cases;
    private ScopeTable bodyScope;

    public BodySwitchOp(List<CaseOp> cases) {
        this.cases = cases;
    }

    public List<CaseOp> getCases() {
        return this.cases;
    }

    public ScopeTable getBodyScope() {
        return this.bodyScope;
    }

    public void setBodyScope(ScopeTable bodyScope) {
        this.bodyScope = bodyScope;
    }

    public void accept(visitor visitor) {
        visitor.visit(this);
    }
}
