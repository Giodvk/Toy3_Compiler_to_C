package classNode.Expression;

import semanticAnalyzer.ScopeTable;
import visitors.visitor;

public class binaryExpression extends expressionNode{
    private final String operator;//plus, minus, or, and etc...
    private final expressionNode leftOperand;
    private final expressionNode rightOperand;
    private ScopeTable scopeExpression;

    public binaryExpression(String operator, expressionNode leftOperand, expressionNode rightOperand
    , int line, int column) {
        this.operator = operator;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.line = line;
        this.column = column;
    }

    public String getOperator() {
        return this.operator;
    }

    public expressionNode getLeftOperand() {
        return this.leftOperand;
    }

    public expressionNode getRightOperand(){
        return this.rightOperand;
    }

    public void setScopeExpression(ScopeTable scopeExpression) {
        this.scopeExpression = scopeExpression;
    }

    public ScopeTable getScopeExpression() {
        return this.scopeExpression;
    }

    public String getType() {return this.type;}

    public void setType(String type) {this.type = type;}

    public int getLine() {return this.line;}

    public int getColumn() {return this.column;}

    @Override
    public void accept(visitor visitor) {
        visitor.visit(this);
    }
}
