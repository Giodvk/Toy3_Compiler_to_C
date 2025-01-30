package classNode.Stat;

import classNode.Expression.identifierExpression;
import classNode.main.BodySwitchOp;
import classNode.main.statOp;
import semanticAnalyzer.ScopeTable;
import visitors.visitor;

public class SwitchOp extends statOp {
    private final identifierExpression variable;
    private final BodySwitchOp bodySwitchOp;
    private ScopeTable switchTable;

    public SwitchOp(identifierExpression variable, BodySwitchOp bodySwitchOp) {
        this.variable = variable;
        this.bodySwitchOp = bodySwitchOp;
    }

    public identifierExpression getVariable() {
        return this.variable;
    }

    public BodySwitchOp getBodySwitchOp() {
        return this.bodySwitchOp;
    }

    public ScopeTable getSwitchTable() {
        return this.switchTable;
    }

    public void setSwitchTable(ScopeTable switchTable) {
        this.switchTable = switchTable;
    }

    public void accept(visitor visitor) {
        visitor.visit(this);
    }
}
