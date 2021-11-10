import java.util.List;

class Translator implements ExprS2.Visitor<String>, StmtS2.Visitor<String> {

  String print(ExprS2 expr) {
    return expr.accept(this);
  }

  String print(StmtS2 stmt) {
    return stmt.accept(this);
  }

  @Override
  public String visitModifiersStmt(StmtS2.Modifiers stmt) {
    StringBuilder mods = new StringBuilder();
    for (TokenS2 modifier : stmt.modifiers) {      
      if (modifier.lexeme.equals("public"))
        mods.append("public ");
      if (modifier.lexeme.equals("private"))
        mods.append("private ");
      if (modifier.lexeme.equals("protected"))
        mods.append("protected ");
      if (modifier.lexeme.equals("String"))
        mods.append("string");
      if (modifier.lexeme.equals("final"))
        mods.append("const ");
      if (modifier.lexeme.equals("static"))
        mods.append("static ");
      if (modifier.lexeme.equals("void"))
        mods.append("void ");
      if (modifier.lexeme.equals("abstract"))
        mods.append("abstract ");
      if (modifier.lexeme.equals("int"))
        mods.append("int ");
      if (modifier.lexeme.equals("double"))
        mods.append("double ");
      if (modifier.lexeme.equals("float"))
        mods.append("float "); 
    }    
    return expand2(mods.toString());
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

    if (stmt.initializer == null) {
      var.append(expand2(stmt.name.lexeme) + ";");
      return var.toString();
    }
    else {
    var.append(stmt.name.lexeme);
    var.append(" = ");
    var.append(expand(stmt.initializer));
    var.append(";");    
    }
    return var.toString();
  }
  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitClassStmt(StmtS2.Class stmt) {
    StringBuilder cl = new StringBuilder();

    // currently would only work for basic classes
    cl.append("class " + expand2(stmt.name.lexeme) + " {");
    for (StmtS2 method : stmt.methods) {
      cl.append("\n" + print(method));
    }

    cl.append("\n}\n");
    return cl.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitFunctionStmt(StmtS2.Function stmt) {
    StringBuilder func = new StringBuilder();

    // currently would only work for functions with one param
    func.append(expand2(stmt.name.lexeme) + " (");
    for(int i =0; i < stmt.params.size(); i++ ){
      if(i == stmt.params.size()-1){
        func.append(stmt.paramstype.get(i).lexeme + " " + stmt.params.get(i).lexeme);
        break;
      }
      func.append(stmt.paramstype.get(i).lexeme + " " + stmt.params.get(i).lexeme + ", ");           
    }
    
    func.append(") {");
    
    for(StmtS2 i : stmt.body) {
      func.append("\n " + print(i));
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
    for(int i =0; i < stmt.params.size(); i++ ){
      if(i == stmt.params.size()-1){
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

    loop.append("for ("+ expandstmt(stmt.initializer) + expand(stmt.condition) + ";" + expand(stmt.increment) + ")\n{" + expandstmt(stmt.body) + "\n}\n");

    return loop.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitIfStmt(StmtS2.If stmt) {
    StringBuilder cond = new StringBuilder();

    if (stmt.elseBranch == null) {
      cond.append("if (" + expand(stmt.condition) + ")" + "\n{\n" + expandstmt(stmt.thenBranch) + "\n}\n");
    }
    else cond.append(
        "if (" + expand(stmt.condition) + ")" + "\n{\n" + expandstmt(stmt.thenBranch) + "\n}\n" + "else\n{\n" + stmt.elseBranch + "\n}\n");

    return cond.toString();
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
  public String visitBreakStmt(StmtS2.Break stmt) {
    StringBuilder brk = new StringBuilder();
    brk.append("\n");
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

  @Override
  public String visitModifiersExpr(ExprS2.Modifiers expr) {
    return expand2("", expr.keyword.lexeme);
    // if (expr.lexeme.equals("String"))
    //     mods.append("string");
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitDoStmt(StmtS2.Do stmt) {    
    StringBuilder loop = new StringBuilder();
    loop.append("do {\n" + expandstmt(stmt.body) + "\n} " + "while (" + expand(stmt.condition) + ");\n");
    return loop.toString();   
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
  public String visitInterfaceStmt(StmtS2.Interface stmt) {
    StringBuilder itf = new StringBuilder();
    itf.append("interface " + stmt.name.lexeme + " {\n");
    for (int i=0; i < stmt.methods.size(); i++) {
      itf.append("\n" + print(stmt.modifiers.get(i)) + print(stmt.methods.get(i)));
    }


    /* for (StmtS2 mod:stmt.modifiers) {
      itf.append(print(mod));
    }
    for (StmtS2 method : stmt.methods) {
      itf.append("\n" + print(method));
    } */
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
    for (int i =0; i < stmt.body.size(); i++) {
      if (i == stmt.body.size()-1) {
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
    get.append(expr.object+"."+expr.name.lexeme);
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
    //StringBuilder throwstmt = new StringBuilder();
    //throwstmt.appen("\n");
    //throwstmt.append(expand2("",stmt.keyword.lexeme));
    //return throwstmt.toString();
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
    return expand2("",
                expr.object, expr.name.lexeme, expr.value);
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
    System.out.println("in translator - grouping expr " + expr + " " + expr.expression);
    StringBuilder group = new StringBuilder();
    group.append("(");
    if(expr.expression == null)
    {
      group.append("");
    }
    else {
      group.append(expand(expr.expression));
    }
    group.append(")");
    return group.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
    public String visitArrayGroupingExpr(ExprS2.ArrayGrouping expr) {
        StringBuilder group = new StringBuilder();
        group.append("[");
        group.append(expand(expr.expression));
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
  public String visitAssignExpr(ExprS2.Assign expr) {
    StringBuilder assign = new StringBuilder();
    assign.append(expand2("", expr.name , " = " , expr.value));
    return assign.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitExpressionStmt(StmtS2.Expression stmt) {
    StringBuilder expr = new StringBuilder();
    expr.append(expand2("",stmt.expression));
    return expr.toString();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitTryStmt(StmtS2.Try stmt) {
    StringBuilder trystmt = new StringBuilder();
    trystmt.append(stmt.keyword.lexeme);
    trystmt.append(" {\n");
    trystmt.append(expandstmt(stmt.body));
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
    catchstmt.append(expandstmt(stmt.body));
    catchstmt.append("\n}\n");
    return catchstmt.toString();
  }
  ////////////////////////////////////////////////////////////////////////////////////////////
  @Override
  public String visitFinallyStmt(StmtS2.Finally stmt) {
    StringBuilder finstmt = new StringBuilder();
    finstmt.append(stmt.keyword.lexeme);
    finstmt.append(" {\n");
    finstmt.append(expandstmt(stmt.body));
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
    if (expr.value == null) literal.append("null");
    else literal.append(expr.value);
    return literal.toString();
  }
  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////

  private String expand(ExprS2... exprs) {
    StringBuilder builder = new StringBuilder();
    for (ExprS2 expr : exprs) {
      //builder.append(" ");
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
    //builder.append(")");

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
      //builder.append(" ");
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
