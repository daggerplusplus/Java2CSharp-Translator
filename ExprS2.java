//> Appendix II expr
import java.util.List;

abstract class ExprS2 {
    interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitBinaryExpr(Binary expr);
        R visitCallExpr(Call expr);
        R visitGetExpr(Get expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitSetExpr(Set expr);
        R visitSuperExpr(Super expr);
        R visitThisExpr(This expr);
        R visitImportExpr(Import expr);
        R visitPackageExpr(Package expr);
        R visitUnaryExpr(Unary expr);
        R visitVariableExpr(Variable expr);
        R visitDotExpr(Dot expr);
    }


    static class Assign extends ExprS2 {
        Assign(TokenS2 name, ExprS2 value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
        final TokenS2 name;
        final ExprS2 value;
    }

    static class Binary extends ExprS2 {
        Binary(ExprS2 left, TokenS2 operator, ExprS2 right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        final ExprS2 left;
        final TokenS2 operator;
        final ExprS2 right;
    }

    static class Call extends ExprS2 {
        Call(ExprS2 callee, TokenS2 paren, List<ExprS2> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }

        final ExprS2 callee;
        final TokenS2 paren;
        final List<ExprS2> arguments;
    }

    static class Get extends ExprS2 {
        Get(ExprS2 object, TokenS2 name) {
            this.object = object;
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpr(this);
        }

        final ExprS2 object;
        final TokenS2 name;
    }

    static class Grouping extends ExprS2 {
        Grouping(ExprS2 expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final ExprS2 expression;
    }

    static class Literal extends ExprS2 {
        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;
    }

    static class Logical extends ExprS2 {
        Logical(ExprS2 left, TokenS2 operator, ExprS2 right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }

        final ExprS2 left;
        final TokenS2 operator;
        final ExprS2 right;
    }

    static class Set extends ExprS2 {
        Set(ExprS2 object, TokenS2 name, ExprS2 value) {
            this.object = object;
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSetExpr(this);
        }

        final ExprS2 object;
        final TokenS2 name;
        final ExprS2 value;
    }

    static class Super extends ExprS2 {
        Super(TokenS2 keyword, TokenS2 method) {
            this.keyword = keyword;
            this.method = method;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitSuperExpr(this);
        }

        final TokenS2 keyword;
        final TokenS2 method;
    }

    static class This extends ExprS2 {
        This(TokenS2 keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitThisExpr(this);
        }

        final TokenS2 keyword;
    }

    static class Import extends ExprS2 {
        Import(TokenS2 keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitImportExpr(this);
        }

        final TokenS2 keyword;
    }

    static class Package extends ExprS2 {
        Package(TokenS2 keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPackageExpr(this);
        }

        final TokenS2 keyword;
    }

    static class Dot extends ExprS2 {
        Dot(TokenS2 keyword) {
            this.keyword = keyword;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitDotExpr(this);
        }

        final TokenS2 keyword;
    }

    static class Unary extends ExprS2 {
        Unary(TokenS2 operator, ExprS2 right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final TokenS2 operator;
        final ExprS2 right;
    }

    static class Variable extends ExprS2 {
        Variable(TokenS2 name) {
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        final TokenS2 name;
    }



    abstract <R> R accept(Visitor<R> visitor);
}
