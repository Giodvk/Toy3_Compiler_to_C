package classNode.Expression;

import java_cup.runtime.SyntaxTreeDFS;
import visitors.visitor;

public abstract class expressionNode{
   protected String type;
   protected int line;
   protected int column;

   public abstract void accept(visitor v);

   public abstract String getType();

   public abstract void setType(String type);

   public abstract int getLine();

   public abstract int getColumn();

}