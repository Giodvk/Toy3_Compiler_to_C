package esercitazione4;

import java_cup.runtime.Symbol;
import java.lang.String;
%%

%line
%column
%public
%class LexerAnalyzer
%unicode
%cup
%{
    StringBuilder string = new StringBuilder();

%}
LineTerminator = \r|\n|\r\n
WhiteSpace = {LineTerminator} | [ \t\f]

Comment = {TraditionalComment} | {EndOfLineComment}
TraditionalComment =  [^*] ~"*/" | "/*" "*"+ "/"
InputCharacter = [^\r\n]

EndOfLineComment =  {InputCharacter}* {LineTerminator}?
identifier = [:jletter:] [:jletterdigit:]*
int = 0 | [1-9][0-9]*("E"("+" | "-")[0-9]+)?
double =  0"."(0 | [1-9])+("E"("+" | "-")[0-9]+)? | [1-9][0-9]*"."(0 | [1-9])+("E"("+" | "-")[0-9]+)?
charAscii = [a-zA-Z0-9] | [(){}!\"#\$%&<=>\?@] | [+\-*\/_] | [,\.;]
charCostant = "'" {charAscii} "'"



%state STRING
%state COMMENT


%%

<YYINITIAL>{
    // Parole chiavi del linguaggio
    "program" {return new Symbol(sym.PROGRAM, yyline, yycolumn);}
    "begin" {return new Symbol(sym.BEGIN, yyline, yycolumn);}
    "end" {return new Symbol(sym.END, yyline, yycolumn);}
    "int" {return new Symbol(sym.INT, yyline, yycolumn);}
    "double" {return new Symbol(sym.DOUBLE, yyline, yycolumn);}
    "bool" {return new Symbol(sym.BOOL, yyline, yycolumn);}
    "string" {return new Symbol(sym.STRING, yyline, yycolumn);}
    "char" {return new Symbol(sym.CHAR, yyline, yycolumn);}
    "return" {return new Symbol(sym.RETURN, yyline,yycolumn);}
    "if" {return new Symbol(sym.IF, yyline, yycolumn);}
    "then" {return new Symbol(sym.THEN, yyline, yycolumn);}
    "while" {return new Symbol(sym.WHILE, yyline, yycolumn);}
    "do" {return new Symbol(sym.DO, yyline, yycolumn);}
    "else" {return new Symbol(sym.ELSE, yyline, yycolumn);}
    "ref" {return new Symbol(sym.REF, yyline, yycolumn);}
    "def" {return new Symbol(sym.DEF, yyline, yycolumn);}
    "<<" {return new Symbol(sym.IN, yyline, yycolumn);}
    ">>" {return new Symbol(sym.OUT, yyline, yycolumn);}
    "!>>" {return new Symbol(sym.OUTNL, yyline, yycolumn);}
    "|" {return new Symbol(sym.PIPE, yyline, yycolumn);}

    //Operatori relazionali e logici
    ">" {return new Symbol(sym.GT, yyline, yycolumn);}
    ">=" {return new Symbol(sym.GE, yyline, yycolumn);}
    "<" {return new Symbol(sym.LT, yyline, yycolumn);}
    "<=" {return new Symbol(sym.LE, yyline, yycolumn);}
    "==" {return new Symbol(sym.EQ, yyline, yycolumn);}
    "<>" {return new Symbol(sym.NE, yyline, yycolumn);}
    "not" {return new Symbol(sym.NOT, yyline, yycolumn);}
    "and" {return new Symbol(sym.AND, yyline, yycolumn);}
    "or" {return new Symbol(sym.OR, yyline, yycolumn);}

    //Stringhe costanti
        "\"" {string.setLength(0); yybegin(STRING);}

    {WhiteSpace} {/* ignore */}


    //Commenti
    "/*" | "//" {yybegin(COMMENT);}


    //Operatori
    "+" {return new Symbol(sym.PLUS, yyline, yycolumn);}
    "-" {return new Symbol(sym.MINUS, yyline, yycolumn);}
    "*" {return new Symbol(sym.TIMES, yyline, yycolumn);}
    "/" {return new Symbol(sym.DIV, yyline, yycolumn);}
    ":=" {return new Symbol(sym.ASSIGN, yyline, yycolumn);}
    "=" {return new Symbol(sym.ASSIGNDECL, yyline, yycolumn);}

    //Costanti dei numeri
    {int} {return new Symbol(sym.INT_CONST, yyline, yycolumn, Integer.parseInt(yytext()));}
    {double} {return new Symbol(sym.DOUBLE_CONST, yyline, yycolumn, Double.parseDouble(yytext()));}

     //Costanti dei char
    {charCostant} {return new Symbol(sym.CHAR_CONST, yyline, yycolumn, yytext());}

    //Costanti booleani
    "true" {return new Symbol(sym.TRUE, yyline, yycolumn,Boolean.parseBoolean(yytext()));}
    "false" {return new Symbol(sym.FALSE, yyline, yycolumn, Boolean.parseBoolean(yytext()));}


    //Punteggiature e parentesi
    "(" {return new Symbol(sym.LPAR, yyline, yycolumn);}
    ")" {return new Symbol(sym.RPAR, yyline, yycolumn);}
    "{" {return new Symbol(sym.LBRAC, yyline, yycolumn);}
    "}" {return new Symbol(sym.RBRAC, yyline, yycolumn);}
    ";" {return new Symbol(sym.SEMI, yyline, yycolumn);}
    ":" {return new Symbol(sym.COLON, yyline, yycolumn);}
    "," {return new Symbol(sym.COMMA, yyline, yycolumn);}
     "eof" {return new Symbol(sym.EOF, yyline, yycolumn);}


     //Identificatori
     {identifier} {return new Symbol(sym.ID, yyline, yycolumn ,yytext());}

    . {System.err.println("Errore, token non riconosciuto "+ yytext() + ", linea: "+yyline+", colonna: "+yycolumn);}
}


<STRING> {

\"   { yybegin(YYINITIAL); return new Symbol(sym.STRING_CONST,yyline, yycolumn, string.toString()); }
[^\n\r\"\\]+ { string.append( yytext() ); }
\\t { string.append("\t"); }
\\n { string.append("\n"); }
\\r { string.append("\r"); }
\\\" { string.append("\""); }
\\ { string.append("\\"); }

"eof" {System.err.println("Stringa costante non completata, riga: "+yyline+", colonna: "+yycolumn);}

}

<COMMENT> {
{Comment} {/*ignore*/}

"eof" {System.err.println("Commento non chiuso, riga: "+yyline+", colonna: "+yycolumn);}
}