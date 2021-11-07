  import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ParserS2 {
    private static class ParseError extends RuntimeException {}

    private final List<TokenS2> tokens;
    private int current = 0;

    private final List<TokenTypeS2> validMod = new ArrayList<TokenTypeS2>(Arrays.asList(TokenTypeS2.PUBLIC, TokenTypeS2.PRIVATE, TokenTypeS2.ABSTRACT, TokenTypeS2.STATIC, TokenTypeS2.ABSTRACT, TokenTypeS2.FINAL, TokenTypeS2.PROTECTED));
    private final List<TokenTypeS2> validRet= new ArrayList<TokenTypeS2>(Arrays.asList(TokenTypeS2.VOID, TokenTypeS2.INT, TokenTypeS2.STRING, TokenTypeS2.CHAR, TokenTypeS2.FLOAT, TokenTypeS2.DOUBLE, TokenTypeS2.BOOLEAN));



    ParserS2(List<TokenS2> tokens) {
        this.tokens = tokens;
    }
    

    List<StmtS2> parse() {
        List<StmtS2> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    private ExprS2 expression() {
        return assignment();
    }

    public List<TokenS2> getModifiers()
    {
        List<TokenS2> modifiers = new ArrayList<TokenS2>();
        while(validMod.contains(peek().type) || validRet.contains(peek().type)){
            //Token modifier = consume(peek().type, "Expect access modifier");
            modifiers.add(tokens.get(current));
            advance();
        }
        return modifiers;
    }

    public boolean chekModifier()
    {
        List<TokenS2> modifiers = new ArrayList<TokenS2>();
        if(validMod.contains(peek().type) || validRet.contains(peek().type)){
            return true;
        }
        return false;
    }

    private StmtS2 modifiersDeclaration() {

        List<TokenS2> modifiers = new ArrayList<TokenS2>();
        modifiers = getModifiers();
        StringBuilder builder = new StringBuilder();
        for (TokenS2 m : modifiers) {
            if (modifiers != modifiers.get(0)) builder.append(" ");
            builder.append(m.lexeme);
        }

        return new StmtS2.Modifiers(modifiers);
    }

    private StmtS2 declaration() {
        try {
            //get all modifiers
//            System.out.println("Token " + peek().type + "\tLexeme " + peek().lexeme);
            if(chekModifier())
            {
                return modifiersDeclaration();
            }

            if (match(TokenTypeS2.CLASS))
            {
                return classDeclaration();
            }
            if (match(TokenTypeS2.INTERFACE)) {
                return interfaceDeclaration();
            }
            //If we have modifiers but not class or interface type then it either a variable or function declaration
            if (peek().type == TokenTypeS2.IDENTIFIER) {
                // if IDENTIFIER -> '(' then we go to function declaration else it will be a variable declaration
                if(next().type == TokenTypeS2.LEFT_PAREN){
                    return function("function");
                }else {
                    return varDeclaration();
                }
            }

            return statement();

        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }



    private StmtS2 classDeclaration() {
        TokenS2 name = consume(TokenTypeS2.IDENTIFIER, "Expect class name.");

        ExprS2.Variable superclass = null;
        if (match(TokenTypeS2.LESS)) {
            consume(TokenTypeS2.IDENTIFIER, "Expect superclass name.");
            superclass = new ExprS2.Variable(previous());
        }


        consume(TokenTypeS2.LEFT_BRACE, "Expect '{' before class body.");


        List<StmtS2.Function> methods = new ArrayList<>();
        List<StmtS2> fields = new ArrayList<>();
        while (!check(TokenTypeS2.RIGHT_BRACE) && !isAtEnd()) {
            fields.add(declaration());
        }


        consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after class body.");


        return new StmtS2.Class(name, superclass, fields);

    }


    private StmtS2 interfaceDeclaration() {
//        System.out.println("interfaceDeclaration");   //TEST
        TokenS2 name = consume(TokenTypeS2.IDENTIFIER, "Expect interface name.");

        consume(TokenTypeS2.LEFT_BRACE, "Expect '{' before body.");

        List<StmtS2.Function> methods = new ArrayList<>();
        while (!check(TokenTypeS2.RIGHT_BRACE) && !isAtEnd()) {
            methods.add(function("method"));
        }
        consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after class body.");
        return new StmtS2.Interface(name, methods);
    }

    private StmtS2 varDeclaration() {
//        System.out.println("varDeclaration");   //TEST
//        Token type = peek();
//        System.out.println(peek());
        TokenS2 name = consume(TokenTypeS2.IDENTIFIER, "Expect variable name.");

        ExprS2 initializer = null;
        if (match(TokenTypeS2.EQUAL)) {
            initializer = expression();
        }

        consume(TokenTypeS2.SEMICOLON, "Expect ';' after variable declaration.");
        return new StmtS2.Var(name, initializer);
    }

    private StmtS2 statement() {

        if (match(TokenTypeS2.FOR)) {
            return forStatement();
        }

        if (match(TokenTypeS2.IF)) return ifStatement();
        if (match(TokenTypeS2.SYSTEM)) {
            if (match(TokenTypeS2.DOT)) {
                if (match(TokenTypeS2.OUT)) {
                    if (match(TokenTypeS2.DOT)) {
                        if (match(TokenTypeS2.PRINT)) return printStatement();
                        if (match(TokenTypeS2.PRINTLN)) return printlnStatement();
                    }

                }
            }
        }

        if (match(TokenTypeS2.RETURN)) return returnStatement();

        if (match(TokenTypeS2.WHILE)) return whileStatement();

        if (match(TokenTypeS2.DO)) return doStatement();

        if (match(TokenTypeS2.SWITCH)) return switchStatement();

        if (match(TokenTypeS2.TRY)) return tryStatement();

        if (match(TokenTypeS2.CATCH)) return catchStatement();

        if (match(TokenTypeS2.FINALLY)) return finallyStatement();

        if (match(TokenTypeS2.LEFT_BRACE)) return new StmtS2.Block(block());

        if (match(TokenTypeS2.THROW)) return throwStatement();

        if (match(TokenTypeS2.ENUM)) return enumStatement();

        if (match(TokenTypeS2.CONTINUE)) return continueStatement();

        if (match(TokenTypeS2.BREAK)) return breakStatement();

        return expressionStatement();
    }

    private StmtS2 forStatement() {
        consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after 'for'.");

        StmtS2 initializer;
        if (match(TokenTypeS2.SEMICOLON)) {
            initializer = null;
        } else if (peek().type == TokenTypeS2.INT) {
            TokenS2 type = consume(TokenTypeS2.INT, "Expect Type.");
            List<TokenS2> l = new ArrayList<TokenS2>();
            l.add(type);
            initializer = varDeclaration();
        } else {
            initializer = expressionStatement();
        }

        ExprS2 condition = null;
        if (!check(TokenTypeS2.SEMICOLON)) {
            condition = expression();
        }
        consume(TokenTypeS2.SEMICOLON, "Expect ';' after loop condition.");

        ExprS2 increment = null;
        if (!check(TokenTypeS2.RIGHT_PAREN)) {
            increment = expression(); 
        }
        consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after for clauses.");

        StmtS2 body = statement();

        return new StmtS2.For(initializer, condition, increment , body);

        // if (increment != null) {
        //     body = new StmtS2.Block(Arrays.asList(body, new StmtS2.Expression(increment)));
        // }

        // if (condition == null) condition = new ExprS2.Literal(true);
        // body = new StmtS2.For(condition, body);


        // if (initializer != null) {
        //     body = new StmtS2.Block(Arrays.asList(initializer, body));
        // }
        // return body;
    }

    private StmtS2 ifStatement() {
        consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after 'if'.");
        ExprS2 condition = expression();
        consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after if condition."); 

        StmtS2 thenBranch = statement();
        StmtS2 elseBranch = null;
        if (match(TokenTypeS2.ELSE)) {
            elseBranch = statement();
        }

        return new StmtS2.If(condition, thenBranch, elseBranch);
    }

    private StmtS2 printStatement() {
        consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after print statement.");
        ExprS2 value = expression();
        consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after print statement.");
        consume(TokenTypeS2.SEMICOLON, "Expect ';' after value.");
        return new StmtS2.Print(value);
    }

    private StmtS2 printlnStatement() {
        ExprS2 value = expression();
        consume(TokenTypeS2.PRINTLN, "Expect '(' after value.");
        return new StmtS2.println(value);
    }

    private StmtS2 returnStatement() {
        TokenS2 keyword = previous();
        ExprS2 value = null;
        if (!check(TokenTypeS2.SEMICOLON)) {
            value = expression();
        }

        consume(TokenTypeS2.SEMICOLON, "Expect ';' after return value.");
        return new StmtS2.Return(keyword, value);
    }

    private StmtS2 continueStatement() {
      TokenS2 keyword = previous();      
      consume(TokenTypeS2.SEMICOLON, "Expect ';' after continue");
      return new StmtS2.Continue(keyword);
    }

    private StmtS2 breakStatement() {
      TokenS2 keyword = previous();
      consume(TokenTypeS2.SEMICOLON, "Expect ';' after continue");
      return new StmtS2.Break(keyword);
    }

    private StmtS2 whileStatement() {
        consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after 'while'.");
        ExprS2 condition = expression();
        consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after condition.");
        StmtS2 body = statement();

        return new StmtS2.While(condition, body);
    }

    private StmtS2 doStatement() {
        consume(TokenTypeS2.LEFT_BRACE, "Expect '{' after expression");
        ExprS2 condition = expression();
        consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after condition");
        StmtS2 body = statement();

        return new StmtS2.Do(condition, body);
    }

    private StmtS2 switchStatement() {
        consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after expression");
        ExprS2 condition = expression();
        consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after condition");
        consume(TokenTypeS2.LEFT_BRACE, "Expect '{' before body");
        consume(TokenTypeS2.CASE, "Expect 'case'");
        ExprS2 body = expression();

        StmtS2 caseBranch = statement();
        StmtS2 defaultBranch = null;
        if (match(TokenTypeS2.CASE)) {
            caseBranch = statement();
        }
        if (match(TokenTypeS2.DEFAULT)) {
            defaultBranch = statement();
        }
        return new StmtS2.Switch(condition, body, caseBranch, defaultBranch);
    }

    private StmtS2 tryStatement() {
        TokenS2 name = previous();
        consume(TokenTypeS2.LEFT_BRACE, "Expect '{' after expression");
        StmtS2 body = statement();
        consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after condition");

        return new StmtS2.Try(name,body);
    }

      private StmtS2 catchStatement() {
      TokenS2 name = previous();
      consume(TokenTypeS2.LEFT_PAREN, "Expect '('");
       List<TokenS2> parameters = new ArrayList<>();
        List<TokenS2> parametersTypes = new ArrayList<>();
        if (!check(TokenTypeS2.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Can't have more than 255 parameters.");
                }
                if(match(TokenTypeS2.EXCEPTION))
                parametersTypes.add(previous());
                            
                //get param types
                parameters.add(
                        consume(TokenTypeS2.IDENTIFIER, "Expect parameter name."));
            } while (match(TokenTypeS2.COMMA));
        }
      consume(TokenTypeS2.LEFT_BRACE, "Expect '{' after expression");
      StmtS2 body = statement();
      consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after condition");

      return new StmtS2.Catch(name,body, parametersTypes, parameters);
  }

      private StmtS2 finallyStatement() {
        TokenS2 name = previous();
        consume(TokenTypeS2.LEFT_BRACE, "Expect '{' after expression");
        StmtS2 body = statement();
        consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after condition");

        return new StmtS2.Finally(name,body);
    }


    private StmtS2 throwStatement() {
        consume(TokenTypeS2.LEFT_BRACE, "Expect '{' after expression");
        TokenS2 keyword = previous();
        consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after condition");

        return new StmtS2.Throw(keyword);
    }

    private StmtS2 enumStatement() {
        consume(TokenTypeS2.LEFT_BRACE, "Expect '{' after identifier");
        TokenS2 keyword = previous();
        StmtS2 body = statement();

        return new StmtS2.Enum(keyword,body);
    }



    private StmtS2 expressionStatement() {
        ExprS2 expr = expression();
        consume(TokenTypeS2.SEMICOLON, "Expect ';' after expression.");
        return new StmtS2.Expression(expr);
    }

    private StmtS2.Function function(String kind) {

        TokenS2 name = consume(TokenTypeS2.IDENTIFIER, "Expect " + kind + " name.");

        consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after " + kind + " name.");
        List<TokenS2> parameters = new ArrayList<>();
        List<TokenS2> parametersTypes = new ArrayList<>();
        if (!check(TokenTypeS2.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= 255) {
                    error(peek(), "Can't have more than 255 parameters.");
                }
                if(validRet.contains(peek().type)){
                    parametersTypes.add(consume(peek().type, "Expect parameter type"));
                }
                //get param types
                parameters.add(
                        consume(TokenTypeS2.IDENTIFIER, "Expect parameter name."));
            } while (match(TokenTypeS2.COMMA));
        }
        consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after parameters.");


        consume(TokenTypeS2.LEFT_BRACE, "Expect '{' before " + kind + " body.");
        List<StmtS2> body = block();
        return new StmtS2.Function(name, parametersTypes, parameters, body);

    }

    private List<StmtS2> block() {
        List<StmtS2> statements = new ArrayList<>();


        while (!check(TokenTypeS2.RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private ExprS2 assignment() {
        ExprS2 expr = or();

        if (match(TokenTypeS2.EQUAL)) {
            TokenS2 equals = previous();
            ExprS2 value = assignment();

            if (expr instanceof ExprS2.Variable) {
                TokenS2 name = ((ExprS2.Variable)expr).name;
                return new ExprS2.Assign(name, value);

            } else if (expr instanceof ExprS2.Get) {
                ExprS2.Get get = (ExprS2.Get)expr;
                return new ExprS2.Set(get.object, get.name, value);
            }

            error(equals, "Invalid assignment target."); 
        }

        return expr;
    }

    private ExprS2 or() {
        ExprS2 expr = and();

        while (match(TokenTypeS2.OR)) {
            TokenS2 operator = previous();
            ExprS2 right = and();
            expr = new ExprS2.Logical(expr, operator, right);
        }

        return expr;
    }

    private ExprS2 and() {
        ExprS2 expr = equality();

        while (match(TokenTypeS2.AND)) {
            TokenS2 operator = previous();
            ExprS2 right = equality();
            expr = new ExprS2.Logical(expr, operator, right);
            
        }
        
        return expr;
    }

    private ExprS2 equality() {
        ExprS2 expr = comparison();

        while (match(TokenTypeS2.BANG_EQUAL, TokenTypeS2.EQUAL_EQUAL)) {
            TokenS2 operator = previous();
            ExprS2 right = comparison();
            expr = new ExprS2.Binary(expr, operator, right);
        }

        return expr;
    }

    private ExprS2 comparison() {
        ExprS2 expr = term();

        while (match(TokenTypeS2.GREATER, TokenTypeS2.GREATER_EQUAL, TokenTypeS2.LESS, TokenTypeS2.LESS_EQUAL)) {
            TokenS2 operator = previous();
            ExprS2 right = term();
            expr = new ExprS2.Binary(expr, operator, right);
        }

        return expr;
    }

    private ExprS2 term() {
        ExprS2 expr = factor();

        while (match(TokenTypeS2.MINUS, TokenTypeS2.PLUS)) {
            TokenS2 operator = previous();
            ExprS2 right = factor();
            expr = new ExprS2.Binary(expr, operator, right);
        }

        return expr;
    }

    private ExprS2 factor() {
        ExprS2 expr = unary();

        while (match(TokenTypeS2.SLASH, TokenTypeS2.STAR)) {
            TokenS2 operator = previous();
            ExprS2 right = unary();
            expr = new ExprS2.Binary(expr, operator, right);
        }

        return expr;
    }

    private ExprS2 unary() {
        if (match(TokenTypeS2.BANG, TokenTypeS2.MINUS, TokenTypeS2.PLUS)) {                   
            TokenS2 operator = previous();
            ExprS2 right = unary();
            return new ExprS2.Unary(operator, right);
        }       


        return call();

    }

    private ExprS2 finishCall(ExprS2 callee) {
        List<ExprS2> arguments = new ArrayList<>();
        if (!check(TokenTypeS2.RIGHT_PAREN)) {
            do {

                if (arguments.size() >= 255) {
                    error(peek(), "Can't have more than 255 arguments.");
                }

                arguments.add(expression());
            } while (match(TokenTypeS2.COMMA));
        }

        TokenS2 paren = consume(TokenTypeS2.RIGHT_PAREN,
                "Expect ')' after arguments.");

        return new ExprS2.Call(callee, paren, arguments);
    }

    private ExprS2 call() {
        ExprS2 expr = primary();

        while (true) {
            if (match(TokenTypeS2.LEFT_PAREN)) {
                expr = finishCall(expr);

            } else if (match(TokenTypeS2.DOT)) {
                TokenS2 name = consume(TokenTypeS2.IDENTIFIER,
                        "Expect property name after '.'.");
                expr = new ExprS2.Get(expr, name);

            } else {
                break;
            }
        }

        return expr;
    }

    private ExprS2 primary() {
        if (match(TokenTypeS2.FALSE)) return new ExprS2.Literal(false);
        if (match(TokenTypeS2.TRUE)) return new ExprS2.Literal(true);
        if (match(TokenTypeS2.NULL)) return new ExprS2.Literal(null);

        if (match(TokenTypeS2.NUMBER, TokenTypeS2.STRING)) {
            return new ExprS2.Literal(previous().literal);
        }


        if (match(TokenTypeS2.SUPER)) {
            TokenS2 keyword = previous();
            consume(TokenTypeS2.DOT, "Expect '.' after 'super'.");
            TokenS2 method = consume(TokenTypeS2.IDENTIFIER,
                    "Expect superclass method name.");
            return new ExprS2.Super(keyword, method);
        }


        if (match(TokenTypeS2.THIS)) return new ExprS2.This(previous());
        if (match(TokenTypeS2.IMPORT)) return new ExprS2.Import(previous());
        if (match(TokenTypeS2.PACKAGE)) return new ExprS2.Package(previous());

        if (match(TokenTypeS2.DOT)) return new ExprS2.Dot(previous());


        if (match(TokenTypeS2.IDENTIFIER)) {
            return new ExprS2.Variable(previous());
        }


        if (match(TokenTypeS2.LEFT_PAREN)) {
            ExprS2 expr = expression();
            consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after expression.");
            return new ExprS2.Grouping(expr);
        }


        throw error(peek(), "Expect expression.");

    }

    private boolean match(TokenTypeS2... types) {
        for (TokenTypeS2 type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private TokenS2 consume(TokenTypeS2 type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private boolean check(TokenTypeS2 type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private TokenS2 advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenTypeS2.EOF;
    }

    private TokenS2 peek() {
        return tokens.get(current);
    }

    private TokenS2 previous() {
        return tokens.get(current - 1);
    }
    private TokenS2 next() {
        return tokens.get(current + 1);
    }
    private TokenS2 nextnext() {
        return tokens.get(current + 2);
    }

    private ParseError error(TokenS2 token, String message) {
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == TokenTypeS2.SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                case PUBLIC:
                case ABSTRACT:
                case ARGS:
                case AND:
                case BANG:
                case BANG_EQUAL:
                case BOOLEAN:
                case BREAK:
                case BYTE:
                case CASE:
                case CATCH:
                case CHAR:
                case COMMA:
                case CONST:
                case CONTINUE:
                case DEFAULT:
                case DIVEQUAL:
                case DO:
                case DOT:
                case DOUBLE:
                case ELSE:
                case ENUM:
                case EOF:
                case EQUAL:
                case EQUAL_EQUAL:
                case EXTENDS:
                case RIGHT_BRACE:
                case RIGHT_BRACKET:
                case RIGHT_PAREN:
                case LEFT_PAREN:
                case FALSE:
                case FINAL:
                case FINALLY:
                case FLOAT:
                case GREATER:
                case GREATER_EQUAL:
                case IDENTIFIER:
                case INT:
                case INTERFACE:
                case LEFT_BRACE:
                case LEFT_BRACKET:
                case OUT:
                case PLUS:
                case PLUSEQUAL:
                case LESS:
                case LESS_EQUAL:
                case LONG:
                case MAIN:
                case MINUS:
                case MINUSEQUAL:
                case THROW:
                case TRUE:
                case TRY:
                case MULTEQUAL:
                case NULL:
                case NUMBER:
                case OR:
                case PRINTLN:
                case PRIVATE:
                case PROTECTED:
                case SHORT:
                case SLASH:
                case STAR:
                case STATIC:
                case STRING:
                case SUPER:
                case SWITCH:
                case SYSTEM:
                case THIS:
                case VOID:
                case VOLATILE:
                    return;
            }

            advance();
        }
    }

}
