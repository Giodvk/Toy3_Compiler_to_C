package classNode.Stat;
import classNode.Expression.identifierExpression;
import classNode.main.statOp;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

import java.util.List;

public class ReadOp extends statOp{
    private List<identifierExpression> variables;
    private ScopeTable readScope;

    public ReadOp(List<identifierExpression> variables){
        this.variables = variables;
    }

    public List<identifierExpression> getVariables() {
        return this.variables;
    }

    public ScopeTable getReadScope() {
        return this.readScope;
    }

    public void setReadScope(ScopeTable readScope) {
        this.readScope = readScope;
    }

    @Override
    public void accept (visitor visitor){
        visitor.visit(this);
    }
}
