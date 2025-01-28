package classNode.Stat;

import classNode.Expression.expressionNode;
import classNode.Expression.identifierExpression;
import classNode.main.statOp;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

import java.util.List;

public class FunCallStat extends statOp {
    private final identifierExpression idFun;
    private final List<expressionNode> args;
    private ScopeTable funScope;
    private String type;

    public FunCallStat(identifierExpression idFun, List<expressionNode> args) {
        this.idFun = idFun;
        this.args = args;
    }

    public identifierExpression getIdFun() {
        return this.idFun;
    }

    public List<expressionNode> getArgs() {
        return this.args;
    }

    public ScopeTable getFunScope() {
        return this.funScope;
    }

    public void setFunScope(ScopeTable funScope) {
        this.funScope = funScope;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void accept (visitor visitor) {
        visitor.visit(this);
    }
}
