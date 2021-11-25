import java.util.List;

abstract class Expr {
    interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitAssignArrayExpr(AssignArray expr);
        R visitBinaryExpr(Binary expr);
        R visitCallExpr(Call expr);
        R visitGetExpr(Get expr);
        R visitGroupingExpr(Grouping expr);
        R visitGrouping2Expr(Grouping2 expr);
        R visitArrayGroupingExpr(ArrayGrouping expr);
        R visitArrayGrouping2Expr(ArrayGrouping2 expr);
        R visitArray2GroupingExpr(Array2Grouping expr);
        R visitArray2Grouping2Expr(Array2Grouping2 expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitSetExpr(Set expr);
        R visitSuperExpr(Super expr);
        R visitThisExpr(This expr);
        R visitMainExpr(Main expr);
        R visitImportExpr(Import expr);
        R visitPackageExpr(Package expr);
        R visitUnaryExpr(Unary expr);
        R visitUnary2Expr(Unary2 expr);
        R visitUnary3Expr(Unary3 expr);
        R visitVariableExpr(Variable expr);
        R visitDotExpr(Dot expr);
        R visitModifiersExpr(Modifiers expr);
        R visitTypesExpr(Types expr);
        R visitNewExpr(New expr);
        R visitNewLineExpr();
        R visitThrowExpr(Throw expr);
        R visitExceptionExpr(Exception expr);
        
    }


    static class Assign extends Expr {
        Assign(Token name, Expr value, Token equals) {
            this.name = name;
            this.value = value;
            this.equals = equals;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
        final Token name;
        final Expr value;
        final Token equals;
    }

    static class AssignArray extends Expr {
        AssignArray(Expr arr, Expr value, Token equals) {
            this.arr = arr;
            this.value = value;
            this.equals = equals;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignArrayExpr(this);
        }
        final Expr arr;
        final Expr value;
        final Token equals;
    }

    static class Binary extends Expr {
        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    static class Call extends Expr {
        Call(Expr callee, List<Expr> arguments) {
            this.callee = callee;            
            this.arguments = arguments;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }

        final Expr callee;        
        final List<Expr> arguments;
    }

    static class Get extends Expr {
        Get(Expr object, Expr name) {
            this.object = object;
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpr(this);
        }

        final Expr object;
        final Expr name;
    }

    static class Grouping extends Expr {
        Grouping(List<Expr> expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final List<Expr> expression;
    }

    static class Grouping2 extends Expr {
        Grouping2(Token token) {
            this.token = token;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGrouping2Expr(this);
        }

        final Token token;
    }

    static class ArrayGrouping extends Expr {
        ArrayGrouping(List<Expr> expression, int dimension) {
            this.expression = expression;
            this.dimension = dimension;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitArrayGroupingExpr(this);
        }

        final List<Expr> expression;
        final int dimension;
    }

    static class ArrayGrouping2 extends Expr {
        ArrayGrouping2(Token token) {
            this.token = token;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitArrayGrouping2Expr(this);
        }

        final Token token;
    }

    static class Array2Grouping extends Expr {
        Array2Grouping(List<Expr> expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitArray2GroupingExpr(this);
        }

        final List<Expr> expression;
    }

    static class Array2Grouping2 extends Expr {
        Array2Grouping2(Token token) {
            this.token = token;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitArray2Grouping2Expr(this);
        }

        final Token token;
    }

    static class Literal extends Expr {
        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;
    }

    static class Logical extends Expr {
        Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    static class Set extends Expr {
        Set(Expr object, Expr name, Expr value, Token equals) {
            this.object = object;
            this.name = name;
            this.value = value;
            this.equals = equals;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetExpr(this);
        }

        final Expr object;
        final Expr name;
        final Expr value;
        final Token equals;
    }

    static class Super extends Expr {
        Super(Token keyword, Token method) {
            this.keyword = keyword;
            this.method = method;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSuperExpr(this);
        }

        final Token keyword;
        final Token method;
    }

    static class This extends Expr {
        This(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitThisExpr(this);
        }

        final Token keyword;
    }

    static class New extends Expr {
        New(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitNewExpr(this);
        }

        final Token keyword;
    }

    static class NewLine extends Expr {
        NewLine() {
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitNewLineExpr();
        }

    }

    static class Main extends Expr {
        Main(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitMainExpr(this);
        }

        final Token keyword;
    }

    static class Modifiers extends Expr {
        Modifiers(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitModifiersExpr(this);
        }

        final Token keyword;
    }

    static class Types extends Expr {
        Types(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitTypesExpr(this);
        }

        final Token keyword;
    }

    static class Import extends Expr {
        Import(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitImportExpr(this);
        }

        final Token keyword;
    }

    static class Throw extends Expr {
        Throw(Token exp, Stmt grp) {

            this.exp = exp;
            this.grp = grp;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitThrowExpr(this);
        }

        final Token exp;
        final Stmt grp;
    }

    static class Exception extends Expr {
        Exception(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExceptionExpr(this);
        }

        final Token keyword;
    }

    static class Package extends Expr {
        Package(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPackageExpr(this);
        }

        final Token keyword;
    }

    static class Dot extends Expr {
        Dot(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitDotExpr(this);
        }

        final Token keyword;
    }

    static class Unary extends Expr {
        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator;
        final Expr right;
    }

    static class Unary2 extends Expr {
        Unary2(Expr left, Token operator) {
            this.left = left;
            this.operator = operator;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnary2Expr(this);
        }

        final Token operator;
        final Expr left;
    }

    static class Unary3 extends Expr {
        Unary3(Expr left, Token operator, Expr ln) {
            this.left = left;
            this.operator = operator;
            this.ln = ln;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnary3Expr(this);
        }

        final Token operator;
        final Expr left;
        final Expr ln;
    }

    static class Variable extends Expr {
        Variable(Token name) {
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        final Token name;
    }



    abstract <R> R accept(Visitor<R> visitor);
}
