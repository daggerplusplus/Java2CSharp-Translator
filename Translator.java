import java.lang.String;
import java.util.List;

class Translator implements Expr.Visitor<String>, Stmt.Visitor<String> {

  String print(Expr expr) {
    return expr.accept(this);
  }

  String print(Stmt stmt) {
    return stmt.accept(this);
  }
  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitPrintStmt(Stmt.Print stmt) {
    StringBuilder write = new StringBuilder();
    write.append("Console.Write(" + expand(stmt.expression) + ");");
    return write.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitPrintlnStmt(Stmt.println stmt) {
    StringBuilder write = new StringBuilder();
    write.append("Console.WriteLine(" + expand(stmt.expression) + ");");
    return write.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitBlockStmt(Stmt.Block stmt) {
    StringBuilder block = new StringBuilder();

    
    for (Stmt statement : stmt.statements) {
      block.append(statement.accept(this));
    }

    return block.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitVarStmt(Stmt.Var stmt) {
    StringBuilder var = new StringBuilder();

    if (stmt.initializer.isEmpty()) {
      var.append(expand2(stmt.name.lexeme));
      var.append(";");
      return var.toString();
    }
    var.append(stmt.name.lexeme);
    for (int i = 0; i < stmt.initializer.size(); i++) {
      if (i == 0) {
        var.append(" = ");
      }
      if (i == stmt.initializer.size() - 1) {
        var.append(expand2("", stmt.initializer.get(i)));
        break;
      }
      var.append(expand2("", stmt.initializer.get(i), " "));
    }
    // var.append(expand(stmt.initializer));
    var.append(";");

    return var.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitClassStmt(Stmt.Class stmt) {
    StringBuilder cl = new StringBuilder();

    cl.append("class " + expand2(stmt.name.lexeme)); // class 'class name'

    // extends this.superclass = superclass;
    if (!stmt.superclass.isEmpty()) {
      cl.append(": ");
      for (int i = 0; i < stmt.superclass.size(); i++) {
        if (i == stmt.superclass.size() - 1) {
          cl.append(expand2("", stmt.superclass.get(i), " "));
          break;
        }
        cl.append(expand2("", stmt.superclass.get(i), ", "));
      }
    }

    // implements
    if (!stmt.implementinterface.isEmpty()) {
      for (int i = 0; i < stmt.implementinterface.size(); i++) {
        if (!stmt.superclass.isEmpty() && i == 0) {
          cl.append(expand2(", ", stmt.implementinterface.get(i)));
          break;
        } else if (stmt.superclass.isEmpty() && i == 0) {
          cl.append(expand2(": ", stmt.implementinterface.get(i)));
          break;
        }
        if (i == stmt.implementinterface.size() - 1) {
          cl.append(expand2("", stmt.implementinterface.get(i), " "));
          break;
        }
        cl.append(expand2(", ", stmt.implementinterface.get(i)));
      }
    }

    cl.append(" {\n");
    StringBuilder bodybuilder = new StringBuilder();
    for (Stmt method : stmt.methods) {
      bodybuilder.append("" + expand2("", method));
    }
    cl.append(bodybuilder.toString().replaceAll("(?m)^", "    "));

    cl.append("\n}\n");
    return cl.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitFunctionStmt(Stmt.Function stmt) {
    StringBuilder func = new StringBuilder();
    
    if (expand2(stmt.name.lexeme).equals("main")) {
      func.append("Main (");
    } else {
      func.append(expand2("", stmt.name.lexeme, " ("));
    }

    for (int i = 0; i < stmt.paramtid.size(); i++) {
      String t = "";
      String a = "";
      if (!stmt.paramtyp.get(i).equals(stmt.paramtid.get(i))) {
        t = t + expand2("", stmt.paramtyp.get(i));
      }
      if (!stmt.paramary.get(i).equals(stmt.paramtid.get(i))) {
        a = a + expandstmt(stmt.paramary.get(i)) + "";
      }
      if (i == stmt.paramtid.size() - 1) {
        func.append(expand2("", t, a, " ", expandstmt(stmt.paramtid.get(i))));
        break;
      }
      func.append(expand2("", t, a, " ", stmt.paramtid.get(i), ", "));
    }
    if (!stmt.body.isEmpty() && stmt.body.get(0) == null) {
      func.append(");\n");
      return func.toString();
    }

    func.append(") \n{\n");
        StringBuilder bodybuilder = new StringBuilder();
        for(Stmt i : stmt.body) {
            bodybuilder.append(print(i));
        }
        func.append(bodybuilder.toString().replaceAll("(?m)^", "    "));
        func.append("\n}\n");

    return func.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitInterfaceFunctionStmt(Stmt.InterfaceFunction stmt) {
    StringBuilder func = new StringBuilder();

    func.append(expand2(stmt.name.lexeme) + " (");

    for (int i = 0; i < stmt.paramtid.size(); i++) {
      String t = "";
      String a = "";
      if (!stmt.paramtyp.get(i).equals(stmt.paramtid.get(i))) {
        t = t + expand2("", stmt.paramtyp.get(i));
      }
      if (!stmt.paramary.get(i).equals(stmt.paramtid.get(i))) {
        a = a + expand2("", stmt.paramary.get(i)) + "";
      }
      if (i == stmt.paramtid.size() - 1) {
        func.append(expand2("", t, a, " ", expandstmt(stmt.paramtid.get(i))));
        break;
      }
      func.append(expand2("", t, a, " ", stmt.paramtid.get(i), ", "));
    }

    func.append(");");

    return func.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitForStmt(Stmt.For stmt) {
    StringBuilder loop = new StringBuilder();
    loop.append("for (");
    
    if(stmt.initializer == null) {loop.append(" ; ");} else{loop.append(stmt.type.lexeme + " " + expandstmt(stmt.initializer));}

    loop.append(" " + expand(stmt.condition) + "; ");
    
    if(stmt.increment == null) { loop.append(" ");} else{loop.append(expand(stmt.increment));}
    
    loop.append( ")\n{\n" + expandstmt(stmt.body).replaceAll("(?m)^", "    ") + "}\n");

    return loop.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitIfStmt(Stmt.If stmt) {
    StringBuilder cond = new StringBuilder();

    cond.append("if (" + expand2("", stmt.condition) + ")" + "{\n");
        StringBuilder bodybuilder = new StringBuilder();
        for(Stmt s: stmt.thenBranch){
            bodybuilder.append(expand2("", s));
        }
        cond.append(bodybuilder.toString().replaceAll("(?m)^", "    "));
        cond.append("}\n");

    return cond.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitElseIfStmt(Stmt.ElseIf stmt) {
    StringBuilder cond = new StringBuilder();

    cond.append("else if (" + expand2("", stmt.condition) + ")" + "{\n");
        StringBuilder bodybuilder = new StringBuilder();
        for(Stmt s: stmt.thenBranch){
            bodybuilder.append(expand2("", s));
        }
        cond.append(bodybuilder.toString().replaceAll("(?m)^", "    "));
        cond.append("}\n");

    return cond.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitElseStmt(Stmt.Else stmt) {
    StringBuilder cond = new StringBuilder();
    cond.append("else {\n");
        StringBuilder bodybuilder = new StringBuilder();
        for(Stmt s: stmt.thenBranch){
            bodybuilder.append(expand2("", s));
        }
        cond.append(bodybuilder.toString().replaceAll("(?m)^", "    "));
        cond.append("}\n");

    return cond.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitBreakStmt(Stmt.Break stmt) {
    StringBuilder brk = new StringBuilder();
    brk.append(stmt.keyword.lexeme + ";");
    return brk.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitGetStmt(Stmt.Get stmt) {
    StringBuilder brk = new StringBuilder();
    brk.append(expand2("", stmt.expr, ";"));
    return brk.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitSuperExpr(Expr.Super expr) {
    return expand2("", expr.method);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitThisExpr(Expr.This expr) {
    return expand2("", expr.keyword.lexeme);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitNewExpr(Expr.New expr) {
    return expand2("", expr.keyword.lexeme);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitMainExpr(Expr.Main expr) {
    return expand2("", "Main");
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitTypesExpr(Expr.Types expr) {
    StringBuilder types = new StringBuilder();
    if (expr.keyword.lexeme.equals("String"))
      types.append("string ");
    if (expr.keyword.lexeme.equals("void"))
      types.append("void ");
    if (expr.keyword.lexeme.equals("int"))
      types.append("int ");
    if (expr.keyword.lexeme.equals("double"))
      types.append("double ");
    if (expr.keyword.lexeme.equals("float"))
      types.append("float ");
    if (expr.keyword.lexeme.equals("char"))
      types.append("char ");
    if (expr.keyword.lexeme.equals("boolean"))
      types.append("bool ");
    return types.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitModifiersExpr(Expr.Modifiers expr) {

    StringBuilder mods = new StringBuilder();
    if (expr.keyword.lexeme.equals("public"))
      mods.append("public ");
    if (expr.keyword.lexeme.equals("private"))
      mods.append("private ");
    if (expr.keyword.lexeme.equals("static"))
      mods.append("static ");
    if (expr.keyword.lexeme.equals("protected"))
      mods.append("protected ");
    if (expr.keyword.lexeme.equals("final"))
      mods.append("const ");
    // if (expr.keyword.lexeme.equals("static"))
    //   mods.append("static ");
    if (expr.keyword.lexeme.equals("abstract"))
      mods.append("abstract ");
    return mods.toString();
    // if (expr.lexeme.equals("String"))
    // mods.append("string");
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitWhileStmt(Stmt.While stmt) {
    StringBuilder loop = new StringBuilder();
    loop.append("while (" + expand2("", stmt.condition) + ")\n{\n " + expandstmt(stmt.body).replaceAll("(?m)^", "    ") + "\n}\n");
    return loop.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitDoStmt(Stmt.Do stmt) {
    StringBuilder loop = new StringBuilder();
    loop.append("do {\n");
    StringBuilder bodybuilder = new StringBuilder();
    for (int i = 0; i < stmt.body.size(); i++) {
      bodybuilder.append(expand2("\n", stmt.body.get(i)));
    }
    loop.append(bodybuilder.toString().replaceAll("(?m)^", "    "));
    loop.append("\n}");
    return loop.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitWhileDo(Stmt.WhileDo stmt) {
    StringBuilder wd = new StringBuilder();
    wd.append("while (" + expand2("", stmt.condition) + ");\n");
    return wd.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitUnaryExpr(Expr.Unary expr) {
    return expand2("", expr.operator.lexeme, expr.right);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitUnary2Expr(Expr.Unary2 expr) {
    return expand2("", expr.left, expr.operator.lexeme);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitUnary3Expr(Expr.Unary3 expr) {
    return expand2("", expr.left, expr.operator.lexeme, ";", expr.ln);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitInterfaceStmt(Stmt.Interface stmt) {
    StringBuilder itf = new StringBuilder();
    itf.append("interface " + stmt.name.lexeme);

    if (!stmt.extender.isEmpty()) {
      itf.append(": ");
      for (int i = 0; i < stmt.extender.size(); i++) {
        if (i == stmt.extender.size() - 1) {
          itf.append(expand2("", stmt.extender.get(i), " "));
          break;
        }
        itf.append(expand2("", stmt.extender.get(i), ", "));
      }
    }

    itf.append(" {\n");
    StringBuilder bodybuilder = new StringBuilder();
    for (int i = 0; i < stmt.methods.size(); i++) {
      bodybuilder.append((print(stmt.mods.get(i)) + print(stmt.methods.get(i))) );
    }
    itf.append(bodybuilder.toString().replaceAll("(?m)^", "    "));

    itf.append("\n}\n");
    return itf.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitReturnStmt(Stmt.Return stmt) {
    StringBuilder rtn = new StringBuilder();
    rtn.append(expand2(stmt.keyword.lexeme) + " " + expand(stmt.value) + ";");
    return rtn.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitEnumStmt(Stmt.Enum stmt) {
    StringBuilder enums = new StringBuilder();
    enums.append("enum ");
    enums.append(stmt.keyword.lexeme);
    enums.append(" {\n");
    StringBuilder bodybuilder = new StringBuilder();
    for (int i = 0; i < stmt.body.size(); i++) {
      if (i == stmt.body.size() - 1) {
        bodybuilder.append((stmt.body.get(i).lexeme) + "\n");
        break;
      }
      bodybuilder.append(stmt.body.get(i).lexeme + ",\n");
    }
    enums.append(bodybuilder.toString().replaceAll("(?m)^", "    "));

    enums.append("}\n");
    return enums.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitVariableExpr(Expr.Variable expr) {
    StringBuilder variable = new StringBuilder();
    variable.append(expand2("", expr.name.lexeme, " "));
    return variable.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitContinueStmt(Stmt.Continue stmt) {
    StringBuilder c = new StringBuilder();
    c.append("\n");
    c.append(expand2("", stmt.keyword.lexeme));
    c.append(";");

    return c.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitGetExpr(Expr.Get expr) {
    StringBuilder get = new StringBuilder();
    get.append(expand2("", expr.object, ".", expr.name).replaceAll("\\s+",""));  
    return get.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  // @Override
  // public String visitGet2Expr(Expr.Get2 expr) {
  //   StringBuilder get = new StringBuilder();
  //   get.append(expand2("", expr.object, ".", expr.name).replaceAll("\\s+","")); 
  //   get.append(";\n");
  //   return get.toString();
  // }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitCallExpr(Expr.Call expr) {    
    StringBuilder cll = new StringBuilder();
    cll.append(expand2(" ", expr.callee, "("));
    for (int i = 0; i < expr.arguments.size(); i++) {
      if (i == expr.arguments.size() - 1) {
        cll.append(expand2("", expr.arguments.get(i)));
        break;
      }
      cll.append(expand2("", expr.arguments.get(i), ","));
    }
    cll.append(")");
    return cll.toString();
  }
  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitSwitchStmt(Stmt.Switch stmt) {
    StringBuilder swt = new StringBuilder();
    swt.append("switch (" + expand(stmt.condition) + ") {\n");
    StringBuilder bodybuilder = new StringBuilder();
    for (int i = 0; i < stmt.caseBranch.size(); i++) {
      bodybuilder.append("case " + expand(stmt.caseVal.get(i)) + ": " + expandstmt(stmt.caseBranch.get(i)) + "\n");
      bodybuilder.append("break;\n");
    }
    if (stmt.defaultBranch != null) {
      bodybuilder.append("default: " + expandstmt(stmt.defaultBranch) + "\n");
      bodybuilder.append("break;\n");
    }
    swt.append(bodybuilder.toString().replaceAll("(?m)^", "    "));
    swt.append("}\n");
    return swt.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitSetExpr(Expr.Set expr) {
    return expand2("", expr.object, expr.name, expr.equals.lexeme, expr.value);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitBinaryExpr(Expr.Binary expr) {
    StringBuilder bin = new StringBuilder();
    bin.append(expand2("", expand2("", expr.left), " ", expr.operator.lexeme, " ", expand2("", expr.right)));
    return bin.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitGroupingExpr(Expr.Grouping expr) {
    StringBuilder group = new StringBuilder();
    group.append("(");
    if (expr.expression.equals(null)) {
      group.append("");
    } else {
      for (int i = 0; i < expr.expression.size(); i++) {
        if (i == expr.expression.size() - 1) {
          group.append(expand2("", expr.expression.get(i)));
        } else {
          group.append(expand2("", expr.expression.get(i), ","));
        }
      }
    }
    group.append(")");
    return group.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitGrouping2Expr(Expr.Grouping2 expr) {
    StringBuilder group = new StringBuilder();
    group.append("() ");
    return group.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String visitArrayGroupingExpr(Expr.ArrayGrouping expr) {
        StringBuilder group = new StringBuilder();
        group.append("[");
        if(expr.dimension == 1){
            if(expr.expression.isEmpty()){
                group.append(" ");
            }
            else{
                for (int i = 0; i < expr.expression.size(); i++) {
                    if(i == expr.expression.size()-1)
                    {
                        group.append(expand2("", expr.expression.get(i)));
                    }
                    else
                    {
                        group.append(expand2("", expr.expression.get(i), ", "));
                    }
                }
            }
        }
        else{
            for (int i = 0; i < expr.dimension; i++) {
                try{
                    if(expr.expression.get(i) != null){
                        group.append(expand2("", expr.expression.get(i)));
                    }
                }
                catch(Exception e) {

                }

                if(i != expr.dimension-1){
                    group.append(", ");
                }

            }


        }

//        group.append(expand(expr.expression));
        group.append("] ");
        return group.toString();
    }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitArrayGrouping2Expr(Expr.ArrayGrouping2 expr) {
    StringBuilder group = new StringBuilder();
    group.append("[] ");
    return group.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitArray2GroupingExpr(Expr.Array2Grouping expr) {
    StringBuilder group = new StringBuilder();
    group.append("{");
    for (int i = 0; i < expr.expression.size(); i++) {
      if (i == expr.expression.size() - 1) {
        group.append(expand2("", expr.expression.get(i)));
      } else {
        group.append(expand2("", expr.expression.get(i), ", "));
      }
    }
    // group.append(expand(expr.expression));
    group.append("}");
    return group.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitArray2Grouping2Expr(Expr.Array2Grouping2 expr) {
    StringBuilder group = new StringBuilder();
    group.append("{} ");
    return group.toString();
  }

  

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitAssignExpr(Expr.Assign expr) {
    StringBuilder assign = new StringBuilder();    
    assign.append(expand2("", expr.name, expr.equals.lexeme, expr.value, ";\n"));
    return assign.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String visitAssignArrayExpr(Expr.AssignArray expr) {
        StringBuilder assign = new StringBuilder();
        assign.append(expand2("", expr.arr , expr.equals.lexeme , expr.value, ";\n"));
        return assign.toString();
    }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitExpressionStmt(Stmt.Expression stmt) {
    StringBuilder expr = new StringBuilder();
    expr.append(expand2("", stmt.expression));
    return expr.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitTryStmt(Stmt.Try stmt) {
    StringBuilder trystmt = new StringBuilder();
    trystmt.append(stmt.keyword.lexeme);
    trystmt.append(" {\n");
    trystmt.append(expand2("", stmt.body).replaceAll("(?m)^", "    "));
    trystmt.append("\n}\n");
    return trystmt.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitCatchStmt(Stmt.Catch stmt) {
    StringBuilder catchstmt = new StringBuilder();
    catchstmt.append(stmt.keyword.lexeme);
    catchstmt.append(" (");
    for (int i = 0; i < stmt.params.size(); i++) {
      if (i == stmt.params.size() - 1) {
        catchstmt.append(stmt.paramstype.get(i).lexeme + " " + stmt.params.get(i).lexeme);
        break;
      }
      catchstmt.append(stmt.paramstype.get(i).lexeme + " " + stmt.params.get(i).lexeme + ", ");
    }
    catchstmt.append(") {\n");
    catchstmt.append(expand2("", stmt.body).replaceAll("(?m)^", "    "));
    catchstmt.append("\n}\n");
    return catchstmt.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitFinallyStmt(Stmt.Finally stmt) {
    StringBuilder finstmt = new StringBuilder();
    finstmt.append(stmt.keyword.lexeme);
    finstmt.append(" {\n");
    finstmt.append(expand2("", stmt.body).replaceAll("(?m)^", "    "));
    finstmt.append("\n}\n");
    return finstmt.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitThrowExpr(Expr.Throw expr) {
    return expand2("", "throw new ", expr.exp.lexeme, " ", expr.grp, ";");
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitExceptionExpr(Expr.Exception expr) {
    return expand2("", expr.keyword.lexeme);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitImportExpr(Expr.Import expr) {
    return null;
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitDotExpr(Expr.Dot expr) {
    
    return null;
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitLogicalExpr(Expr.Logical expr) {
    StringBuilder log = new StringBuilder();
    log.append(expand2("", expr.left, " ", expr.operator.lexeme, " ", expr.right));
    return log.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitPackageExpr(Expr.Package expr) {
    return null;
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitLiteralExpr(Expr.Literal expr) {
    StringBuilder literal = new StringBuilder();
    if (expr.value == null)
      literal.append("null");
    else
      literal.append(expr.value);
    return literal.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitNewLineExpr() {
    return "\n";
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////

  private String expand(Expr... exprs) {
    StringBuilder builder = new StringBuilder();
    for (Expr expr : exprs) {
      // builder.append(" ");
      builder.append(expr.accept(this));
    }
    return builder.toString();
  }

  private String expand2(String name, Expr... exprs) {
    StringBuilder builder = new StringBuilder();

    builder.append(name);
    for (Expr expr : exprs) {
      // builder.append(" ");
      builder.append(expr.accept(this));
    }
    // builder.append(")");

    return builder.toString();
  }

  private String expandstmt(Stmt... stmts) {
    StringBuilder builder = new StringBuilder();
    for (Stmt stmt : stmts) {
      // builder.append(" ");
      builder.append(stmt.accept(this));
    }
    return builder.toString();
  }

  private String expand2(String name, Object... parts) {
    StringBuilder builder = new StringBuilder();

    transform(builder, parts);

    return builder.toString();
  }

  private void transform(StringBuilder builder, Object... parts) {
    for (Object part : parts) {
      // builder.append(" ");
      if (part instanceof Expr) {
        builder.append(((Expr) part).accept(this));

      } else if (part instanceof Stmt) {
        builder.append(((Stmt) part).accept(this));

      } else if (part instanceof Token) {
        builder.append(((Token) part).lexeme);
      } else if (part instanceof List) {
        transform(builder, ((List) part).toArray());
      } else {
        builder.append(part);
      }
    }
  }

} // end class
