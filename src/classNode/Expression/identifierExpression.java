package classNode.Expression;

import semanticAnalyzer.ScopeTable;
import visitors.visitor;

public class identifierExpression extends expressionNode{
    private final String token = "identifier";
    private String name;
    private ScopeTable scopeTable;

    public identifierExpression(String name, int line, int column) {
        this.name = name;
        this.line = line;
        this.column = column;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getToken(){
        return this.token;
    }

    public ScopeTable getScopeTable(){
        return this.scopeTable;
    }

    public void setScopeTable(ScopeTable scopeTable){
        this.scopeTable = scopeTable;
    }

    public String getType(){ return this.type;}

    public void setType(String type){ this.type = type;}

    public int getLine(){return this.line;}

    public int getColumn(){return this.column;}

    @Override
    public void accept(visitor visitor){
        visitor.visit(this);
    }
}
