package visitors;

import classNode.DeclarationNode.*;
import classNode.Expression.*;
import classNode.Stat.*;
import classNode.jumpStatement.ifThenElse;
import classNode.jumpStatement.ifThenOp;
import classNode.jumpStatement.whileOp;
import classNode.main.BeginEndOp;
import classNode.main.BodyOp;
import classNode.main.ProgramOp;
import classNode.main.statOp;
import semanticAnalyzer.ScopeStack;
import semanticAnalyzer.ScopeTable;
import semanticAnalyzer.SemanticError;
import semanticAnalyzer.Symbols.EntryTable;
import semanticAnalyzer.Symbols.FunctionEntry;
import semanticAnalyzer.Symbols.Symbol;
import semanticAnalyzer.Symbols.VarEntry;

import java.util.ArrayList;
import java.util.List;


public class ScopeVisitor implements visitor {
    private final ScopeStack scopeTables;
    private ScopeTable currentScope;

    public ScopeVisitor(){
        scopeTables = new ScopeStack();
    }

    @Override
    public void visit(ProgramOp programOp) {
        currentScope = new ScopeTable("Program",null);
        scopeTables.push(currentScope);
        for (DeclOp declaration : programOp.getDefs()){
            if(declaration.getClass().equals(DefDeclOp.class)) {
                DefDeclOp decl = (DefDeclOp) declaration;
                FunctionEntry functionEntry = new FunctionEntry(new ArrayList<>(),decl.getType());
                for (ParDeclOp param : ((DefDeclOp) declaration).getParams()) {
                    for (PvarOp par : param.getParams()) {
                        VarEntry var = new VarEntry(param.getType(), par.getRef());
                        functionEntry.getVars().add(var);
                    }
                }
                try {
                    Symbol symbol = new Symbol("FUNCTION", decl.getFunName().getName());
                    currentScope.addSymbol(functionEntry, symbol);
                }catch (SemanticError e){
                    SemanticError semanticError = new SemanticError(decl.getFunName().getLine(), e.getMessage(), decl.getFunName().getColumn());
                    System.err.println(semanticError.getMessage());
                    System.exit(1);
                }
            }else {
                declaration.accept(this);
            }
        }
        for (DeclOp declaration : programOp.getDefs()){
            if(declaration.getClass().equals(DefDeclOp.class)) {
                declaration.accept(this);
            }
        }
        programOp.getProgram().accept(this);
        ScopeTable globalScope = currentScope.copy();
        programOp.setGlobalScope(globalScope);
        scopeTables.pop();
        currentScope = null;
        if(scopeTables.size() == 0){
            //System.out.println("Analisi dello scoping conclusa corretamente");
        }
    }

    @Override
    public void visit (BeginEndOp beginEndOp) {
        ScopeTable beginScope = new ScopeTable("Main", currentScope);
        currentScope = beginScope;
        scopeTables.push(beginScope);
        for (varDeclOp decl : beginEndOp.getDeclOps()){
            decl.accept(this);
        }
        for(statOp stat : beginEndOp.getStatOps()){
            stat.accept(this);
        }
        beginEndOp.setMainScope(beginScope);
        scopeTables.pop();
        currentScope = scopeTables.top();
    }

    @Override
    public void visit (BodyOp bodyOp) {
        for (varDeclOp decl : bodyOp.getDeclaration()){
            decl.accept(this);
        }
        for (statOp stat : bodyOp.getStatement()){
            stat.accept(this);
        }
        //ScopeTable bodyScope = currentScope.copy();
        bodyOp.setBodyScope(currentScope);
    }

    @Override
    public void visit (DefDeclOp declOp) {
        ScopeTable functionScope = new ScopeTable(declOp.getFunName().getName(), currentScope);
        scopeTables.push(functionScope);
        currentScope = functionScope;
        for(ParDeclOp param : declOp.getParams()){
            param.accept(this);
        }
        declOp.getBodyFun().accept(this);
        declOp.setScopeFun(functionScope);
        currentScope = functionScope.getParentScope();
        scopeTables.pop();
    }

    @Override
    public void visit(ParDeclOp parDeclOp) {
        for (PvarOp param : parDeclOp.getParams()) {
            Symbol var = new Symbol("VAR", param.getVar().getName());
            VarEntry par = new VarEntry(parDeclOp.getType(), param.getRef());
            try {
                currentScope.addSymbol(par, var);
            }catch (SemanticError e){
                SemanticError semanticError = new SemanticError(param.getVar().getLine(), e.getMessage(), param.getVar().getColumn());
                System.err.println(semanticError.getMessage());
                System.exit(1);
            }
        }
        for (PvarOp param : parDeclOp.getParams()) {
            param.accept(this);
        }
    }

