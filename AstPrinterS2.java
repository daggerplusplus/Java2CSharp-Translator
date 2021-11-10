import java.util.List;

class AstPrinterS2 implements ExprS2.Visitor<String>, StmtS2.Visitor<String> {    
    String print(ExprS2 expr) {
        return expr.accept(this);
    }

    String print(StmtS2 stmt) {
        return stmt.accept(this);
    }



    @Override
    public String visitBlockStmt(StmtS2.Block stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(block ");

        for (StmtS2 statement : stmt.statements) {
            builder.append(statement.accept(this));
        }

        builder.append(")");
        return builder.toString();
    }




    @Override
    public String visitClassStmt(StmtS2.Class stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(CLASS " + stmt.name.lexeme);


        if (stmt.superclass != null) {
            builder.append(" < " + print(stmt.superclass));
        }


        for (StmtS2 method : stmt.methods) {
            builder.append(" " + print(method));
        }

        builder.append(")");
        return builder.toString();
    }


    @Override
    public String visitExpressionStmt(StmtS2.Expression stmt) {
        return parenthesize("EXPRESSION", stmt.expression);
    }
//    public String visitExpressionStmt(Stmt.Expression stmt) {
//        return parenthesize(";", stmt.expression);
//    }

    @Override
    public String visitFunctionStmt(StmtS2.Function stmt) {
        StringBuilder builder = new StringBuilder();
        builder.append("(FUNCTION DECLARATION " + stmt.name.lexeme);


        if(!stmt.params.isEmpty())
        {
            builder.append(" (PARAMETERS ");
            for (int index=0; index < stmt.params.size(); index++)
            {
                if (stmt.params.get(index) != stmt.params.get(0)) builder.append(" ");
                builder.append(stmt.params.get(index).lexeme);
                builder.append(" (TYPE MODIFIER ");
//                System.out.println(stmt.paramstype.get(index));
                builder.append(stmt.paramstype.get(index).lexeme);
                builder.append(") ");
            }
            builder.append(") ");
        }

        for (StmtS2 body : stmt.body) {
            builder.append(body.accept(this));
        }

        builder.append(")");
        return builder.toString();
    }


    @Override
    public String visitIfStmt(StmtS2.If stmt) {
        if (stmt.elseBranch == null) {
            return parenthesize2("IF", "CONDITION" , stmt.condition, stmt.thenBranch);
        }

        return parenthesize2("IF-ELSE", "CONDITION" ,stmt.condition, stmt.thenBranch,
                stmt.elseBranch);
    }


    @Override
    public String visitPrintStmt(StmtS2.Print stmt) {
        return parenthesize("PRINT", stmt.expression);
    }

    public String visitPrintlnStmt(StmtS2.println stmt) {
        return parenthesize("PRINTLINE", stmt.expression);
    }


    @Override
    public String visitReturnStmt(StmtS2.Return stmt) {
        if (stmt.value == null) return "(RETURN)";
        return parenthesize("RETURN", stmt.value);
    }


    @Override
    public String visitVarStmt(StmtS2.Var stmt) {
        StringBuilder builder = new StringBuilder();

//        builder.append("(TYPE MODIFIER ");
//        builder.append(stmt.type.lexeme);
//        builder.append(")");

        if (stmt.initializer == null) {
            return parenthesize2("VARIABLE DECLARATION", builder.toString() , stmt.name);
        }

        return parenthesize2("VARIABLE DECLARATION", builder.toString(), "(ASSIGNMENT", stmt.name, stmt.initializer, ")");
    }


    @Override
    public String visitWhileStmt(StmtS2.While stmt) {
        return parenthesize2("WHILE", stmt.condition, stmt.body);
    }


    @Override
    public String visitAssignExpr(ExprS2.Assign expr) {
        return parenthesize2("ASSIGNMENT", expr.name.lexeme, expr.value);
    }


    @Override
    public String visitBinaryExpr(ExprS2.Binary expr) {
        return parenthesize(expr.operator.lexeme,
                expr.left, expr.right);
    }


    @Override
    public String visitCallExpr(ExprS2.Call expr) {
        return parenthesize2("CALL", expr.callee, expr.arguments);
    }


    @Override
    public String visitGetExpr(ExprS2.Get expr) {
        return parenthesize2(".", expr.object, expr.name.lexeme);
    }


    @Override
    public String visitGroupingExpr(ExprS2.Grouping expr) {
        return parenthesize("GROUP", expr.expression);
    }

    @Override
  public String visitArrayGroupingExpr(ExprS2.ArrayGrouping expr) {
    return parenthesize("GROUP", expr.expression);
  }

    @Override
    public String visitLiteralExpr(ExprS2.Literal expr) {
        if (expr.value == null) return "NILL";
        return expr.value.toString();
    }


    @Override
    public String visitLogicalExpr(ExprS2.Logical expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }


    @Override
    public String visitSetExpr(ExprS2.Set expr) {
        return parenthesize2("ASSIGNMENT",
                expr.object, expr.name.lexeme, expr.value);
    }


    @Override
    public String visitSuperExpr(ExprS2.Super expr) {
        return parenthesize2("SUPER", expr.method);
    }

    @Override
    public String visitThisExpr(ExprS2.This expr) {
        return "THIS";
    }

    @Override
  public String visitModifiersExpr(ExprS2.Modifiers expr) {
    return parenthesize2("", expr.keyword.lexeme);
  }

    @Override
    public String visitPackageExpr(ExprS2.Package expr) {
        return "Package";
    }

    @Override
    public String visitImportExpr(ExprS2.Import expr) {
        return "Import";
    }

    @Override
    public String visitDotExpr(ExprS2.Dot expr) { return "DOT"; }



    @Override
    public String visitUnaryExpr(ExprS2.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    @Override
    public String visitUnary2Expr(ExprS2.Unary2 expr) {
        return parenthesize2("", expr.left, expr.operator.lexeme);
    }


    @Override
    public String visitVariableExpr(ExprS2.Variable expr) {
        return expr.name.lexeme;
    }

    @Override
    public String visitBreakStmt(StmtS2.Break stmt) {
        return stmt.keyword.lexeme;
    }
    @Override
    public String visitForStmt(StmtS2.For stmt) {
        if (stmt.initializer == null && stmt.increment == null) return parenthesize2("FOR", "", stmt.condition, "", stmt.body);
        if (stmt.initializer == null) return parenthesize2("FOR", "", stmt.condition, stmt.increment, stmt.body);
        if (stmt.increment == null) return parenthesize2("FOR", stmt.initializer, stmt.condition, "", stmt.body);
        return parenthesize2("FOR", stmt.initializer, stmt.condition, stmt.increment, stmt.body);
    }
    @Override
    public String visitDoStmt(StmtS2.Do stmt) {
        return null;
    }
    @Override
    public String visitEnumStmt(StmtS2.Enum stmt) {
        return stmt.keyword.lexeme;
    }
    @Override
    public String visitInterfaceStmt(StmtS2.Interface stmt) {
        return null;   //PLACEHOLDER
    }
    
    @Override
    public String visitModifiersStmt(StmtS2.Modifiers stmt) {
        StringBuilder builder = new StringBuilder();

        builder.append("MODIFIERS ");
        for (TokenS2 modifiers : stmt.modifiers) {
            if (modifiers != stmt.modifiers.get(0)) builder.append(" ");
            builder.append(modifiers.lexeme);
        }
//        builder.append(" ");

        return parenthesize2(builder.toString());
    }

    //CONTINUE
    @Override
    public String visitContinueStmt(StmtS2.Continue stmt) {
        return stmt.keyword.lexeme;
    }
   

    @Override
    public String visitThrowStmt(StmtS2.Throw stmt) {
        return null;
    }

    @Override
    public String visitSwitchStmt(StmtS2.Switch stmt) {
        return null; //PLACEHOLDER
    }

   
    @Override
    public String visitTryStmt(StmtS2.Try stmt) {
        return null; //PLACEHOLDER
    }   

    @Override
    public String visitCatchStmt(StmtS2.Catch stmt) {
      return null;
    }

    @Override
    public String visitFinallyStmt(StmtS2.Finally stmt) {
      return null;
    }

    @Override
    public String visitArrayGrouping2Expr(ExprS2.ArrayGrouping2 expr) {
      return null;
    }

    private String parenthesize(String name, ExprS2... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (ExprS2 expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    private String parenthesize2(String name, Object... parts) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        transform(builder, parts);
        builder.append(")");

        return builder.toString();
    }

    private String parenthesize3(String name, Object... parts) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        transform(builder, parts);

        return builder.toString();
    }

    private void transform(StringBuilder builder, Object... parts) {
        for (Object part : parts) {
            builder.append(" ");
            if (part instanceof ExprS2) {
                builder.append(((ExprS2)part).accept(this));

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
}
