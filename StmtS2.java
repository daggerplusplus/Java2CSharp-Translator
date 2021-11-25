import java.util.List;

abstract class StmtS2 {
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
        R visitThrowStmt(Throw stmt);
        R visitCatchStmt(Catch stmt);
        R visitFinallyStmt(Finally stmt);
        R visitGetStmt(Get stmt);
        R visitInterfaceFunctionStmt(InterfaceFunction stmt);
    }

    static class Block extends StmtS2 {
        Block(List<StmtS2> statements) {
            this.statements = statements;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        final List<StmtS2> statements;
    }

    static class Class extends StmtS2 {
        Class(TokenS2 name,
              List<ExprS2.Variable> superclass, List<ExprS2.Variable> implementinterface,
              List<StmtS2> methods) {
            this.name = name;
            this.superclass = superclass;
            this.implementinterface = implementinterface;
            this.methods = methods;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitClassStmt(this);
        }

        final TokenS2 name;
        final List<ExprS2.Variable> superclass;
        final List<ExprS2.Variable> implementinterface;
        final List<StmtS2> methods;
    }

    static class Interface extends StmtS2 {
        Interface(TokenS2 name,
                  List<ExprS2.Variable> extender, List<StmtS2.InterfaceFunction> methods,
                  List<StmtS2> mods) {
            this.name = name;
            this.extender = extender;
            this.methods = methods;
            this.mods = mods;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitInterfaceStmt(this);
        }
        final TokenS2 name;
        final List<ExprS2.Variable> extender;
        final List<StmtS2.InterfaceFunction> methods;
        final List<StmtS2> mods;
    }

    static class Expression extends StmtS2 {
        Expression(ExprS2 expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        final ExprS2 expression;
    }

    static class Function extends StmtS2 {
        Function(TokenS2 name, List<StmtS2> paramtyp,List<StmtS2> paramary, List<StmtS2> paramtid, List<StmtS2> body) {
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

        final TokenS2 name;
        final List<StmtS2> paramtyp;
        final List<StmtS2> paramary;
        final List<StmtS2> paramtid;
        final List<StmtS2> body;
    }

    
    static class InterfaceFunction extends StmtS2 {
        InterfaceFunction(TokenS2 name, List<StmtS2> paramtyp, List<StmtS2> paramary, List<StmtS2> paramtid) {
            this.name = name;
            this.paramtyp = paramtyp;
            this.paramary = paramary;   
            this.paramtid = paramtid;         
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitInterfaceFunctionStmt(this);
        }

        final TokenS2 name; 
        final List<StmtS2> paramtyp;
        final List<StmtS2> paramary;
        final List<StmtS2> paramtid;      
    }

    static class If extends StmtS2 {
        If(ExprS2 condition, List<StmtS2> thenBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        final ExprS2 condition;
        final List<StmtS2> thenBranch;
    }

    static class ElseIf extends StmtS2 {
        ElseIf(ExprS2 condition, List<StmtS2> thenBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitElseIfStmt(this);
        }

        final ExprS2 condition;
        final List<StmtS2> thenBranch;
    }

    static class Else extends StmtS2 {
        Else(List<StmtS2> thenBranch) {
            this.thenBranch = thenBranch;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitElseStmt(this);
        }

        final List<StmtS2> thenBranch;
    }

    static class Switch extends StmtS2 {
        Switch(ExprS2 condition, List<StmtS2> caseBranch, List<ExprS2> caseVal, StmtS2 defaultBranch) {
            this.condition = condition;          
            this.caseBranch = caseBranch;
            this.caseVal = caseVal;
            this.defaultBranch = defaultBranch;            
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSwitchStmt(this);
        }

        final ExprS2 condition;        
        final List<StmtS2> caseBranch;
        final List<ExprS2> caseVal;
        final StmtS2 defaultBranch;        
    }

    static class Print extends StmtS2 {
        Print(ExprS2 expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        final ExprS2 expression;
    }

    static class println extends StmtS2 {
        println(ExprS2 expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintlnStmt(this);
        }

        final ExprS2 expression;
    }

    static class Return extends StmtS2 {
        Return(TokenS2 keyword, ExprS2 value) {
            this.keyword = keyword;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }

        final TokenS2 keyword;
        final ExprS2 value;
    }

    static class Var extends StmtS2 {
        Var(TokenS2 name, List<ExprS2> initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
        final TokenS2 name;
        final List<ExprS2> initializer;
    }

    static class While extends StmtS2 {
        While(ExprS2 condition, StmtS2 body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }

        final ExprS2 condition;
        final StmtS2 body;
    }

    static class Do extends StmtS2 {
        Do(List<StmtS2> body) {
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitDoStmt(this);
        }
        final List<StmtS2> body;
    }
    static class WhileDo extends StmtS2 {
        WhileDo(ExprS2 condition) {
            this.condition = condition;
        }

        @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visitWhileDo(this);}
        final ExprS2 condition;
    }

    static class Break extends StmtS2 {
        Break(TokenS2 keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBreakStmt(this);
        }
        final TokenS2 keyword;
    }
    static class Continue extends StmtS2 {
        Continue(TokenS2 keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitContinueStmt(this);
        }
        final TokenS2 keyword;
    }
    static class Throw extends StmtS2 {
        Throw(TokenS2 keyword){
            this.keyword = keyword;
        }
        @Override
        <R> R accept(Visitor<R> visitor){
            return visitor.visitThrowStmt(this);
        }
        final TokenS2 keyword;
    }

    static class For extends StmtS2
    {
        For(StmtS2 initializer, ExprS2 condition, ExprS2 increment, StmtS2 body, TokenS2 type) {
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

        final StmtS2 initializer;
        final ExprS2 condition;
        final ExprS2 increment;
        final StmtS2 body;
        final TokenS2 type;
    }
    static class Enum extends StmtS2 {
        Enum(TokenS2 keyword, List<TokenS2> body) {
            this.keyword = keyword;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitEnumStmt(this);
        }
        final TokenS2 keyword;
        final List<TokenS2> body;
    }

    static class Try extends StmtS2 {
        Try(TokenS2 keyword, List<StmtS2> body) {
            this.keyword = keyword;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTryStmt(this);
        }
        final TokenS2 keyword;
        final List<StmtS2> body;
    }

    static class Catch extends StmtS2 {
        Catch(TokenS2 keyword,List<StmtS2> body, List<TokenS2> paramstype, List<TokenS2> params) {
            this.keyword = keyword;
            this.body = body;
            this.paramstype = paramstype;
            this.params = params;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCatchStmt(this);
        }
        final TokenS2 keyword;
        final List<StmtS2> body;
        final List<TokenS2> params;
        final List<TokenS2> paramstype;
    }

    static class Finally extends StmtS2 {
        Finally(TokenS2 keyword, List<StmtS2> body) {
            this.keyword = keyword;
            this.body = body;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFinallyStmt(this);
        }
        final TokenS2 keyword;
        final List<StmtS2> body;
    }

    static class Get extends StmtS2 {
        Get(ExprS2 expr) {
            this.expr = expr;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetStmt(this);
        }

        final ExprS2 expr;
    }

    abstract <R> R accept(Visitor<R> visitor);


}

