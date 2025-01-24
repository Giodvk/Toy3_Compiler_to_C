package classNode.Stat;

import classNode.Expression.expressionNode;
import classNode.main.statOp;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

public class returnOp extends statOp {
    private expressionNode expr;
    private ScopeTable returnScope;
    private String type;

    public returnOp(expressionNode expr) {
        this.expr = expr;
    }

    public expressionNode getExpr() {
        return this.expr;
    }

    public void setReturnScope(ScopeTable returnScope) {
        this.returnScope = returnScope;
    }

    public ScopeTable getReturnScope() {
        return this.returnScope;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return this.type;
    }

    @Override
    public void accept(visitor visitor) {
        visitor.visit(this);
    }
}
