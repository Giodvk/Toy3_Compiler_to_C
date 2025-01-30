package visitors;
import classNode.DeclarationNode.*;
import classNode.Expression.*;
import classNode.Stat.*;
import classNode.jumpStatement.ifThenElse;
import classNode.jumpStatement.ifThenOp;
import classNode.jumpStatement.whileOp;
import classNode.main.*;
import semanticAnalyzer.ScopeStack;
import semanticAnalyzer.ScopeTable;
import semanticAnalyzer.Symbols.EntryTable;
import semanticAnalyzer.Symbols.FunctionEntry;
import semanticAnalyzer.Symbols.Symbol;
import semanticAnalyzer.Symbols.VarEntry;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class WriteCVisitor implements visitor {
    private FileWriter fileC;
    private String actualFun;
    private int maxBuffer = 1;
    private int currentBuffer = 1; //contatore buffer
    private Hashtable<String, Boolean> releaseTempBuffer = new Hashtable();
    public WriteCVisitor(String nomeFile) {
        try {
            fileC = new FileWriter(nomeFile);
        } catch (IOException e) {
            System.err.println("Errore nella creazione del file: " + e.getMessage());
        }
    }

    @Override
    public void visit(ProgramOp programOp) {

        List<varDeclOp> vars = new ArrayList<>();
        this.write("""
                #include "stdio.h"
                #include "stdlib.h"
                #include "string.h"
                
                char *tempBuffer;
                char bufferIntDouble[1000];
                """);
        for (DeclOp var : programOp.getDefs()) {
            if (var instanceof varDeclOp) {
                var.accept(this);
                vars.add((varDeclOp) var);
            }
        }
        for (DeclOp decl : programOp.getDefs()) {
            if (decl instanceof DefDeclOp) {
                if (((DefDeclOp) decl).getType().equals("string")) {
                    this.write("char* " + ((DefDeclOp) decl).getFunName().getName() + "(");
                }else if(((DefDeclOp) decl).getType().equals("bool") || ((DefDeclOp) decl).getType().equals("integer")){
                    this.write("int " + ((DefDeclOp) decl).getFunName().getName() + "(");
                }else if(((DefDeclOp) decl).getType().isEmpty()){
                    this.write("void " + ((DefDeclOp) decl).getFunName().getName() + "(");
                }else {
                    this.write(((DefDeclOp) decl).getType() + " " + ((DefDeclOp) decl).getFunName().getName() + "(");
                }
                for (int i = 0; i< ((DefDeclOp) decl).getParams().size(); i++) {
                    ParDeclOp params = ((DefDeclOp) decl).getParams().get(i);
                    params.accept(this);
                    if(i < ((DefDeclOp) decl).getParams().size() - 1) {
                        this.write(", ");
                    }

                }
                this.write(");\n");
            }
        }
        init_globalString(vars);
        for (DeclOp decl : programOp.getDefs()) {
            if (decl instanceof DefDeclOp) {
                decl.accept(this);
            }
        }
        programOp.getProgram().accept(this);
        try {
            this.fileC.close();
        } catch (IOException e) {
            System.err.println("Errore nella chiusura del file: " + fileC + e.getMessage());
        }

    }

    @Override
    public void visit(DefDeclOp defDeclOp) {
        actualFun = defDeclOp.getFunName().getName();
        if(defDeclOp.getType().equals("string")){
            this.write("char* " + defDeclOp.getFunName().getName() + "(");
        }else if(defDeclOp.getType().equals("bool") || defDeclOp.getType().equals("integer")){
            this.write("int " + defDeclOp.getFunName().getName() + "(");
        }else if(defDeclOp.getType().isEmpty()){
            this.write("void " + defDeclOp.getFunName().getName() + "(");
        }else {
            this.write(defDeclOp.getType() + " " + defDeclOp.getFunName().getName() + "(");
        }
        for (int i = 0; i < defDeclOp.getParams().size(); i++) {
            defDeclOp.getParams().get(i).accept(this);
            if(i < defDeclOp.getParams().size() - 1){
                this.write(", ");
            }
        }
        this.write(") {\n");
        defDeclOp.getBodyFun().accept(this);
        this.write("}\n");
    }

    @Override
    public void visit(BeginEndOp beginEndOp) {
        this.write("int main() { \n");
        this.write("init_globalString();\n");
        for (varDeclOp decl : beginEndOp.getDeclOps()) {
            decl.accept(this);
        }
        for (statOp stat : beginEndOp.getStatOps()) {
            stat.accept(this);
        }
        this.write("}\n");
    }

    @Override
    public void visit(ifThenOp ifThenOp) {
        this.write("if (");
        ifThenOp.getCondition().accept(this);
        this.write(") {\n");
        ifThenOp.getBodyIf().accept(this);
        this.write("}\n");
    }

    @Override
    public void visit(ifThenElse ifThenElse) {
        this.write("if (");
        ifThenElse.getCondition().accept(this);
        this.write(") {\n");
        ifThenElse.getBodyIf().accept(this);
        this.write("}else{\n");
        ifThenElse.getBodyElse().accept(this);
        this.write("}\n");
    }

    @Override
    public void visit(whileOp whileOp) {
        this.write("while (");
        whileOp.getCondition().accept(this);
        this.write(") {\n");
        whileOp.getBodyWhile().accept(this);
        this.write("}\n");
    }

    @Override
    public void visit(WriteOp writeOp) {
        boolean flag = prepareFunction(writeOp.getVariables());
        this.write("printf(\"");
        for(expressionNode expr : writeOp.getVariables()){
            switch (expr.getType()){
                case "integer", "bool":
                    this.write("%d ");
                    break;
                case "string":
                    this.write("%s ");
                    break;
                case "double":
                    this.write("%.2lf ");
                    break;
                case "char":
                    this.write("%c ");
                    break;
                default:
                    break;
            }
        }
        if(writeOp.getNewLine()!= null){
            this.write("\\n\", ");
        }else{
            this.write("\", ");
        }
        for(int i = 0; i< writeOp.getVariables().size(); i++){
            expressionNode expr = writeOp.getVariables().get(i);
            expr.accept(this);
            if (i < writeOp.getVariables().size() - 1) {
                this.write(", ");
            }
        }
        this.write(");\n");
        if(flag){
            discardMemoryFun();
        }
    }

    public void visit(ReadOp readOp) {
        for (identifierExpression id : readOp.getVariables()){
            if(!id.getType().equals("string")){
                this.write("scanf(\" ");
                switch (id.getType()){
                    case "integer", "bool":
                      this.write("%d\", & ");
                      break;
                    case "double":
                        this.write("%lf\", & ");
                        break;
                    case "char":
                        this.write("%c\", & ");
                        break;
                    default:
                        break;
                }
                id.accept(this);
                this.write(");\n");
                this.write("getchar();\n");
            }else{
                id.accept(this);
                this.write(" = (char*) realloc(");
                id.accept(this);
                this.write(", sizeof(char) * 1001);\n");
                this.write("fgets("+id.getName()+", 1000, stdin);\n");
                id.accept(this);
                this.write("[strcspn(");
                id.accept(this);
                this.write(", \"\\n\")] = '\\0';\n");
                id.accept(this);
                this.write(" = (char*) realloc(");
                id.accept(this);
                this.write(", sizeof(char) * strlen(");
                id.accept(this);
                this.write("));\n");
            }
        }
    }

    @Override
    public void visit(ParDeclOp parDeclOp) {
        for (int i = 0; i < parDeclOp.getParams().size(); i++) {
            PvarOp param = parDeclOp.getParams().get(i);
            if (parDeclOp.getType().equals("integer") || parDeclOp.getType().equals("bool")) {
                this.write("int ");
            } else if (parDeclOp.getType().equals("string")) {
                this.write("char *");
            } else {
                this.write(parDeclOp.getType() + " ");
            }
            param.accept(this);
            if (i < parDeclOp.getParams().size() - 1) {
                this.write(", ");
            }
        }
    }

    @Override
    public void visit(PvarOp pvarOp) {
        pvarOp.getVar().accept(this);
    }

    @Override
    public void visit(varDeclOp varDeclOp) {
        String type = varDeclOp.getVarOptInits().get(0).getIdentifier().getType()+" ";
        String scope = varDeclOp.getDeclScope().getContext();
        if (type.equals("bool ") || type.equals("integer ")) {
            type = "int ";
            this.write(type);
        }else if (type.equals("string ") && scope.equals("Program")){
            type = "char ";
            this.write(type);
            setPointer(varDeclOp.getVarOptInits());
            if(varDeclOp.getVarOptInits().size() == 1 && typesDecl(varDeclOp.getType().toString()) == null){
                varOptInitOp var = varDeclOp.getVarOptInits().get(0);
                var.getIdentifier().accept(this);
                this.write(";\n");
                var.setExpression(new constantExpression(varDeclOp.getType(), "string_const", var.getIdentifier().getLine(), var.getIdentifier().getColumn()));
                var.getExpression().setType("string");
                return;
            }
        } else if (type.equals("string ")) {
            type = "char ";
            this.write(type);
            setPointer(varDeclOp.getVarOptInits());
            if(varDeclOp.getVarOptInits().size() == 1 && typesDecl(varDeclOp.getType().toString()) == null){
                varOptInitOp var = varDeclOp.getVarOptInits().get(0);
                var.getIdentifier().accept(this);
                this.write(" = (char*) malloc((sizeof(char)* strlen(\""+ varDeclOp.getType().toString() + "\\0\") + 1));\n");
                this.write("strcpy(");
                var.getIdentifier().accept(this);
                this.write(", \"" + varDeclOp.getType().toString()+ "\\0\");\n");
                return;
            }else {
                allocateMemory(varDeclOp.getVarOptInits());
            }
        }else {
            this.write(type);
        }
        if(varDeclOp.getVarOptInits().size() == 1 && typesDecl(varDeclOp.getType().toString())==null){
            varOptInitOp var = varDeclOp.getVarOptInits().get(0);
            var.getIdentifier().accept(this);
            this.write(" = ");
            this.write(varDeclOp.getType().toString()+ ";\n");
            return;
        }
        for (int i = 0; i < varDeclOp.getVarOptInits().size(); i++) {
            varOptInitOp var = varDeclOp.getVarOptInits().get(i);
            var.accept(this);
            if (i < varDeclOp.getVarOptInits().size() - 1) {
                this.write(", ");
            }
        }
        this.write(";\n");
    }

    @Override
    public void visit(varOptInitOp varOptInitOp) {
        String type = varOptInitOp.getIdentifier().getType();
        if(type.equals("string") && varOptInitOp.getScopeTable().getContext().equals("Program")){
                varOptInitOp.getIdentifier().accept(this);
        }else if(type.equals("string")){
           initializeString(varOptInitOp.getIdentifier(), varOptInitOp.getExpression());
        }else {
            varOptInitOp.getIdentifier().accept(this);
            if (varOptInitOp.getExpression() != null) {
                this.write(" = ");
                varOptInitOp.getExpression().accept(this);
            }
        }
    }

    @Override
    public void visit(BodyOp bodyOp) {
        for (varDeclOp decl : bodyOp.getDeclaration()) {
            decl.accept(this);
        }
        for (statOp stat : bodyOp.getStatement()) {
            stat.accept(this);
        }
    }

    @Override
    public void visit(AssignOp assignOp) {
        boolean flag = false;
        if(assignOp.getExpression().size() == 1 && assignOp.getExpression().get(0) instanceof CallOp){
            flag = prepareFunction(((CallOp) assignOp.getExpression().get(0)).getArgs());
        }
        for (int i = 0; i < assignOp.getVariables().size(); i++) {
            identifierExpression id = assignOp.getVariables().get(i);
            expressionNode expr = assignOp.getExpression().get(i);
            if(id.getType().equals("string")){
                if(expr instanceof identifierExpression || expr instanceof constantExpression || expr instanceof CallOp){
                    id.accept(this);
                    this.write(" = (char*) realloc(");
                    id.accept(this);
                    this.write(", sizeof(char) * ");
                    generateLengthExpression(expr);
                    this.write(" + 1);\n");
                    this.write("strcpy(");
                    id.accept(this);
                    this.write(", ");
                    expr.accept(this);
                    this.write(")");
                    if(expr instanceof CallOp){
                        if(releaseTempBuffer.contains(((CallOp) expr).getIdFun().getName())){
                            this.write(";\nfree(tempBuffer)");
                        }
                    }
                }else {
                    expr.accept(this);
                    id.accept(this);
                    this.write(" = (char*)realloc(");
                    id.accept(this);
                    this.write(", sizeof(char) * (strlen(tempBuffer) + 1));\n");
                    this.write("strcpy("+id.getName()+", tempBuffer);\n");
                    this.write("free(tempBuffer)");
                }
            }else {
                id.accept(this);
                this.write(" = ");
                expr.accept(this);
            }
            this.write(";\n");
            if(flag){
                discardMemoryFun();
            }
        }
    }

    @Override
    public void visit(CallOp callOp) {
        FunctionEntry types = (FunctionEntry) callOp.getFunScope().lookUpSymbol(new Symbol("FUNCTION", callOp.getIdFun().getName()));
        this.write(callOp.getIdFun().getName() + "(");
        for(int i = 0; i < callOp.getArgs().size(); i++) {
            VarEntry var = types.getVars().get(i);
            expressionNode expr = callOp.getArgs().get(i);
            if(var.isRef()){
                EntryTable param = ((identifierExpression)expr).getScopeTable().lookUpSymbol(new Symbol("VAR", ((identifierExpression) expr).getName()));
                if (((VarEntry)param).isRef())
                {
                    this.write(((identifierExpression)expr).getName());
                }else{
                    this.write("&" + ((identifierExpression) expr).getName());
                }
            }else{
                expr.accept(this);
            }
            if(i < callOp.getArgs().size() - 1) {
                this.write(", ");
            }
        }
        this.write(")");
    }

    @Override
    public void visit(FunCallStat funCallStat) {
        boolean flag = prepareFunction(funCallStat.getArgs());
        FunctionEntry types = (FunctionEntry) funCallStat.getFunScope().lookUpSymbol(new Symbol("FUNCTION", funCallStat.getIdFun().getName()));
        this.write(funCallStat.getIdFun().getName() + "(");
        for(int i = 0; i < funCallStat.getArgs().size(); i++) {
            VarEntry var = types.getVars().get(i);
            expressionNode expr = funCallStat.getArgs().get(i);
            if(var.isRef()){
                EntryTable param = ((identifierExpression)expr).getScopeTable().lookUpSymbol(new Symbol("VAR", ((identifierExpression) expr).getName()));
                if (((VarEntry)param).isRef())
                {
                    this.write(((identifierExpression)expr).getName());
                }else{
                    this.write("&" + ((identifierExpression) expr).getName());
                }
            }else{
                expr.accept(this);
            }
            if(i < funCallStat.getArgs().size() - 1) {
                this.write(", ");
            }
        }
        this.write(");\n");
        if(flag) {
            discardMemoryFun();
        }
        if(funCallStat.getType().equals("string")){
            if(releaseTempBuffer.containsKey(funCallStat.getIdFun().getName())){
                this.write("free(tempBuffer);\n");
            }
        }
    }

    @Override
    public void visit(returnOp returnOp) {
        if(returnOp.getExpr().getType().equals("string") && returnOp.getExpr() instanceof binaryExpression){
            returnOp.getExpr().accept(this);
            this.write("return tempBuffer;\n");
            releaseTempBuffer.put(actualFun,true);
        }else {
            this.write("return ");
            returnOp.getExpr().accept(this);
            this.write(";\n");
        }
    }

    @Override
    public void visit(identifierExpression id) {
        changeIdentifier(id);
        this.write(id.getName());
    }

    @Override
    public void visit(unaryExpression unaryExpression) {
        if(unaryExpression.getOperator().equals("not")){
            this.write("!(");
            unaryExpression.getOperand().accept(this);
            this.write(")");
        }else if(unaryExpression.getOperator().equals("-")){
            this.write("-");
            unaryExpression.getOperand().accept(this);
        }
    }

    @Override
    public void visit(binaryExpression binary) {
        if(binary.getOperator().equals("and")){
            binary.getLeftOperand().accept(this);
            this.write(" && ");
            binary.getRightOperand().accept(this);
        }else if(binary.getOperator().equals("or")){
            binary.getLeftOperand().accept(this);
            this.write(" || ");
            binary.getRightOperand().accept(this);
        }else if(binary.getOperator().equals("<>") &&
                !(binary.getLeftOperand().getType().equals("string") && binary.getLeftOperand().getType().equals("string"))) {
            binary.getLeftOperand().accept(this);
            this.write(" != ");
            binary.getRightOperand().accept(this);
        }else if(binary.getOperator().equals("+") &&
                (binary.getLeftOperand().getType().equals("string") || binary.getLeftOperand().getType().equals("string"))) {
            String temp = "tempBuffer";
            this.write(temp + "= (char*) malloc((sizeof(char)* 1000) + 1);\n");
            this.write("strcpy(" + temp + ", \"\");\n");
            concatenateString(binary, temp);
        }else if(binary.getLeftOperand().getType().equals("string") && binary.getLeftOperand().getType().equals("string")){
            switch (binary.getOperator()) {
                case "==" -> {
                    this.write("strcmp(");
                    binary.getLeftOperand().accept(this);
                    this.write(", ");
                    binary.getRightOperand().accept(this);
                    this.write(") == 0");
                }
                case "<>" -> {
                    this.write("strcmp(");
                    binary.getLeftOperand().accept(this);
                    this.write(", ");
                    binary.getRightOperand().accept(this);
                    this.write(") != 0");
                }
                case ">" -> {
                    this.write("strcmp(");
                    binary.getLeftOperand().accept(this);
                    this.write(", ");
                    binary.getRightOperand().accept(this);
                    this.write(") > 0");
                }
                case "<" -> {
                    this.write("strcmp(");
                    binary.getLeftOperand().accept(this);
                    this.write(", ");
                    binary.getRightOperand().accept(this);
                    this.write(") < 0");
                }
                case ">=" -> {
                    this.write("strcmp(");
                    binary.getLeftOperand().accept(this);
                    this.write(", ");
                    binary.getRightOperand().accept(this);
                    this.write(") >= 0");
                }
                case "<=" -> {
                    this.write("strcmp(");
                    binary.getLeftOperand().accept(this);
                    this.write(", ");
                    binary.getRightOperand().accept(this);
                    this.write(") <= 0");
                }
            }

        }else{
            binary.getLeftOperand().accept(this);
            this.write(" " + binary.getOperator() + " ");
            binary.getRightOperand().accept(this);
        }
    }

    @Override
    public void visit(constantExpression constant) {
        if(constant.getType().equals("bool")){
            if(constant.getTypeConstant().equals("true")){
                this.write("1 ");
            }else{
                this.write("0 ");
            }
        }else if(constant.getType().equals("string")){
            this.write("\""+ constant.getConstant() + "\\0\"");
        }else {
            this.write(constant.getConstant().toString());
        }
    }

    @Override
    public void visit(CaseOp caseOp) {
        this.write("case ");
        caseOp.getConstant().accept(this);
        this.write(": ");
        for(statOp statOp : caseOp.getStatements()){
            statOp.accept(this);
        }
        this.write("break;\n");
    }

    @Override
    public void visit(BodySwitchOp bodySwitchOp) {
        for(CaseOp cond : bodySwitchOp.getCases()){
            cond.accept(this);
        }
    }

    @Override
    public void visit(SwitchOp switchOp) {
        this.write("switch(");
        switchOp.getVariable().accept(this);
        this.write(") {\n");
        switchOp.getBodySwitchOp().accept(this);
        this.write("}\n");
    }

    public void write(String s) {
        try {
            fileC.write(s);
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file: " + e.getMessage());
        }
    }

    private void setPointer(List<varOptInitOp> pointers) {
        for (varOptInitOp op : pointers) {
            changeIdentifier(op.getIdentifier());
            op.getIdentifier().setName("*" + op.getIdentifier().getName());
        }
    }

    private void allocateMemory(List<varOptInitOp> strings) {
        for (int i = 0; i < strings.size(); i++) {
            varOptInitOp op = strings.get(i);
            op.getIdentifier().accept(this);
            if (op.getExpression() != null) {
                this.write(" = (char*) malloc(sizeof(char) * (");
                generateLengthExpression(op.getExpression());
                this.write(" + 1))");
            }else{
                this.write(" = (char*) malloc(sizeof(char) * 1)");
            }
            if (i < strings.size() - 1) {
                this.write(", ");
            }
            op.getIdentifier().setName(op.getIdentifier().getName().replace("*", ""));
        }
        this.write(";\n");
    }

    private void generateLengthExpression(expressionNode expr) {
        if (expr instanceof constantExpression) {
            String value = ((constantExpression) expr).getConstant().toString();
            this.write(String.valueOf(value.length()));
        } else if (expr instanceof identifierExpression) {
            if(expr.getType().equals("string")) {
                this.write("strlen(");
                expr.accept(this); // Scrive l'identificatore
                this.write(")");
            }else if(expr.getType().equals("integer")) {
                this.write("sizeof(int)");
            }else if(expr.getType().equals("double")) {
                this.write("sizeof(double)");
            }
        } else if (expr instanceof binaryExpression) {
            generateLengthExpression(((binaryExpression) expr).getLeftOperand());
            this.write(" + ");
            generateLengthExpression(((binaryExpression) expr).getRightOperand());
        }
    }

    private void concatenateString(binaryExpression binary, String buffer) {
        expressionNode left = binary.getLeftOperand();
        expressionNode right = binary.getRightOperand();
        if(left instanceof binaryExpression){
            concatenateString((binaryExpression) left, buffer);
        }
        if(left instanceof identifierExpression || left instanceof constantExpression){
            checkStringType(buffer, left);
        }if(right instanceof identifierExpression || right instanceof constantExpression){
            checkStringType(buffer, right);
        }
        if(right instanceof binaryExpression){
            concatenateString((binaryExpression) right, buffer);
        }
    }

    private void checkStringType(String buffer, expressionNode right) {
        if(right.getType().equals("integer")){
            this.write("sprintf(bufferIntDouble, \"%d\", ");
            right.accept(this);
            this.write(");\n");
            this.write("strcat("+ buffer + ", bufferIntDouble);\n");
        } else if (right.getType().equals("double")) {
            this.write("sprintf(bufferIntDouble, \"%.2lf\", ");
            right.accept(this);
            this.write(");\n");
            this.write("strcat("+ buffer + ", bufferIntDouble);\n");
        }else {
            this.write("strcat(" + buffer + ", ");
            right.accept(this);
            this.write(");\n");
        }
    }

    private void changeIdentifier(identifierExpression identifier) {
        Symbol function = new Symbol("FUNCTION", identifier.getName());
        Symbol var = new Symbol("VAR", identifier.getName());
        VarEntry variable = (VarEntry) identifier.getScopeTable().lookUpSymbol(var);
        if(identifier.getScopeTable().lookUpSymbol(function) != null){
            identifier.setName(identifier.getName() + "_var");
        }
        if(variable != null && variable.isRef()){
            identifier.setName("*" + identifier.getName());
        }
    }

    private void init_globalString(List<varDeclOp> vars) {
        this.write("""
                void init_globalString(){
                """);
        for (varDeclOp op : vars) {
            String type = op.getVarOptInits().get(0).getIdentifier().getType();
            if(type.equals("string")){
                for(varOptInitOp variable : op.getVarOptInits()){
                    if (variable.getExpression() != null) {
                        variable.getIdentifier().setName(variable.getIdentifier().getName().replace("*", ""));
                        variable.getIdentifier().accept(this);
                        this.write(" = (char*) malloc(sizeof(char) * (");
                        generateLengthExpression(variable.getExpression());
                        this.write(" + 1));\n");
                        initializeString(variable.getIdentifier(), variable.getExpression());
                        this.write(";\n");
                    }else{
                        variable.getIdentifier().setName(variable.getIdentifier().getName().replace("*", ""));
                        variable.getIdentifier().accept(this);
                        this.write(" = (char*) malloc(sizeof(char) * 1000);\n");
                        initializeString(variable.getIdentifier(), variable.getExpression());
                        this.write(";\n");
                    }
                }
            }
        }
        this.write("}\n");
    }

    private void initializeString(identifierExpression id, expressionNode expr) {
        if(expr == null){
            this.write("strcpy(");
            id.accept(this);
            this.write(", \"\\0\")");
            return;
        }
        if(expr instanceof identifierExpression || expr instanceof constantExpression){
            this.write("strcpy(");
            id.accept(this);
            this.write(", ");
            expr.accept(this);
            this.write(")");
        }else{
            expr.accept(this);
            this.write("strcpy(");
            id.accept(this);
            this.write(",tempBuffer);\nfree(tempBuffer)");
        }
    }

    private String typesDecl(String type){
        return switch (type) {
            case "integer" -> "integer";
            case "double" -> "double";
            case "bool" -> "bool";
            case "string" -> "string";
            case "char" -> "char";
            default -> null;
        };
    }

    private boolean prepareFunction(List<expressionNode> exprs){
        boolean flag = false;
        currentBuffer = maxBuffer;
        for(int j = 0; j < exprs.size(); j++){
            expressionNode expr = exprs.get(j);
            if(expr.getType().equals("string") && expr instanceof binaryExpression){
                flag = true;
                this.write("char *buffer"+ maxBuffer + " = (char*) malloc(sizeof(char) * (");
                generateLengthExpression(expr);
                this.write(" + 1));\n");
                this.write("strcpy(buffer"+maxBuffer + ", \"\");\n");
                this.write("tempBuffer = (char*) malloc(sizeof(char) * 1001);\n");
                this.write("strcpy(tempBuffer, \"\");\n");
                concatenateString((binaryExpression) expr, "tempBuffer");
                this.write("strcpy(buffer"+ maxBuffer +", tempBuffer);\nfree(tempBuffer);\n");
                exprs.remove(expr);
                ScopeTable current = ((binaryExpression) expr).getScopeExpression();
                expr = new identifierExpression("buffer"+ maxBuffer, expr.getLine(), expr.getColumn());
                ((identifierExpression) expr).setScopeTable(current);
                expr.setType("string");
                exprs.add(j, expr);
                maxBuffer = maxBuffer + 1;
            }else if(expr instanceof CallOp){
                flag = prepareFunction(((CallOp) expr).getArgs());
            }
        }
        return flag;
    }
    private void discardMemoryFun(){
        for(int j = currentBuffer; j < maxBuffer; j++){
            this.write("free(buffer"+ j + ");\n");
        }
    }
}
