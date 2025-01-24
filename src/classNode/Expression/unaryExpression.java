package classNode.Expression;

import semanticAnalyzer.ScopeTable;
import visitors.visitor;

public class unaryExpression extends expressionNode{
    private final String operator;
    private final expressionNode operand;
    private ScopeTable scopeTable;

    public unaryExpression(String operator, expressionNode operand, int line, int column) {
        this.operator = operator;
        this.operand = operand;
        this.line = line;
        this.column = column;
    }

    public String getOperator(){
        return this.operator;
    }

    public expressionNode getOperand(){
        return this.operand;
    }

    public void setScopeTable(ScopeTable scopeTable){
        this.scopeTable = scopeTable;
    }

    public ScopeTable getScopeTable(){
        return this.scopeTable;
    }

    public void setType(String type){ this.type = type; }

    public String getType(){ return this.type; }

    public int getLine(){ return this.line; }

    public int getColumn(){ return this.column; }

    @Override
    public void accept(visitor visitor){
        visitor.visit(this);
    }
}
