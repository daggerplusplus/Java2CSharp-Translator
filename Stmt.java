import java.util.List;

abstract class Stmt {
    interface Visitor<R> {
        R visitBlockStmt(Block stmt);
        R visitClassStmt(Class stmt);
        R visitExpressionStmt(Expression stmt);
        R visitFunctionStmt(Function stmt);
        R visitIfStmt(If stmt);
        R visitElseIfStmt(ElseIf stmt);
        R visitElseStmt(Else stmt);
        R visitPrintStmt(Print stmt);
        R visitPrintlnStmt(println stmt);
        R visitReturnStmt(Return stmt);
        R visitVarStmt(Var stmt);
        R visitWhileStmt(While stmt);
        R visitDoStmt(Do stmt);
        R visitWhileDo(WhileDo stmt);
        R visitSwitchStmt(Switch stmt);
        R visitBreakStmt(Break stmt);
        R visitContinueStmt (Continue stmt);
        R visitForStmt(For stmt);
        R visitEnumStmt(Enum stmt);
        R visitInterfaceStmt(Interface stmt);
        R visitTryStmt(Try stmt);
        R visitCatchStmt(Catch stmt);
        R visitFinallyStmt(Finally stmt);
        R visitGetStmt(Get stmt);
        R visitInterfaceFunctionStmt(InterfaceFunction stmt);
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
              List<Expr.Variable> superclass, List<Expr.Variable> implementinterface,
              List<Stmt> methods) {
            this.name = name;
            this.superclass = superclass;
            this.implementinterface = implementinterface;
            this.methods = methods;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitClassStmt(this);
        }

        final Token name;
        final List<Expr.Variable> superclass;
        final List<Expr.Variable> implementinterface;
        final List<Stmt> methods;
    }

    static class Interface extends Stmt {
        Interface(Token name,
                  List<Expr.Variable> extender, List<Stmt.InterfaceFunction> methods,
                  List<Stmt> mods) {
            this.name = name;
            this.extender = extender;
            this.methods = methods;
            this.mods = mods;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitInterfaceStmt(this);
        }
        final Token name;
        final List<Expr.Variable> extender;
        final List<Stmt.InterfaceFunction> methods;
        final List<Stmt> mods;
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
        Function(Token name, List<Stmt> paramtyp,List<Stmt> paramary, List<Stmt> paramtid, List<Stmt> body) {
            this.name = name;
            this.paramtyp = paramtyp;
            this.paramary = paramary;
            this.paramtid = paramtid;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }

        final Token name;
        final List<Stmt> paramtyp;
        final List<Stmt> paramary;
        final List<Stmt> paramtid;
        final List<Stmt> body;
    }

    
    static class InterfaceFunction extends Stmt {
        InterfaceFunction(Token name, List<Stmt> paramtyp, List<Stmt> paramary, List<Stmt> paramtid) {
            this.name = name;
            this.paramtyp = paramtyp;
            this.paramary = paramary;   
            this.paramtid = paramtid;         
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitInterfaceFunctionStmt(this);
        }

        final Token name; 
        final List<Stmt> paramtyp;
        final List<Stmt> paramary;
        final List<Stmt> paramtid;      
    }

    static class If extends Stmt {
        If(Expr condition, List<Stmt> thenBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        final Expr condition;
        final List<Stmt> thenBranch;
    }

    static class ElseIf extends Stmt {
        ElseIf(Expr condition, List<Stmt> thenBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitElseIfStmt(this);
        }

        final Expr condition;
        final List<Stmt> thenBranch;
    }

    static class Else extends Stmt {
        Else(List<Stmt> thenBranch) {
            this.thenBranch = thenBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitElseStmt(this);
        }

        final List<Stmt> thenBranch;
    }

    static class Switch extends Stmt {
        Switch(Expr condition, List<Stmt> caseBranch, List<Expr> caseVal, Stmt defaultBranch) {
            this.condition = condition;          
            this.caseBranch = caseBranch;
            this.caseVal = caseVal;
            this.defaultBranch = defaultBranch;            
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSwitchStmt(this);
        }

        final Expr condition;        
        final List<Stmt> caseBranch;
        final List<Expr> caseVal;
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
        Var(Token name, List<Expr> initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
        final Token name;
        final List<Expr> initializer;
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
        Do(List<Stmt> body) {
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitDoStmt(this);
        }
        final List<Stmt> body;
    }
    static class WhileDo extends Stmt {
        WhileDo(Expr condition) {
            this.condition = condition;
        }

        @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visitWhileDo(this);}
        final Expr condition;
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
    static class For extends Stmt
    {
        For(Stmt initializer, Expr condition, Expr increment, Stmt body, Token type) {
            this.initializer = initializer;
            this.condition = condition;
            this.increment = increment;
            this.body = body;
            this.type = type;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitForStmt(this);          
        }

        final Stmt initializer;
        final Expr condition;
        final Expr increment;
        final Stmt body;
        final Token type;
    }
    static class Enum extends Stmt {
        Enum(Token keyword, List<Token> body) {
            this.keyword = keyword;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitEnumStmt(this);
        }
        final Token keyword;
        final List<Token> body;
    }

    static class Try extends Stmt {
        Try(Token keyword, List<Stmt> body) {
            this.keyword = keyword;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTryStmt(this);
        }
        final Token keyword;
        final List<Stmt> body;
    }

    static class Catch extends Stmt {
        Catch(Token keyword,List<Stmt> body, List<Token> paramstype, List<Token> params) {
            this.keyword = keyword;
            this.body = body;
            this.paramstype = paramstype;
            this.params = params;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCatchStmt(this);
        }
        final Token keyword;
        final List<Stmt> body;
        final List<Token> params;
        final List<Token> paramstype;
    }

    static class Finally extends Stmt {
        Finally(Token keyword, List<Stmt> body) {
            this.keyword = keyword;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFinallyStmt(this);
        }
        final Token keyword;
        final List<Stmt> body;
    }

    static class Get extends Stmt {
        Get(Expr expr) {
            this.expr = expr;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetStmt(this);
        }

        final Expr expr;
    }

    abstract <R> R accept(Visitor<R> visitor);


}

