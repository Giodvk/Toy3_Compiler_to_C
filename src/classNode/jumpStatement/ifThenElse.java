package classNode.jumpStatement;
import classNode.Expression.expressionNode;
import classNode.main.BodyOp;
import classNode.main.statOp;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

public class ifThenElse extends statOp{
    private final expressionNode condition;
    private final BodyOp bodyIf;
    private final BodyOp bodyElse;
    private ScopeTable ifScope;
    private ScopeTable elseScope;

    public ifThenElse(expressionNode condition, BodyOp bodyIf, BodyOp bodyElse){
        this.condition = condition;
        this.bodyIf = bodyIf;
        this.bodyElse = bodyElse;
    }

    public expressionNode getCondition() {
        return this.condition;
    }

    public BodyOp getBodyIf() {
        return this.bodyIf;
    }

    public BodyOp getBodyElse() {
        return this.bodyElse;
    }

    public ScopeTable getIfScope() {
        return this.ifScope;
    }
    public void setIfScope(ScopeTable ifScope) {
        this.ifScope = ifScope;
    }

    public ScopeTable getElseScope() {
        return this.elseScope;
    }

    public void setElseScope(ScopeTable elseScope) {
        this.elseScope = elseScope;
    }

    @Override
    public void accept (visitor visitor) {
        visitor.visit(this);
    }
}
