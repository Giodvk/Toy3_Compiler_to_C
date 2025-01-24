package classNode.DeclarationNode;

import semanticAnalyzer.ScopeTable;
import visitors.visitor;

import java.util.List;

public class ParDeclOp extends DeclOp{
    private List<PvarOp> params;
    private String type;
    private ScopeTable scopeTable;

    public ParDeclOp(List<PvarOp> params, String type){
        this.params = params;
        this.type = type;
    }

    public List<PvarOp> getParams(){
        return this.params;
    }

    public String getType(){
        return this.type;
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
