package visitors;

import classNode.DeclarationNode.*;
import classNode.Expression.*;
import classNode.Stat.*;
import classNode.jumpStatement.ifThenElse;
import classNode.jumpStatement.ifThenOp;
import classNode.jumpStatement.whileOp;
import classNode.main.*;

public class PrintTreeVisitor implements visitor{
    private int indent = 0;

    private void printIndent() {
        for (int i = 0; i < indent; i++) {
            System.out.print("  "); // Ogni livello di indentazione aggiunge due spazi
        }
    }

    @Override
    public void visit(ProgramOp programOp) {
        printIndent();
        System.out.println("ProgramOP");
        indent += 1;
        for (DeclOp decl : programOp.getDefs()){
            decl.accept(this);
        }
        programOp.getProgram().accept(this);
        indent -= 1;
    }

    @Override
    public void visit (BeginEndOp beginEndOp) {
        printIndent();
        System.out.println("BeginEndOp");
        indent += 1;
        for (varDeclOp decl : beginEndOp.getDeclOps()){
            decl.accept(this);
        }
        for(statOp stat : beginEndOp.getStatOps()){
            stat.accept(this);
        }
        indent -= 1;
    }

    @Override
    public void visit (BodyOp bodyOp) {
        printIndent();
        System.out.println("BodyOp");
        indent += 1;
        for(varDeclOp decl : bodyOp.getDeclaration()){
            decl.accept(this);
        }

        for(statOp statement : bodyOp.getStatement()){
            statement.accept(this);
        }
        indent -= 1;
    }

    @Override
    public void visit (DefDeclOp function){
        printIndent();
        System.out.println("DefDeclOp");
        indent += 1;
        function.getFunName().accept(this);
        for (ParDeclOp param : function.getParams()){
            param.accept(this);
        }
        printIndent();
        System.out.println(function.getType());
        function.getBodyFun().accept(this);
        indent -= 1;
    }

    @Override
    public void visit (ParDeclOp parameters){
        printIndent();
        System.out.println("ParDeclOp");
        indent += 1;
        for(PvarOp param : parameters.getParams()){
            param.accept(this);
        }
        printIndent();
        System.out.println(parameters.getType());
        indent -= 1;
    }

    @Override
    public void visit (PvarOp parameter){
        printIndent();
        System.out.println("PvarOp");
        indent += 1;
        parameter.getVar().accept(this);
        printIndent();
        System.out.println(parameter.getRef());
        indent -= 1;
    }

    @Override
    public void visit(varDeclOp variable){
        printIndent();
        System.out.println("VarDeclOp");
        indent += 1;
        for (varOptInitOp var : variable.getVarOptInits()){
            var.accept(this);
        }
        printIndent();
        System.out.println(variable.getType());
        indent -= 1;
    }

    @Override
    public void visit (varOptInitOp variable){
        printIndent();
        System.out.println("VarOptInitOp");
        indent += 1;
        variable.getIdentifier().accept(this);
        if(variable.getExpression() != null) {
            variable.getExpression().accept(this);
        }
        indent -= 1;
    }

    @Override
    public void visit (AssignOp assignOp){
        printIndent();
        System.out.println("AssignOp");
        indent += 1;
        for (identifierExpression id : assignOp.getVariables()){
            id.accept(this);
        }
        for(expressionNode expr: assignOp.getExpression()){
            expr.accept(this);
        }
        indent -= 1;
    }

    @Override
    public void visit (FunCallStat funCallStat){
        printIndent();
        System.out.println("FunCallStat");
        indent += 1;
        funCallStat.getIdFun().accept(this);
        for (expressionNode expr : funCallStat.getArgs()){
            expr.accept(this);
        }
        indent -= 1;
    }

    @Override
    public void visit (ReadOp readOp){
        printIndent();
        System.out.println("ReadOp");
        indent += 1;
        for (identifierExpression id : readOp.getVariables()){
            id.accept(this);
        }
        indent -= 1;
    }

    @Override
    public void visit (WriteOp writeOp){
        printIndent();
        System.out.println("WriteOp");
        indent += 1;
        for (expressionNode expr : writeOp.getVariables()) {
            expr.accept(this);
        }
        indent -= 1;
    }

    @Override
    public void visit (returnOp returnStat){
        printIndent();
        System.out.println("returnOp");
        indent+=1;
        returnStat.getExpr().accept(this);
        indent -= 1;
    }

    @Override
    public void visit (ifThenOp ifThen){
        printIndent();
        System.out.println("ifThenOp");
        indent += 1;
        ifThen.getCondition().accept(this);
        ifThen.getBodyIf().accept(this);
        indent -= 1;
    }

    @Override
    public void visit (ifThenElse ifThenElseOp){
        printIndent();
        System.out.println("ifThenElseOp");
        indent += 1;
        ifThenElseOp.getCondition().accept(this);
        ifThenElseOp.getBodyIf().accept(this);
        ifThenElseOp.getBodyElse().accept(this);
        indent -= 1;
    }

    @Override
    public void visit (whileOp whileOp){
        printIndent();
        System.out.println("whileOp");
        indent+=1;
        whileOp.getCondition().accept(this);
        whileOp.getBodyWhile().accept(this);
        indent -= 1;
    }

    @Override
    public void visit(binaryExpression binary){
        printIndent();
        System.out.println("binaryExpression " + binary.getOperator());
        indent += 1;
        binary.getLeftOperand().accept(this);
        binary.getRightOperand().accept(this);
        indent -= 1;
    }

    @Override
    public void visit (unaryExpression unary){
        printIndent();
        System.out.println("unaryExpression");
        indent+=1;
        unary.getOperand().accept(this);
        indent -= 1;
    }

    @Override
    public void visit (identifierExpression identifier){
        printIndent();
        System.out.println("identifier: "+ identifier.getName());
    }

    @Override
    public void visit (constantExpression constant){
        printIndent();
        System.out.println(constant.getTypeConstant() +": \""+constant.getConstant()+ "\"");
    }

    @Override
    public void visit (CallOp call){
        printIndent();
        System.out.println("CallOp");
        indent += 1;
        call.getIdFun().accept(this);
        for (expressionNode expr : call.getArgs()){
            expr.accept(this);
        }
        indent -= 1;
    }

    @Override
    public void visit(CaseOp caseOp){
        printIndent();
        System.out.println("caseOp");
        indent += 1;
        caseOp.getConstant().accept(this);
        for(statOp stat : caseOp.getStatements()){
            stat.accept(this);
        }
        indent -= 1;
    }

    @Override
    public void visit(BodySwitchOp bodySwitch){
        printIndent();
        System.out.println("bodySwitch");
        indent += 1;
        for(CaseOp cond : bodySwitch.getCases()){
            cond.accept(this);
        }
    }

    @Override
    public void visit(SwitchOp switchOp){
        printIndent();
        System.out.println("switchOp");
        indent += 1;
        switchOp.getVariable().accept(this);
        switchOp.getBodySwitchOp().accept(this);
        indent -= 1;
    }

}
