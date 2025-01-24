package classNode.jumpStatement;

import classNode.Expression.expressionNode;
import classNode.main.BodyOp;
import classNode.main.statOp;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;


public class ifThenOp extends statOp {
    private final expressionNode condition;
    private final BodyOp bodyIf;
    private ScopeTable ifScope;

    public ifThenOp(expressionNode cond, BodyOp body){
        this.condition = cond;
        this.bodyIf = body;
    }

    public expressionNode getCondition(){
        return this.condition;
    }

    public BodyOp getBodyIf(){
        return this.bodyIf;
    }

    public ScopeTable getIfScope(){
        return this.ifScope;
    }

    public void setIfScope(ScopeTable ifScope){
        this.ifScope = ifScope;
    }

    @Override
    public void accept (visitor visitor){
        visitor.visit(this);
    }
}
