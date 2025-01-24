package classNode.DeclarationNode;

import classNode.Expression.identifierExpression;
import classNode.main.BodyOp;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

import java.util.List;


public class DefDeclOp extends DeclOp {
    private identifierExpression funName;
    private List<ParDeclOp> params;
    private String type;
    private BodyOp bodyFun;
    private ScopeTable scopeFun;

    public DefDeclOp(identifierExpression funName, List<ParDeclOp> params, String type, BodyOp bodyFun){
        this.funName = funName;
        this.params = params;
        this.type = type;
        this.bodyFun = bodyFun;
    }

    public identifierExpression getFunName() {
        return this.funName;
    }

    public List<ParDeclOp> getParams() {
        return this.params;
    }

    public String getType() {
        return this.type;
    }

    public BodyOp getBodyFun() {
        return this.bodyFun;
    }

    public ScopeTable getScopeFun() {
        return this.scopeFun;
    }
    public void setScopeFun(ScopeTable scopeFun) {
        this.scopeFun = scopeFun;
    }

    @Override
    public void accept(visitor visitor){
        visitor.visit(this);
    }
}
