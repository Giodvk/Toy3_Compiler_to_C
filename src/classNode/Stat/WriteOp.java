package classNode.Stat;
import classNode.Expression.expressionNode;
import classNode.main.statOp;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

import java.util.List;

public class WriteOp extends statOp{
    private List<expressionNode> variables;
    private final String newLine;
    private ScopeTable writeScope;

    public WriteOp(List<expressionNode> variables, String newLine){
        this.variables = variables;
        this.newLine = newLine;
    }

    public List<expressionNode> getVariables() {
        return this.variables;
    }

    public String getNewLine() {
        return this.newLine;
    }

    public ScopeTable getWriteScope() {
        return this.writeScope;
    }

    public void setWriteScope(ScopeTable writeScope) {
        this.writeScope = writeScope;
    }

    @Override
    public void accept(visitor visitor) {
        visitor.visit(this);
    }

}
