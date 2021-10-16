import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    R visitBlockStmt(Block stmt);
    R visitClassStmt(Class stmt);
    R visitExpressionStmt(Expression stmt);
    R visitFunctionStmt(Function stmt);
    R visitIfStmt(If stmt);
    R visitPrintStmt(Print stmt);
    R visitPrintlnStmt(println stmt);
    R visitReturnStmt(Return stmt);
    R visitVarStmt(Var stmt);
    R visitWhileStmt(While stmt);
    R visitDoStmt(Do stmt);
    R visitSwitchStmt(Switch stmt); 
    R visitBreakStmt(Break stmt); 
    R visitContinueStmt (Continue stmt);
    R visitPublicStmt (Public stmt);
    R visitAbstractStmt (Abstract stmt);
    R visitPrivateStmt (Private stmt);
    R visitForStmt (For stmt);
    R visitEnumStmt(Enum stmt);
    R visitStaticStmt (Static stmt);
    R visitInterfaceStmt(Interface stmt);
    R visitTryStmt (Try stmt);
    R visitThrowStmt(Throw stmt); 
    R visitMainStmt(Main stmt);
    R visitProtectedStmt(Protected stmt);
    R visitFinalStmt(Final stmt); 
    R visitConstStmt(Const stmt);
    R visitExtendsStmt(Extends stmt);

   /*
      AND, , , FALSE, FOR, , NIL, OR,
  , , SUPER, THIS, TRUE, VAR, ,
  , , SYSTEM, OUT, , ,
  , , , , VOID, VOLATILE,   */

  }


  static class Block extends Stmt {
    Block(List<Stmt> statements) {
      this.statements = statements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }

    final List<Stmt> statements;
  }

  static class Class extends Stmt {
    Class(Token name,
          Expr.Variable superclass,
          List<Stmt.Function> methods) {
      this.name = name;
      this.superclass = superclass;
      this.methods = methods;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitClassStmt(this);
    }

    final Token name;
    final Expr.Variable superclass;
    final List<Stmt.Function> methods;
  }

    static class Interface extends Stmt {
    Interface(Token name,
          List<Stmt.Function> methods) {
      this.name = name;      
      this.methods = methods;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitInterfaceStmt(this);
    }

    final Token name;    
    final List<Stmt.Function> methods;
  }

  static class Expression extends Stmt {
    Expression(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    final Expr expression;
  }

  static class Function extends Stmt {
    Function(Token name, List<Token> params, List<Stmt> body) {
      this.name = name;
      this.params = params;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunctionStmt(this);
    }

    final Token name;
    final List<Token> params;
    final List<Stmt> body;
  }

  static class If extends Stmt {
    If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }

    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;
  }

  static class Switch extends Stmt {
    Switch(Expr condition, Expr body, Stmt caseBranch, Stmt defaultBranch) {
      this.condition = condition;
      this.body = body;
      this.caseBranch = caseBranch;
      this.defaultBranch = defaultBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitSwitchStmt(this);
    }

    final Expr condition;
    final Expr body;
    final Stmt caseBranch;
    final Stmt defaultBranch;
  }

  static class Print extends Stmt {
    Print(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    final Expr expression;
  }

  static class println extends Stmt {
    println(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintlnStmt(this);
    }

    final Expr expression;
  }

  static class Return extends Stmt {
    Return(Token keyword, Expr value) {
      this.keyword = keyword;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStmt(this);
    }

    final Token keyword;
    final Expr value;
  }

  static class Var extends Stmt {
    Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }

    final Token name;
    final Expr initializer;
  }

  static class While extends Stmt {
    While(Expr condition, Stmt body) {
      this.condition = condition;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }

    final Expr condition;
    final Stmt body;
  }

  static class Do extends Stmt {
    Do(Expr condition, Stmt body) {
      this.condition = condition;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitDoStmt(this);
    }

    final Expr condition;
    final Stmt body;
  }

  static class Break extends Stmt {
    Break(Token keyword) {
      this.keyword = keyword;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBreakStmt(this);
    }
    final Token keyword;
  }
  static class Continue extends Stmt {
    Continue(Token keyword) {
      this.keyword = keyword;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitContinueStmt(this);
    }
    final Token keyword;
  }

  static class Public extends Stmt {
    Public(Token keyword, Expr expr) {
      this.keyword = keyword;
      this.expr = expr;
    }
    
    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPublicStmt(this);
    }
    final Token keyword;
    final Expr expr;
  }



  static class Abstract extends Stmt {
    Abstract(Token keyword) {
      this.keyword = keyword;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitAbstractStmt(this);
    }
    final Token keyword;
  }
  
  static class Throw extends Stmt {
    Throw(Token keyword){
      this.keyword = keyword;
    }    
    @Override
    <R> R accept(Visitor<R> visitor){
      return visitor.visitThrowStmt(this);
    }
    final Token keyword;
  }
  
  static class Extends extends Stmt {
    Extends(Token keyword) {
      this.keyword = keyword;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExtendsStmt(this);
    }
    final Token keyword;
  }


  static class Private extends Stmt {
    Private(Token keyword) {
      this.keyword = keyword;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrivateStmt(this);
    }

    final Token keyword;
  }



  static class Final extends Stmt {
    Final(Expr condition) {
      this.condition = condition;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFinalStmt(this);
    }
    final Expr condition;
  }

  static class Const extends Stmt {
    Const(Token keyword) {
      this.keyword = keyword;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitConstStmt(this);
    }
    final Token keyword;
  }

  static class For extends Stmt 
  {
    For(Expr initialization, Expr condition, Expr incrdecr, Stmt body) {
      this.initialization = initialization;
      this.condition = condition;
      this.incrdecr = incrdecr;
      this.body = body;
    }
  

  @Override
  <R> R accept(Visitor<R> visitor) {
    return visitor.visitForStmt(this);
 // TODO Auto-generated method stub
   // super.finalize();
  }
  final Expr initialization;
  final Expr condition;
  final Expr incrdecr;
  final Stmt body;
}



static class Protected extends Stmt {
    Protected(Token keyword) {
      this.keyword = keyword;
    }
    
    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitProtectedStmt(this);
    }
    final Token keyword;
  }

  static class Enum extends Stmt {
    Enum(Token keyword, Stmt body) {
      this.keyword = keyword;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitEnumStmt(this);      
    }
    final Token keyword;
    final Stmt body;
  }

static class Try extends Stmt {
    Try(Expr condition) {
      this.condition = condition;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitTryStmt(this);
    }
    final Expr condition;
  }
static class Static extends Stmt {
    Static(Expr condition) {
      this.condition = condition;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitStaticStmt(this);
    }
    final Expr condition;
  }

static class Main extends Stmt {
    Main(Expr condition) {
      this.condition = condition;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitMainStmt(this);
    }
    final Expr condition;
  }
  

  abstract <R> R accept(Visitor<R> visitor);
  

}
