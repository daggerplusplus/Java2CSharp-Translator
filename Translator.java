import java.util.List;

class Translator implements ExprS2.Visitor<String>, StmtS2.Visitor<String> {

  String print(ExprS2 expr) {
    return expr.accept(this);
  }

  String print(StmtS2 stmt) {
    return stmt.accept(this);
  }

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


    cl.append("class " + expand2(stmt.name.lexeme));
    if (stmt.superclass != null) {
      cl.append(" extends " + stmt.superclass.name.lexeme + " ");
    }
    if (stmt.interfaces != null) {
      for (int i = 0; i < stmt.interfaces.size(); i++)
      {
        if (i == stmt.interfaces.size()-1) {
          cl.append("implements " + stmt.interfaces.get(i).name.lexeme);
          break;
        }
        cl.append(stmt.interfaces.get(i).name.lexeme + ", ");
      }
    }
    
    cl.append(" {\n");
    cl.append(expand2("", stmt.methods));


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

    func.append(") \n{\n");

    for (StmtS2 i : stmt.body) {
      func.append(print(i));
    }

    func.append("\n}\n");

    return func.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitInterfaceFunctionStmt(StmtS2.InterfaceFunction stmt) {
    StringBuilder func = new StringBuilder();

    // currently would only work for functions with one param
    func.append(expand2(stmt.name.lexeme) + " (");
    for (int i = 0; i < stmt.params.size(); i++) {
      if (i == stmt.params.size() - 1) {
        func.append(stmt.paramstype.get(i).lexeme + " " + stmt.params.get(i).lexeme);
        break;
      }
      func.append(stmt.paramstype.get(i).lexeme + " " + stmt.params.get(i).lexeme + ", ");
    }

    func.append(");\n");

    return func.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitForStmt(StmtS2.For stmt) {
        StringBuilder loop = new StringBuilder();

        loop.append("for ("+ expandstmt(stmt.initializer) + expand(stmt.condition) + ";" + expand(stmt.increment) + ")\n{\n" + expandstmt(stmt.body) + "\n}\n");

        return loop.toString();
    }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitIfStmt(StmtS2.If stmt) {
    StringBuilder cond = new StringBuilder();

    if (stmt.elseBranch == null) {
      cond.append("if (" + expand(stmt.condition) + ")" + "\n{\n" + expandstmt(stmt.thenBranch) + "\n}\n");
    } else
      cond.append("if (" + expand(stmt.condition) + ")" + "\n{\n" + expandstmt(stmt.thenBranch) + "\n}\n" + "else\n{\n"
          + stmt.elseBranch + "\n}\n");

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
    public String visitTypesExpr(ExprS2.Types expr){
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
        if (expr.keyword.lexeme.equals("static"))
            mods.append("static ");        
        if (expr.keyword.lexeme.equals("abstract"))
            mods.append("abstract ");        
        return mods.toString();
        // if (expr.lexeme.equals("String"))
        //     mods.append("string");
    }
  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitWhileStmt(StmtS2.While stmt) {
    StringBuilder loop = new StringBuilder();
    loop.append("while (" + expand(stmt.condition) + ")\n{\n " + expandstmt(stmt.body) + "\n}\n");
    return loop.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
    public String visitDoStmt(StmtS2.Do stmt) {
        StringBuilder loop = new StringBuilder();
        loop.append("do {\n");
        for (int i =0; i < stmt.body.size(); i++) {
            loop.append(expand2("\n", stmt.body.get(i)));
        }
        loop.append("\n}" );
        System.out.println("in visitdo translator " + loop);
        return loop.toString();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String visitWhileDo(StmtS2.WhileDo stmt){
        System.out.println("in translator for whiledo " + expand2("", stmt.condition) + " " + expand(stmt.condition));
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
  public String visitInterfaceStmt(StmtS2.Interface stmt) {
    StringBuilder itf = new StringBuilder();
    itf.append("interface " + stmt.name.lexeme + " {\n");
    for (int i = 0; i < stmt.methods.size(); i++) {
      itf.append("\n" + print(stmt.modifiers.get(i)) + print(stmt.methods.get(i)));
    }

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
    for (int i = 0; i < stmt.body.size(); i++) {
      if (i == stmt.body.size() - 1) {
        enums.append(stmt.body.get(i).lexeme);
        break;
      }
      enums.append(stmt.body.get(i).lexeme + ",");
    }

    enums.append("\n}\n");
    return enums.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitVariableExpr(ExprS2.Variable expr) {
    StringBuilder variable = new StringBuilder();
    variable.append(expr.name.lexeme);
    return variable.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitContinueStmt(StmtS2.Continue stmt) {
    StringBuilder c = new StringBuilder();
    c.append("\n");
    c.append(expand2(stmt.keyword.lexeme));
    c.append(";");

    return c.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitGetExpr(ExprS2.Get expr) {
    StringBuilder get = new StringBuilder();
    get.append(expr.object + "." + expr.name.lexeme);
    return get.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitCallExpr(ExprS2.Call expr) {
    return null;
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
    return null;
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
    bin.append(expand(expr.left));
    bin.append(expr.operator.lexeme);
    bin.append(expand(expr.right));
    return bin.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String visitGroupingExpr(ExprS2.Grouping expr) {
        StringBuilder group = new StringBuilder();
        group.append("(");
        if(expr.expression.equals(null))
        {
            group.append("");
        }
        else {
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
            if(i == expr.expression.size()-1)
            {
                group.append(expand2("", expr.expression.get(i)));
            }
            else
            {
                group.append(expand2("", expr.expression.get(i), ", "));
            }
        }
//        group.append(expand(expr.expression));
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
            if(i == expr.expression.size()-1)
            {
                group.append(expand2("", expr.expression.get(i)));
            }
            else
            {
                group.append(expand2("", expr.expression.get(i), ", "));
            }
        }
//        group.append(expand(expr.expression));
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
        assign.append(expand2("", expr.name , " = " , expr.value, ";\n"));
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
        trystmt.append(expand2("",stmt.body));
        trystmt.append("\n}\n");
        return trystmt.toString();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String visitCatchStmt(StmtS2.Catch stmt) {
        StringBuilder catchstmt = new StringBuilder();
        catchstmt.append(stmt.keyword.lexeme);
        catchstmt.append(" (");
        for(int i =0; i < stmt.params.size(); i++ ){
            if(i == stmt.params.size()-1){
                catchstmt.append(stmt.paramstype.get(i).lexeme + " " + stmt.params.get(i).lexeme);
                break;
            }
            catchstmt.append(stmt.paramstype.get(i).lexeme + " " + stmt.params.get(i).lexeme + ", ");
        }
        catchstmt.append(") {\n");
        catchstmt.append(expand2("",stmt.body));
        catchstmt.append("\n}\n");
        return catchstmt.toString();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public String visitFinallyStmt(StmtS2.Finally stmt) {
        StringBuilder finstmt = new StringBuilder();
        finstmt.append(stmt.keyword.lexeme);
        finstmt.append(" {\n");
        finstmt.append(expand2("",stmt.body));
        finstmt.append("\n}\n");
        return finstmt.toString();
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
    log.append(expr.left + " " + expr.operator.lexeme + " " + expr.right);
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
    public String visitNewLineExpr(){
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
