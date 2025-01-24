package classNode.Expression;

import semanticAnalyzer.ScopeTable;
import visitors.visitor;

public class constantExpression extends expressionNode{
    private Object constant;
    private String typeConstant;
    private ScopeTable scopeTable;

    public constantExpression(Object constant, String typeConstant, int line, int column) {
        this.constant = constant;
        this.typeConstant = typeConstant;
        this.line = line;
        this.column = column;
    }

    public Object getConstant(){
        return this.constant;
    }

    public void setConstant(Object constant) {
        this.constant = constant;
    }

    public String getTypeConstant() {
        return this.typeConstant;
    }

    public String toString(){
        return "Costante di tipo : "+this.constant.getClass()+" , valore: "+this.constant;
    }

    public ScopeTable getScopeTable() {
        return scopeTable;
    }

    public void setScopeTable(ScopeTable scopeTable) {
        this.scopeTable = scopeTable;
    }

    public String getType() {return this.type;}

    public void setType(String type) {this.type = type;}

    public int getLine() {return this.line;}

    public int getColumn() {return this.column;}

    @Override
    public void accept(visitor visitor){
        visitor.visit(this);
    }

}