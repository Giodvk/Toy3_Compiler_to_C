package classNode.Expression;

import semanticAnalyzer.ScopeTable;
import visitors.visitor;

import java.util.List;

public class CallOp extends expressionNode {
    private final identifierExpression idFun;
    private List<expressionNode> args;
    private ScopeTable funScope;

    public CallOp(identifierExpression idFun, List<expressionNode> args, int line, int column) {
        this.idFun = idFun;
        this.args = args;
        this.line = line;
        this.column = column;
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

    public String getType() {return this.type;}

    public void setType(String type) {this.type = type;}

    public int getLine() {
        return this.line;
    }
    public int getColumn() {return this.column;}

    @Override
    public void accept(visitor visitor){
        visitor.visit(this);
    }
}
