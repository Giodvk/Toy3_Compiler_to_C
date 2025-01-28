package classNode.DeclarationNode;

import classNode.Expression.expressionNode;
import classNode.Expression.identifierExpression;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

public class varOptInitOp extends DeclOp{
    private final identifierExpression identifier;
    private expressionNode expression;
    private ScopeTable scopeTable;

    public varOptInitOp(identifierExpression identifier, expressionNode expression){
        this.identifier = identifier;
        this.expression = expression;
    }

    public identifierExpression getIdentifier(){
        return this.identifier;
    }

    public expressionNode getExpression(){
        return this.expression;
    }

    public void setExpression(expressionNode expression){
        this.expression = expression;
    }

    public ScopeTable getScopeTable(){
        return this.scopeTable;
    }

    public void setScopeTable(ScopeTable scopeTable){
        this.scopeTable = scopeTable;
    }

    @Override
    public void accept(visitor visitor){
        visitor.visit(this);
    }
}