    @Override
    public void visit (PvarOp pvarOp) {
        pvarOp.getVar().accept(this);
        pvarOp.setScopeTable(currentScope);
    }

    @Override
    public void visit(varDeclOp varDeclOp) {
        try {
            for (varOptInitOp var : varDeclOp.getVarOptInits()) {
                String type;
                if (typesDecl(varDeclOp.getType().toString()) != null) {
                    type = varDeclOp.getType().toString().trim();
                } else if (varDeclOp.getVarOptInits().size() == 1 && varDeclOp.getVarOptInits().get(0).getExpression() == null) {
                    type = varDeclOp.getType().getClass().getSimpleName().toLowerCase();
                } else {
                    throw new SemanticError(var.getIdentifier().getLine(),"Errore semantico: inizializzazione " +
                            " variabile "+ var.getIdentifier().getName() + "non effettuata correttamente", var.getIdentifier().getColumn());
                }
                Symbol variable = new Symbol("VAR", var.getIdentifier().getName());
                VarEntry entry = new VarEntry(type, null);
                currentScope.addSymbol(entry, variable);
            }
            for (varOptInitOp varOp : varDeclOp.getVarOptInits()) {
                varOp.accept(this);
            }
            //ScopeTable varDeclScope = currentScope.copy();
            varDeclOp.setDeclScope(currentScope);
        }catch (SemanticError e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void visit(varOptInitOp varOptInitOp) {
        varOptInitOp.getIdentifier().accept(this);
        if (varOptInitOp.getExpression() != null) {
            varOptInitOp.getExpression().accept(this);
        }
        varOptInitOp.setScopeTable(currentScope);
    }

    @Override
    public void visit(AssignOp assignOp) {
        int vars = assignOp.getVariables().size();
        int exprs = assignOp.getExpression().size();
        try {
            if (vars != exprs) {
                throw new SemanticError(assignOp.getExpression().get(0).getLine(),"Errore semantico nell'assegnamento il numero di parametri a sinistra o a destra non coincide",
                        assignOp.getExpression().get(0).getColumn());
            } else if (vars != 1 && getTypes(assignOp.getExpression()).contains(CallOp.class)) {
                throw new SemanticError(assignOp.getExpression().get(0).getLine(),
                        "Errore semantico: assegnamento multiplo effettuato con chiamata a funzione",
                        assignOp.getExpression().get(0).getColumn());
            }
            for (identifierExpression id : assignOp.getVariables()) {
                id.accept(this);
            }
            for (expressionNode expr : assignOp.getExpression()) {
                expr.accept(this);
            }
            //ScopeTable assignScope = currentScope.copy();
            assignOp.setAssignScope(currentScope);
        }catch (SemanticError e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    @Override
    public void visit(FunCallStat funCallStat) {
        int parametersGo = funCallStat.getArgs().size();
        ScopeTable globalScope = scopeTables.top(0);
        Symbol func = new Symbol("FUNCTION", funCallStat.getIdFun().getName());
        EntryTable defParameters = globalScope.lookUpSymbol(func);
        try {
            if (defParameters != null) {
                if (((FunctionEntry) defParameters).getVars().size() != parametersGo) {
                    throw new SemanticError(funCallStat.getIdFun().getLine(),
                            "Errore semantico: numero di parametri inseriti nella chiamata a funzione errato",
                            funCallStat.getIdFun().getColumn());
                }
            } else {
                throw new SemanticError(funCallStat.getIdFun().getLine(),
                        "Errore semantico: funzione \"" + funCallStat.getIdFun().getName() + "\" non definita",
                        funCallStat.getIdFun().getColumn());
            }
        }catch (SemanticError e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        for (expressionNode expr : funCallStat.getArgs()){
            expr.accept(this);
        }
        //ScopeTable funScope = currentScope.copy();
        funCallStat.setFunScope(currentScope);
    }

    @Override
    public void visit(ReadOp readOp) {
        for(identifierExpression id : readOp.getVariables()){
            id.accept(this);
        }
        //ScopeTable readScope = currentScope.copy();
        readOp.setReadScope(currentScope);
    }

    @Override
    public void visit(WriteOp writeOp) {
        for(expressionNode expr : writeOp.getVariables()){
            expr.accept(this);
        }
        //ScopeTable writeScope = currentScope.copy();
        writeOp.setWriteScope(currentScope);
    }

    @Override
    public void visit(returnOp returnStat) {
        returnStat.getExpr().accept(this);
        //ScopeTable returnScope = currentScope.copy();
        returnStat.setReturnScope(currentScope);
    }

    public void visit(ifThenOp ifThenOp) {
        ScopeTable ifScope = new ScopeTable("IfThen",currentScope);
        currentScope = ifScope;
        ifThenOp.getCondition().accept(this);

        scopeTables.push(ifScope);
        ifThenOp.getBodyIf().accept(this);
        ifThenOp.setIfScope(ifScope);

        currentScope = ifScope.getParentScope();
        scopeTables.pop();
    }

    public void visit(ifThenElse ifThenElse) {
        ScopeTable ifScope = new ScopeTable("IfTrue",currentScope);
        currentScope = ifScope;
        scopeTables.push(ifScope);
        ifThenElse.getCondition().accept(this);

        ifThenElse.getBodyIf().accept(this);
        ifThenElse.setIfScope(ifScope);
        currentScope = ifScope.getParentScope();
        scopeTables.pop();

        ScopeTable elseScope = new ScopeTable("IfFalse",currentScope);
        currentScope = elseScope;
        scopeTables.push(elseScope);

        ifThenElse.getBodyElse().accept(this);
        ifThenElse.setElseScope(elseScope);
        currentScope = elseScope.getParentScope();
        scopeTables.pop();
    }

    public void visit(whileOp whileOp) {
        ScopeTable whileScope = new ScopeTable("While",currentScope);
        currentScope = whileScope;
        scopeTables.push(whileScope);
        whileOp.getCondition().accept(this);

        whileOp.getBodyWhile().accept(this);
        whileOp.setScopeTable(whileScope);

        currentScope = whileScope.getParentScope();
        scopeTables.pop();
    }

    @Override
    public void visit(binaryExpression binary) {
        binary.getLeftOperand().accept(this);
        binary.getRightOperand().accept(this);
        //ScopeTable binaryScope = currentScope.copy();
        binary.setScopeExpression(currentScope);
    }

    @Override
    public void visit(CallOp call) {
        int parametersGo = call.getArgs().size();
        ScopeTable globalScope = scopeTables.top(0);
        Symbol func = new Symbol("FUNCTION", call.getIdFun().getName());
        EntryTable defParameters = globalScope.lookUpSymbol(func);
        try {
            if (defParameters != null) {
                if (((FunctionEntry) defParameters).getVars().size() != parametersGo) {
                    throw new SemanticError(call.getIdFun().getLine(),
                            "Errore semantico: numero di parametri inseriti nella chiamata a funzione errato",
                            call.getIdFun().getColumn());
                }
            } else {
                throw new SemanticError(call.getIdFun().getLine(),
                        "Errore semantico: funzione \"" + call.getIdFun().getName() + "\" non definita",
                        call.getIdFun().getColumn());
            }
            for(expressionNode expr : call.getArgs()){
                expr.accept(this);
            }
        }catch (SemanticError e){
            System.err.println(e.getMessage());
            System.exit(1);
        }
        //ScopeTable funScope = currentScope.copy();
        call.setFunScope(currentScope);
    }

    @Override
    public void visit(constantExpression constant) {
        //ScopeTable constantScope = currentScope.copy();
        constant.setScopeTable(currentScope);
    }

    @Override
    public void visit(identifierExpression id) {
        //ScopeTable identifierScope = currentScope.copy();
        id.setScopeTable(currentScope);
    }

    @Override
    public void visit(unaryExpression unary) {
        unary.getOperand().accept(this);
        //ScopeTable unaryScope = currentScope.copy();
        unary.setScopeTable(currentScope);
    }

    private List<Object> getTypes(List<expressionNode> exprs){
        List<Object> types = new ArrayList<>();
        for (expressionNode expr : exprs){
            types.add(expr.getClass());
        }
        return types;
    }

    private String typesDecl(String type){
        return switch (type) {
            case "integer" -> "integer";
            case "double" -> "double";
            case "bool" -> "bool";
            case "string" -> "string";
            default -> null;
        };
    }


}
