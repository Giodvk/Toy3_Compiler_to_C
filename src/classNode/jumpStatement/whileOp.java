package classNode.jumpStatement;
import classNode.Expression.expressionNode;
import classNode.main.BodyOp;
import classNode.main.statOp;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

public class whileOp extends statOp {
    private final expressionNode condition;
    private final BodyOp bodyWhile;
    private ScopeTable scopeTable;

    public whileOp(expressionNode condition, BodyOp bodyWhile) {
        this.condition = condition;
        this.bodyWhile = bodyWhile;
    }

    public expressionNode getCondition() {
        return this.condition;
    }

    public BodyOp getBodyWhile() {
        return this.bodyWhile;
    }

    public void setScopeTable(ScopeTable scopeTable) {
        this.scopeTable = scopeTable;
    }

    public ScopeTable getScopeTable() {
        return this.scopeTable;
    }

    @Override
    public void accept(visitor visitor){
        visitor.visit(this);
    }
}
