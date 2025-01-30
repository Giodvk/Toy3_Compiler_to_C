package visitors;

import classNode.DeclarationNode.*;
import classNode.Expression.*;
import classNode.Stat.*;
import classNode.jumpStatement.ifThenElse;
import classNode.jumpStatement.ifThenOp;
import classNode.jumpStatement.whileOp;
import classNode.main.*;

public interface visitor {
    void visit (ProgramOp program);
    void visit (BeginEndOp beginEndOp);
    void visit (BodyOp bodyOp);
    void visit (DefDeclOp function);
    void visit (ParDeclOp parameters);
    void visit (PvarOp parameter);
    void visit (varDeclOp variable);
    void visit (varOptInitOp variable);
    void visit (AssignOp assignOp);
    void visit (FunCallStat funCallStat);
    void visit (ReadOp readOp);
    void visit (WriteOp writeOp);
    void visit (returnOp returnStat);
    void visit (ifThenOp ifThen);
    void visit (ifThenElse ifThenElseOp);
    void visit (whileOp whileOp);
    void visit (binaryExpression binary);
    void visit (CallOp call);
    void visit (constantExpression constant);
    void visit (identifierExpression id);
    void visit (unaryExpression unary);
    void visit(SwitchOp switchOp);
    void visit(BodySwitchOp bodySwitchOp);
    void visit(CaseOp caseOp);
}
