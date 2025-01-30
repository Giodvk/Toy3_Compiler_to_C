package visitors;

import classNode.DeclarationNode.*;
import classNode.Expression.*;
import classNode.Stat.*;
import classNode.jumpStatement.ifThenElse;
import classNode.jumpStatement.ifThenOp;
import classNode.jumpStatement.whileOp;
import classNode.main.*;
import semanticAnalyzer.OperandTable.Triple;
import semanticAnalyzer.OperandTable.binaryOperandTable;
import semanticAnalyzer.OperandTable.unaryOperandTable;
import semanticAnalyzer.SemanticError;
import semanticAnalyzer.Symbols.EntryTable;
import semanticAnalyzer.Symbols.FunctionEntry;
import semanticAnalyzer.Symbols.Symbol;
import semanticAnalyzer.Symbols.VarEntry;

import java.util.List;

public class TypeChecking implements visitor{

    private String currentFun;
    private final binaryOperandTable binaryTable = new binaryOperandTable();
    private final unaryOperandTable unaryTable = new unaryOperandTable();

    public TypeChecking(ProgramOp programOp){
        ScopeVisitor scopeVisitor = new ScopeVisitor();
        programOp.accept(scopeVisitor);
    }

    @Override
    public void visit(FunCallStat funCallStat){
        FunctionEntry types = (FunctionEntry) funCallStat.getFunScope().lookUpSymbol(new Symbol("FUNCTION",
                funCallStat.getIdFun().getName()));
        int i = 0 ;
        try {
            for (expressionNode expr : funCallStat.getArgs()) {
                expr.accept(this);
                String typeParam = types.getVars().get(i).getType();
                if((expr.getType().equals("integer") && typeParam.equals("double")) || (expr.getType().equals("double") && typeParam.equals("integer"))){
                    i++;
                    continue;
                }
                if (!expr.getType().equals(typeParam)) {
                    throw new SemanticError(expr.getLine(), "Errore semantico: passato un valore " + expr.getType()
                            + " invece che " + typeParam, expr.getColumn());
                }
                if(types.getVars().get(i).isRef()){
                    if(!(expr instanceof identifierExpression)){
                        throw new SemanticError(expr.getLine(), "Errore semantico: passata per riferimento un espressione che" +
                                " non è un identificatore", expr.getColumn());
                    }
                }
                i++;
            }
        } catch (SemanticError e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        funCallStat.setType(types.getReturnType());
    }

    @Override
    public void visit(ReadOp readOp) {
        for(identifierExpression id : readOp.getVariables()){
            id.accept(this);
        }
    }

    @Override
    public void visit(WriteOp writeOp) {
        for(expressionNode expr : writeOp.getVariables()){
            expr.accept(this);
        }
    }

    @Override
    public void visit(returnOp returnStat) {
        returnStat.getExpr().accept(this);
        returnStat.setType(returnStat.getExpr().getType());
        Symbol symbol = new Symbol("FUNCTION", currentFun);
        FunctionEntry Fun = (FunctionEntry) returnStat.getReturnScope().lookUpSymbol(symbol);
        String typeFun = Fun.getReturnType();
        if(typeFun.equals(" ")){
            typeFun = "void";
        }
        try {
            if (!returnStat.getType().equals(typeFun.trim())) {
                throw new SemanticError(returnStat.getExpr().getLine(), "Errore semantico: la funzione dovrebbe ritornare "
                        + "il tipo " + typeFun + " invece di " + returnStat.getType(), returnStat.getExpr().getColumn());
            }
        }catch (SemanticError e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void visit(ifThenOp ifThenOp){
        ifThenOp.getCondition().accept(this);
        try{
            if(ifThenOp.getCondition().getType() == null || !ifThenOp.getCondition().getType().equals("bool")){
                throw new SemanticError(ifThenOp.getCondition().getLine(), "Errore semantico: l'espressione all'interno dell'" +
                        "if non è di tipo bool", ifThenOp.getCondition().getColumn());
            }
        }catch (SemanticError e){
           System.err.println(e.getMessage());
           System.exit(1);
        }
        ifThenOp.getBodyIf().accept(this);
    }


    @Override
    public void visit(ifThenElse ifThenElse){
        ifThenElse.getCondition().accept(this);
        try{
            if(ifThenElse.getCondition().getType() == null || !ifThenElse.getCondition().getType().equals("bool")){
                throw new SemanticError(ifThenElse.getCondition().getLine(), "Errore semantico: l'espressione all'interno dell'" +
                        "if non è di tipo bool", ifThenElse.getCondition().getColumn());
            }
        }catch(SemanticError e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        ifThenElse.getBodyIf().accept(this);
        ifThenElse.getBodyElse().accept(this);
    }

    @Override
    public void visit(whileOp whileOp){
        whileOp.getCondition().accept(this);
        try {
            if (whileOp.getCondition().getType() == null || !whileOp.getCondition().getType().equals("bool")) {
                throw new SemanticError(whileOp.getCondition().getLine(), "Errore semantico: l'espressione all'interno del" +
                        " ciclo while non è di tipo bool", whileOp.getCondition().getColumn());
            }
        } catch (SemanticError e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        whileOp.getBodyWhile().accept(this);
    }

    @Override
    public void visit(ProgramOp program) {
        for(DeclOp decl : program.getDefs()){
            decl.accept(this);
        }
        program.getProgram().accept(this);
    }

    @Override
    public void visit(BeginEndOp beginEndOp) {
        for(varDeclOp declOp : beginEndOp.getDeclOps()){
            declOp.accept(this);
        }
        for(statOp stat : beginEndOp.getStatOps()){
            stat.accept(this);
        }
    }

    @Override
    public void visit(BodyOp bodyOp) {
        for (varDeclOp declOp : bodyOp.getDeclaration()){
            declOp.accept(this);
        }
        for (statOp stat : bodyOp.getStatement()){
            stat.accept(this);
        }
    }

    @Override
    public void visit(DefDeclOp function) {
        currentFun = function.getFunName().getName();
        try {
            if (function.getType().equals(" ")) {
                for (statOp stat : function.getBodyFun().getStatement()) {
                    if (stat instanceof returnOp) {
                        throw new SemanticError(((returnOp) stat).getExpr().getLine(), "Errore semantico: il ritorno della funzione "
                                + function.getFunName().getName() + " è di tipo void", ((returnOp) stat).getExpr().getColumn());
                    }
                }
            }
        }catch (SemanticError e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        function.getBodyFun().accept(this);
    }

    @Override
    public void visit(ParDeclOp parameters) {

    }

    @Override
    public void visit(PvarOp parameter) {

    }

    @Override
    public void visit(varDeclOp variable) {
        for (varOptInitOp var : variable.getVarOptInits()){
            var.accept(this);
        }
    }

    @Override
    public void visit(varOptInitOp variable) {
        variable.getIdentifier().accept(this);
        try {
            if(variable.getExpression() != null) {
                variable.getExpression().accept(this);
                if (!variable.getIdentifier().getType().equals(variable.getExpression().getType())) {
                    throw new SemanticError(variable.getExpression().getLine(), "Errore semantico: inizializzazione di un "
                            + variable.getIdentifier().getType() + " con un " + variable.getExpression().getType(), variable.getExpression().getColumn());
                }
            }
        }catch (SemanticError e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void visit(AssignOp assignOp){
        try {
            for (int i = 0; i < assignOp.getExpression().size(); i++) {
                identifierExpression id = assignOp.getVariables().get(i);
                id.accept(this);
                expressionNode expr = assignOp.getExpression().get(i);
                expr.accept(this);
                if(id.getType().equals("double") && expr.getType().equals("integer")){
                    //System.out.println("Cast da intero a double");
                    continue;
                }
                if(id.getType().equals("integer") && expr.getType().equals("double")){
                    //System.out.println("Cast da double a integer");
                    continue;
                }
                if (!id.getType().equals(expr.getType())) {
                    throw new SemanticError(expr.getLine(), "Errore semantico: assegnazione di un "
                            + expr.getType() + " ad un tipo " + id.getType() , expr.getColumn());
                }
            }
        }catch (SemanticError e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }


    @Override
    public void visit(CallOp callOp){
        FunctionEntry types = (FunctionEntry) callOp.getFunScope().lookUpSymbol(new Symbol("FUNCTION", callOp.getIdFun().getName()));
        int i = 0 ;
        try {
            for (expressionNode expr : callOp.getArgs()) {
                expr.accept(this);
                String typeParam = types.getVars().get(i).getType();
                if (!expr.getType().equals(typeParam)) {
                    throw new SemanticError(callOp.getLine(), "Errore semantico: passato un valore " + expr.getType()
                            + " invece che " + typeParam, callOp.getColumn());
                }
                if(types.getVars().get(i).isRef()){
                    if(!(expr instanceof identifierExpression)){
                        throw new SemanticError(expr.getLine(), "Errore semantico: passata per riferimento un espressione che" +
                                " non è un identificatore", expr.getColumn());
                    }
                }
                i++;
            }
        } catch (SemanticError e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if(types.getReturnType().isEmpty()) {
            callOp.setType("void");
        }else {
            callOp.setType(types.getReturnType());
        }
    }




    @Override
    public void visit(binaryExpression binary){
        binary.getLeftOperand().accept(this);
        binary.getRightOperand().accept(this);
        Triple<String, String, String > operator = new Triple<>(binary.getOperator(),
                binary.getLeftOperand().getType(), binary.getRightOperand().getType());
        try {
            if (binaryTable.getTable().get(operator) != null) {
                binary.setType(binaryTable.getTable().get(operator));
            } else {
                throw new SemanticError(binary.getLine(), "Errore semantico: Operazione "+ binary.getOperator() +" tra " + binary.getLeftOperand().getType()
                        + " e " + binary.getRightOperand().getType() + " non è permessa", binary.getColumn());
            }
        }catch (SemanticError e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void visit(unaryExpression unary){
        unary.getOperand().accept(this);
        Triple<String,String,String> operator = new Triple<>(unary.getOperator(),
                unary.getOperand().getType(), null);
        try{
            if(unaryTable.getUnaryOperandTable().get(operator) != null) {
                unary.setType(unaryTable.getUnaryOperandTable().get(operator));
            }else{
                throw new SemanticError(unary.getLine(), "Operazione "+ unary.getOperator() + " con tipo "
                + unary.getOperand().getType() + " non concessa", unary.getColumn());
            }
        }catch(SemanticError e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void visit(identifierExpression identifierExpression){
        Symbol id = new Symbol("VAR", identifierExpression.getName());

        try {
            if (identifierExpression.getScopeTable().lookUpSymbol(id) != null) {
                EntryTable var = identifierExpression.getScopeTable().lookUpSymbol(id);
                identifierExpression.setType(((VarEntry)var).getType());
            } else {
                throw new SemanticError(identifierExpression.getLine(), "Errore semantico : variabile "
                + identifierExpression.getName() + " non dichiarata", identifierExpression.getColumn());
            }
        }catch (SemanticError e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void visit(constantExpression constant){
        switch(constant.getTypeConstant()){
            case "true", "false":
                constant.setType("bool");
                break;
            case "int_const":
                constant.setType("integer");
                break;
            case "double_const":
                constant.setType("double");
                break;
            case "string_const":
                constant.setType("string");
                break;
            case "char_const":
                constant.setType("char");
                break;
            default:
                break;
        }
    }

    @Override
    public void visit(CaseOp caseOp) {
        caseOp.getConstant().accept(this);
        for(statOp statOp : caseOp.getStatements()){
            statOp.accept(this);
        }
    }

    @Override
    public void visit(BodySwitchOp bodySwitchOp){
        for(CaseOp caseOp : bodySwitchOp.getCases()){
            caseOp.accept(this);
        }
    }

    @Override
    public void visit(SwitchOp switchOp){
        switchOp.getVariable().accept(this);
        switchOp.getBodySwitchOp().accept(this);
        List<CaseOp> cases = switchOp.getBodySwitchOp().getCases();
        try{
            for(CaseOp caseOp : cases){
                if(!switchOp.getVariable().getType().equals(caseOp.getConstant().getType())){
                    throw new SemanticError(caseOp.getConstant().getLine(),
                            "Errore semantico : type mismatch, la costante " +
                            caseOp.getConstant().getConstant().toString() + " non è di tipo "+ switchOp.getVariable().getType(),
                            caseOp.getConstant().getColumn());
                }
            }
        }catch (SemanticError e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
