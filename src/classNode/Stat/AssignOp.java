package classNode.Stat;
import classNode.Expression.expressionNode;
import classNode.main.statOp;
import java.util.List;
import classNode.Expression.identifierExpression;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

public class AssignOp extends statOp{
    private List<identifierExpression> variables;
    private List<expressionNode> expression;
    private ScopeTable assignScope;

    public AssignOp(List<identifierExpression> variables, List<expressionNode> expression){
        this.variables = variables;
        this.expression = expression;
    }

    public List<identifierExpression> getVariables() {
        return this.variables;
    }

    public List<expressionNode> getExpression() {
        return this.expression;
    }

    public ScopeTable getAssignScope() {
        return this.assignScope;
    }

    public void setAssignScope(ScopeTable assignScope) {
        this.assignScope = assignScope;
    }


    @Override
    public void accept(visitor visitor) {
        visitor.visit(this);
    }
}
