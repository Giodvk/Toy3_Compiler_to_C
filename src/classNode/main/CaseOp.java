package classNode.main;

import classNode.Expression.constantExpression;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

import java.util.List;

public class CaseOp {
    private constantExpression constant;
    private final List<statOp> statements;
    private ScopeTable caseScope;

    public CaseOp(constantExpression constant, List<statOp> statements) {
        this.constant = constant;
        this.statements = statements;
    }

    public constantExpression getConstant() {
        return this.constant;
    }

    public List<statOp> getStatements() {
        return this.statements;
    }

    public ScopeTable getCaseScope() {
        return this.caseScope;
    }

    public void setCaseScope(ScopeTable caseScope) {
        this.caseScope = caseScope;
    }

    public void accept(visitor visitor) {
        visitor.visit(this);
    }
}
