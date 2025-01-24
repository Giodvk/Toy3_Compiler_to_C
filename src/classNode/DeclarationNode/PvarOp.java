package classNode.DeclarationNode;
import classNode.Expression.identifierExpression;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

public class PvarOp extends DeclOp{
    private final identifierExpression var;
    private String ref=null;
    private ScopeTable scopeTable;

    public PvarOp(identifierExpression var, String ref){
        this.var = var;
        this.ref=ref;
    }

    public identifierExpression getVar(){
        return this.var;
    }

    public String getRef(){
        return this.ref;
    }

    public void setScopeTable(ScopeTable scopeTable){
        this.scopeTable = scopeTable;
    }
    public ScopeTable getScopeTable(){
        return this.scopeTable;
    }

    @Override
    public void accept(visitor visitor){
        visitor.visit(this);
    }
}
