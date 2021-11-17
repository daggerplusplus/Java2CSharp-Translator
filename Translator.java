import java.util.*;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Translator implements ExprS2.Visitor<String>, StmtS2.Visitor<String> {

  String print(ExprS2 expr) {
    return expr.accept(this);
  }

  String print(StmtS2 stmt) {
    return stmt.accept(this);
  }
      private final List<String> validRet = new ArrayList<String>(Arrays.asList("int",  "string", "char", "float", "double", "bool"));



  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitPrintStmt(StmtS2.Print stmt) {
    StringBuilder write = new StringBuilder();
    write.append("Console.Write(" + expand(stmt.expression) + ");");
    return write.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitPrintlnStmt(StmtS2.println stmt) {
    StringBuilder write = new StringBuilder();
    write.append("Console.WriteLine(" + expand(stmt.expression) + ");");
    return write.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitBlockStmt(StmtS2.Block stmt) {
    StringBuilder block = new StringBuilder();

    
    for (StmtS2 statement : stmt.statements) {
      block.append(statement.accept(this));
    }

    return block.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitVarStmt(StmtS2.Var stmt) {
    StringBuilder var = new StringBuilder();

    if (stmt.initializer.equals(null)) {
      var.append(expand2(stmt.name.lexeme) + ";");
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
  public String visitClassStmt(StmtS2.Class stmt) {
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
    for (StmtS2 method : stmt.methods) {
      bodybuilder.append("" + expand2("", method));
    }
    cl.append(bodybuilder.toString().replaceAll("(?m)^", "    "));

    cl.append("\n}\n");
    return cl.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitFunctionStmt(StmtS2.Function stmt) {
    StringBuilder func = new StringBuilder();

    // currently would only work for functions with one param
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
      func.append(");");
      return func.toString();
    }

    func.append(") \n{\n");
        StringBuilder bodybuilder = new StringBuilder();
        for(StmtS2 i : stmt.body) {
            bodybuilder.append(print(i));
        }
        func.append(bodybuilder.toString().replaceAll("(?m)^", "    "));
        func.append("\n}\n");

    return func.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitInterfaceFunctionStmt(StmtS2.InterfaceFunction stmt) {
    StringBuilder func = new StringBuilder();

    func.append(expand2(stmt.name.lexeme) + " (");
    // for (int i = 0; i < stmt.params.size(); i++) {
    //   if (i == stmt.params.size() - 1) {
    //     func.append(stmt.paramstype.get(i).lexeme + " " + stmt.params.get(i).lexeme);
    //     break;
    //   }
    //   func.append(stmt.paramstype.get(i).lexeme + " " + stmt.params.get(i).lexeme + ", ");
    // }
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
  public String visitForStmt(StmtS2.For stmt) {
    System.out.println("in for ~~~~ " + stmt.initializer);
    StringBuilder loop = new StringBuilder();
    loop.append("for (");
    if(stmt.initializer == null) {loop.append(" ; ");} else{loop.append(expandstmt(stmt.initializer));}

    loop.append(" " + expand(stmt.condition) + "; ");
    
    if(stmt.increment == null) { loop.append(" ");} else{loop.append(expand(stmt.increment));}
    
    loop.append( ")\n{\n" + expandstmt(stmt.body).replaceAll("(?m)^", "    ") + "}\n");

    return loop.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitIfStmt(StmtS2.If stmt) {
    StringBuilder cond = new StringBuilder();

    cond.append("if (" + expand2("", stmt.condition) + ")" + "{\n");
        StringBuilder bodybuilder = new StringBuilder();
        for(StmtS2 s: stmt.thenBranch){
            bodybuilder.append(expand2("", s));
        }
        cond.append(bodybuilder.toString().replaceAll("(?m)^", "    "));
        cond.append("}\n");

    return cond.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitElseIfStmt(StmtS2.ElseIf stmt) {
    StringBuilder cond = new StringBuilder();

    cond.append("else if (" + expand2("", stmt.condition) + ")" + "{\n");
        StringBuilder bodybuilder = new StringBuilder();
        for(StmtS2 s: stmt.thenBranch){
            bodybuilder.append(expand2("", s));
        }
        cond.append(bodybuilder.toString().replaceAll("(?m)^", "    "));
        cond.append("}\n");

    return cond.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitElseStmt(StmtS2.Else stmt) {
    StringBuilder cond = new StringBuilder();
    cond.append("else {\n");
        StringBuilder bodybuilder = new StringBuilder();
        for(StmtS2 s: stmt.thenBranch){
            bodybuilder.append(expand2("", s));
        }
        cond.append(bodybuilder.toString().replaceAll("(?m)^", "    "));
        cond.append("}\n");

    return cond.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitBreakStmt(StmtS2.Break stmt) {
    StringBuilder brk = new StringBuilder();
    brk.append(stmt.keyword.lexeme + ";");
    return brk.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitSuperExpr(ExprS2.Super expr) {
    return expand2("", expr.method);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitThisExpr(ExprS2.This expr) {
    return expand2("", expr.keyword.lexeme);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitNewExpr(ExprS2.New expr) {
    return expand2("", expr.keyword.lexeme);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitMainExpr(ExprS2.Main expr) {
    return expand2("", "Main");
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitTypesExpr(ExprS2.Types expr) {
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
  public String visitModifiersExpr(ExprS2.Modifiers expr) {

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
  public String visitWhileStmt(StmtS2.While stmt) {
    StringBuilder loop = new StringBuilder();
    loop.append("while (" + expand2("", stmt.condition) + ")\n{\n " + expandstmt(stmt.body).replaceAll("(?m)^", "    ") + "\n}\n");
    return loop.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitDoStmt(StmtS2.Do stmt) {
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
  public String visitWhileDo(StmtS2.WhileDo stmt) {
    StringBuilder wd = new StringBuilder();
    wd.append("while (" + expand2("", stmt.condition) + ");\n");
    return wd.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitUnaryExpr(ExprS2.Unary expr) {
    return expand2("", expr.operator.lexeme, expr.right);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitUnary2Expr(ExprS2.Unary2 expr) {
    return expand2("", expr.left, expr.operator.lexeme);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitUnary3Expr(ExprS2.Unary3 expr) {
    return expand2("", expr.left, expr.operator.lexeme, ";", expr.ln);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  // public String visitInterfaceStmt(StmtS2.Interface stmt) {
  // StringBuilder intf = new StringBuilder();
  // intf.append(expand2("", "interface ", stmt.name));
  // if(!stmt.extender.isEmpty()){
  // intf.append(": ");
  // for(int i = 0; i < stmt.extender.size(); i++){
  // if(i == stmt.extender.size()-1){
  // intf.append(expand2("", stmt.extender.get(i), " "));
  // break;
  // }
  // intf.append(expand2("", stmt.extender.get(i), ", "));
  // }
  // }
  // intf.append("\n{\n");
  // for (StmtS2 method : stmt.methods) {
  // intf.append("" + expand2("", method));
  // }
  // intf.append("\n}\n");
  // return intf.toString();
  // }
  public String visitInterfaceStmt(StmtS2.Interface stmt) {
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

    /*
     * for (StmtS2 mod:stmt.modifiers) { itf.append(print(mod)); } for (StmtS2
     * method : stmt.methods) { itf.append("\n" + print(method)); }
     */
    itf.append("\n}\n");
    return itf.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitReturnStmt(StmtS2.Return stmt) {
    StringBuilder rtn = new StringBuilder();
    rtn.append(expand2(stmt.keyword.lexeme) + " " + expand(stmt.value) + ";");
    return rtn.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitEnumStmt(StmtS2.Enum stmt) {
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
  public String visitVariableExpr(ExprS2.Variable expr) {
    StringBuilder variable = new StringBuilder();
    variable.append(expand2("", expr.name.lexeme, " "));
    return variable.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitContinueStmt(StmtS2.Continue stmt) {
    StringBuilder c = new StringBuilder();
    c.append("\n");
    c.append(expand2("", stmt.keyword.lexeme));
    c.append(";");

    return c.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitGetExpr(ExprS2.Get expr) {
    StringBuilder get = new StringBuilder();
    get.append(expand2("", expr.object, ".", expr.name.lexeme));
    return get.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitCallExpr(ExprS2.Call expr) {
    // callee, paren, arguments
    // ExprS2 callee, TokenS2 paren, List<ExprS2> arguments
    StringBuilder cll = new StringBuilder();
    cll.append(expand2(" ", expr.callee, "("));
    for (int i = 0; i < expr.arguments.size(); i++) {
      if (i == expr.arguments.size() - 1) {
        cll.append(expand2("", expandstmt(expr.arguments.get(i))));
        break;
      }
      cll.append(expand2("", expandstmt(expr.arguments.get(i)), ", "));
    }
    cll.append(")");
    return cll.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitThrowStmt(StmtS2.Throw stmt) {
    // StringBuilder throwstmt = new StringBuilder();
    // throwstmt.appen("\n");
    // throwstmt.append(expand2("",stmt.keyword.lexeme));
    // return throwstmt.toString();
    return null;
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitSwitchStmt(StmtS2.Switch stmt) {
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
  public String visitSetExpr(ExprS2.Set expr) {
    return expand2("", expr.object, expr.name.lexeme, expr.value);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitBinaryExpr(ExprS2.Binary expr) {
    StringBuilder bin = new StringBuilder();
    bin.append(expand2("", expand2("", expr.left), " ", expr.operator.lexeme, " ", expand2("", expr.right)));
    return bin.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitGroupingExpr(ExprS2.Grouping expr) {
    StringBuilder group = new StringBuilder();
    group.append("(");
    if (expr.expression.equals(null)) {
      group.append("");
    } else {
      for (int i = 0; i < expr.expression.size(); i++) {
        if (i == expr.expression.size() - 1) {
          group.append(expand2("", expr.expression.get(i)));
        } else {
          group.append(expand2("", expr.expression.get(i), ", "));
        }
      }
    }
    group.append(")");
    return group.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitGrouping2Expr(ExprS2.Grouping2 expr) {
    StringBuilder group = new StringBuilder();
    group.append("() ");
    return group.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitArrayGroupingExpr(ExprS2.ArrayGrouping expr) {
    StringBuilder group = new StringBuilder();
    group.append("[");
    for (int i = 0; i < expr.expression.size(); i++) {
      if (i == expr.expression.size() - 1) {
        group.append(expand2("", expr.expression.get(i)));
      } else {
        group.append(expand2("", expr.expression.get(i), ", "));
      }
    }
    // group.append(expand(expr.expression));
    group.append("]");
    return group.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitArrayGrouping2Expr(ExprS2.ArrayGrouping2 expr) {
    StringBuilder group = new StringBuilder();
    group.append("[] ");
    return group.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitArray2GroupingExpr(ExprS2.Array2Grouping expr) {
    System.out.println("in Translator ");
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
    System.out.println("in Translator " + group);
    return group.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitArray2Grouping2Expr(ExprS2.Array2Grouping2 expr) {
    StringBuilder group = new StringBuilder();
    group.append("{} ");
    return group.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitAssignExpr(ExprS2.Assign expr) {
    StringBuilder assign = new StringBuilder();
    assign.append(expand2("", expr.name, " = ", expr.value, ";\n"));
    return assign.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitExpressionStmt(StmtS2.Expression stmt) {
    StringBuilder expr = new StringBuilder();
    expr.append(expand2("", stmt.expression));
    return expr.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitTryStmt(StmtS2.Try stmt) {
    StringBuilder trystmt = new StringBuilder();
    trystmt.append(stmt.keyword.lexeme);
    trystmt.append(" {\n");
    trystmt.append(expand2("", stmt.body).replaceAll("(?m)^", "    "));
    trystmt.append("\n}\n");
    return trystmt.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitCatchStmt(StmtS2.Catch stmt) {
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
  public String visitFinallyStmt(StmtS2.Finally stmt) {
    StringBuilder finstmt = new StringBuilder();
    finstmt.append(stmt.keyword.lexeme);
    finstmt.append(" {\n");
    finstmt.append(expand2("", stmt.body).replaceAll("(?m)^", "    "));
    finstmt.append("\n}\n");
    return finstmt.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitThrowExpr(ExprS2.Throw expr) {
    return expand2("", "throw new ", expr.exp.lexeme, " ", expr.grp, ";");
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitExceptionExpr(ExprS2.Exception expr) {
    return expand2("", expr.keyword.lexeme);
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitImportExpr(ExprS2.Import expr) {
    return null;
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitDotExpr(ExprS2.Dot expr) {
    return null;
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitLogicalExpr(ExprS2.Logical expr) {
    StringBuilder log = new StringBuilder();
    log.append(expand2("", expr.left, " ", expr.operator.lexeme, " ", expr.right));
    return log.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitPackageExpr(ExprS2.Package expr) {
    return null;
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitLiteralExpr(ExprS2.Literal expr) {
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

  private String expand(ExprS2... exprs) {
    StringBuilder builder = new StringBuilder();
    for (ExprS2 expr : exprs) {
      // builder.append(" ");
      builder.append(expr.accept(this));
    }
    return builder.toString();
  }

  private String expand2(String name, ExprS2... exprs) {
    StringBuilder builder = new StringBuilder();

    builder.append(name);
    for (ExprS2 expr : exprs) {
      // builder.append(" ");
      builder.append(expr.accept(this));
    }
    // builder.append(")");

    return builder.toString();
  }

  private String expandstmt(StmtS2... stmts) {
    StringBuilder builder = new StringBuilder();
    for (StmtS2 stmt : stmts) {
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
      if (part instanceof ExprS2) {
        builder.append(((ExprS2) part).accept(this));

      } else if (part instanceof StmtS2) {
        builder.append(((StmtS2) part).accept(this));

      } else if (part instanceof TokenS2) {
        builder.append(((TokenS2) part).lexeme);
      } else if (part instanceof List) {
        transform(builder, ((List) part).toArray());
      } else {
        builder.append(part);
      }
    }
  }

} // end class
