import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Parser {
  private static class ParseError extends RuntimeException {}

  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }
 //Parsing Expressions parse < Statements and State parse
/*   Expr parse() {
    try {
      return expression();
    } catch (ParseError error) {
      return null;
    }
  } */


  List<Stmt> parse() {
    List<Stmt> statements = new ArrayList<>();
    while (!isAtEnd()) {
      statements.add(declaration());
    }

    return statements;
  } 

  private Expr expression() {
    return assignment();
  }

  private Stmt declaration() {
    try {
      if (match(TokenType.PUBLIC))  accessModifierDeclaration();
      if (match(TokenType.PRIVATE))  accessModifierDeclaration();
      if (match(TokenType.PROTECTED))  accessModifierDeclaration();
      if (match(TokenType.CLASS)) return classDeclaration();
      if (match(TokenType.VAR)) return varDeclaration();
      if (match(TokenType.INTERFACE)) return interfaceDeclaration();
      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }
  private Stmt classDeclaration() {
    System.out.println("reached classDeclaration");   //TEST
    Token name = consume(TokenType.IDENTIFIER, "Expect class name.");

    Expr.Variable superclass = null;
    if (match(TokenType.LESS)) {
      consume(TokenType.IDENTIFIER, "Expect superclass name.");
      superclass = new Expr.Variable(previous());
    }

    consume(TokenType.LEFT_BRACE, "Expect '{' before class body.");

    List<Stmt.Function> methods = new ArrayList<>();
    while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
      methods.add(function("method"));
    }

    consume(TokenType.RIGHT_BRACE, "Expect '}' after class body.");


    return new Stmt.Class(name, superclass, methods);

  }

  private Stmt interfaceDeclaration() {
    System.out.println("reached interfaceDeclaration");  //TEST
    Token name = consume(TokenType.IDENTIFIER, "Expect interface name.");

    consume(TokenType.LEFT_BRACE, "Expect '{' before body.");

    List<Stmt.Function> methods = new ArrayList<>();
    while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
      methods.add(function("method"));
    }

    consume(TokenType.RIGHT_BRACE, "Expect '}' after class body.");


    return new Stmt.Interface(name, methods);

  }

  private Stmt varDeclaration() {
    System.out.println("reached varDeclaration");  //TEST
    Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");

    Expr initializer = null;
    if (match(TokenType.EQUAL)) {
      initializer = expression();
    }

    consume(TokenType.SEMICOLON, "Expect ';' after variable declaration.");
    return new Stmt.Var(name, initializer);
  }

   private void accessModifierDeclaration() {
     System.out.println("reached accessModifierDeclaration");
    
    /* Token name;
    //public, private, protected
    Expr modifier = null;
    if (match(TokenType.PUBLIC)) {
      name = consume(TokenType.PUBLIC, "Expect access modifier");
      modifier = expression();
      return Expr.Public(name,modifier); 
    }
    else if (match(TokenType.PRIVATE)) {
      name = consume(TokenType.PRIVATE, "Expect access modifier");
      modifier = expression();
      return Expr.Private(modifier);
    }
    else if (match(TokenType.PROTECTED)) {
      name = consume(TokenType.PROTECTED, "Expect access modifier");
      modifier = expression();
      return Expr.Protected(modifier);
    } */
  }
  /*
  private Stmt nonAccessModifierDeclaration() {
    System.out.println("reached nonAccessModifierDeclaration");
    //abstract, final
  }
 */
  private Stmt statement() {

    if (match(TokenType.FOR)) return forStatement();

    if (match(TokenType.IF)) return ifStatement();

    if (match(TokenType.PRINT)) return printStatement();

    if (match(TokenType.PRINTLN)) return printlnStatement();

    if (match(TokenType.RETURN)) return returnStatement();

    if (match(TokenType.WHILE)) return whileStatement();

    if (match(TokenType.DO)) return doStatement();

    if (match(TokenType.SWITCH)) return switchStatement();

    if (match(TokenType.TRY)) return tryStatement();

    if (match(TokenType.LEFT_BRACE)) return new Stmt.Block(block());

    if (match(TokenType.THROW)) return throwStatement();

    if (match(TokenType.ENUM)) return enumStatement();    

    if (match(TokenType.EXTENDS)) return extendsStatement();

    return expressionStatement();
  }

  private Stmt forStatement() {
    consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.");

    Stmt initializer;
    if (match(TokenType.SEMICOLON)) {
      initializer = null;
    } else if (match(TokenType.VAR)) {
      initializer = varDeclaration();
    } else {
      initializer = expressionStatement();
    }

    Expr condition = null;
    if (!check(TokenType.SEMICOLON)) {
      condition = expression();
    }
    consume(TokenType.SEMICOLON, "Expect ';' after loop condition.");

    Expr increment = null;
    if (!check(TokenType.RIGHT_PAREN)) {
      increment = expression();
    }
    consume(TokenType.RIGHT_PAREN, "Expect ')' after for clauses.");

    Stmt body = statement();


    if (increment != null) {
      body = new Stmt.Block(
          Arrays.asList(
              body,
              new Stmt.Expression(increment)));
    }

    if (condition == null) condition = new Expr.Literal(true);
    body = new Stmt.While(condition, body);


    if (initializer != null) {
      body = new Stmt.Block(Arrays.asList(initializer, body));
    }


    return body;

  }

  private Stmt ifStatement() {
    consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
    Expr condition = expression();
    consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition."); // [parens]

    Stmt thenBranch = statement();
    Stmt elseBranch = null;
    if (match(TokenType.ELSE)) {
      elseBranch = statement();
    }

    return new Stmt.If(condition, thenBranch, elseBranch);
  }

  private Stmt printStatement() {
    Expr value = expression();
    consume(TokenType.SEMICOLON, "Expect ';' after value.");
    return new Stmt.Print(value);
  }

  private Stmt printlnStatement() {
    Expr value = expression();
    consume(TokenType.PRINTLN, "Expect '(' after value.");
    return new Stmt.println(value);
  }

  private Stmt returnStatement() {
    Token keyword = previous();
    Expr value = null;
    if (!check(TokenType.SEMICOLON)) {
      value = expression();
    }

    consume(TokenType.SEMICOLON, "Expect ';' after return value.");
    return new Stmt.Return(keyword, value);
  }

  private Stmt whileStatement() {
    consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
    Expr condition = expression();
    consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");
    Stmt body = statement();

    return new Stmt.While(condition, body);
  }

  private Stmt doStatement() {
    consume(TokenType.LEFT_BRACE, "Expect '{' after expression");
    Expr condition = expression();
    consume(TokenType.RIGHT_BRACE, "Expect '}' after condition");
    Stmt body = statement();

    return new Stmt.Do(condition, body);
  }

  private Stmt switchStatement() {
    consume(TokenType.LEFT_PAREN, "Expect '(' after expression");
    Expr condition = expression();
    consume(TokenType.RIGHT_PAREN, "Expect ')' after condition");
    consume(TokenType.LEFT_BRACE, "Expect '{' before body");
    consume(TokenType.CASE, "Expect 'case'");
    Expr body = expression();
    
    Stmt caseBranch = statement();
    Stmt defaultBranch = null;
    if (match(TokenType.CASE)) {
      caseBranch = statement();
    }
    if (match(TokenType.DEFAULT)) {
      defaultBranch = statement();
    }
    return new Stmt.Switch(condition, body, caseBranch, defaultBranch);
  }

  private Stmt tryStatement() {
    consume(TokenType.LEFT_BRACE, "Expect '{' after expression");
    Expr condition = expression();
    consume(TokenType.RIGHT_BRACE, "Expect '}' after condition");    

    return new Stmt.Try(condition);
  }   
  

  private Stmt throwStatement() {    
    consume(TokenType.LEFT_BRACE, "Expect '{' after expression");
    Token keyword = previous();
    consume(TokenType.RIGHT_BRACE, "Expect '}' after condition");

    return new Stmt.Throw(keyword);    
  }

  private Stmt enumStatement() {
    consume(TokenType.LEFT_BRACE, "Expect '{' after identifier");
    Token keyword = previous();
    Stmt body = statement();

    return new Stmt.Enum(keyword,body);
  }
  private Stmt extendsStatement() {
    consume(TokenType.CLASS, "Expect class after keyword");
    Token keyword = previous();
    
    return new Stmt.Extends(keyword);
  }


  private Stmt expressionStatement() {
    Expr expr = expression();
    consume(TokenType.SEMICOLON, "Expect ';' after expression.");
    return new Stmt.Expression(expr);
  }

  private Stmt.Function function(String kind) {
    System.out.println("reached function check"); //TEST
    Token name = consume(TokenType.IDENTIFIER, "Expect " + kind + " name.");

    consume(TokenType.LEFT_PAREN, "Expect '(' after " + kind + " name.");
    List<Token> parameters = new ArrayList<>();
    if (!check(TokenType.RIGHT_PAREN)) {
      do {
        if (parameters.size() >= 255) {
          error(peek(), "Can't have more than 255 parameters.");
        }

        parameters.add(
            consume(TokenType.IDENTIFIER, "Expect parameter name."));
      } while (match(TokenType.COMMA));
    }
    consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");


    consume(TokenType.LEFT_BRACE, "Expect '{' before " + kind + " body.");
    List<Stmt> body = block();
    return new Stmt.Function(name, parameters, body);

  }

  private List<Stmt> block() {
    List<Stmt> statements = new ArrayList<>();

    while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
      statements.add(declaration());
    }

    consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");
    return statements;
  }

  private Expr assignment() {
    Expr expr = or();

    if (match(TokenType.EQUAL)) {
      Token equals = previous();
      Expr value = assignment();

      if (expr instanceof Expr.Variable) {
        Token name = ((Expr.Variable)expr).name;
        return new Expr.Assign(name, value);

      } else if (expr instanceof Expr.Get) {
        Expr.Get get = (Expr.Get)expr;
        return new Expr.Set(get.object, get.name, value);
      }

      error(equals, "Invalid assignment target."); // [no-throw]
    }

    return expr;
  }
//< Statements and State parse-assignment
//> Control Flow or
  private Expr or() {
    Expr expr = and();

    while (match(TokenType.OR)) {
      Token operator = previous();
      Expr right = and();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }
//< Control Flow or
//> Control Flow and
  private Expr and() {
    Expr expr = equality();

    while (match(TokenType.AND)) {
      Token operator = previous();
      Expr right = equality();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }
//< Control Flow and
//> equality
  private Expr equality() {
    Expr expr = comparison();

    while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }
//< equality
//> comparison
  private Expr comparison() {
    Expr expr = term();

    while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
      Token operator = previous();
      Expr right = term();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }
//< comparison
//> term
  private Expr term() {
    Expr expr = factor();

    while (match(TokenType.MINUS, TokenType.PLUS)) {
      Token operator = previous();
      Expr right = factor();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }
//< term
//> factor
  private Expr factor() {
    Expr expr = unary();

    while (match(TokenType.SLASH, TokenType.STAR)) {
      Token operator = previous();
      Expr right = unary();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }
//< factor
//> unary
  private Expr unary() {
    if (match(TokenType.BANG, TokenType.MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }

/* Parsing Expressions unary < Functions unary-call
    return primary();
*/
//> Functions unary-call
    return call();
//< Functions unary-call
  }
//< unary
//> Functions finish-call
  private Expr finishCall(Expr callee) {
    List<Expr> arguments = new ArrayList<>();
    if (!check(TokenType.RIGHT_PAREN)) {
      do {
//> check-max-arity
        if (arguments.size() >= 255) {
          error(peek(), "Can't have more than 255 arguments.");
        }
//< check-max-arity
        arguments.add(expression());
      } while (match(TokenType.COMMA));
    }

    Token paren = consume(TokenType.RIGHT_PAREN,
                          "Expect ')' after arguments.");

    return new Expr.Call(callee, paren, arguments);
  }
//< Functions finish-call
//> Functions call
  private Expr call() {
    Expr expr = primary();

    while (true) { // [while-true]
      if (match(TokenType.LEFT_PAREN)) {
        expr = finishCall(expr);
//> Classes parse-property
      } else if (match(TokenType.DOT)) {
        Token name = consume(TokenType.IDENTIFIER,
            "Expect property name after '.'.");
        expr = new Expr.Get(expr, name);
//< Classes parse-property
      } else {
        break;
      }
    }

    return expr;
  }
//< Functions call
//> primary
  private Expr primary() {
    if (match(TokenType.FALSE)) return new Expr.Literal(false);
    if (match(TokenType.TRUE)) return new Expr.Literal(true);
    if (match(TokenType.NIL)) return new Expr.Literal(null);

    if (match(TokenType.NUMBER, TokenType.STRING)) {
      return new Expr.Literal(previous().literal);
    }
//> Inheritance parse-super

    if (match(TokenType.SUPER)) {
      Token keyword = previous();
      consume(TokenType.DOT, "Expect '.' after 'super'.");
      Token method = consume(TokenType.IDENTIFIER,
          "Expect superclass method name.");
      return new Expr.Super(keyword, method);
    }
//< Inheritance parse-super
//> Classes parse-this

    if (match(TokenType.THIS)) return new Expr.This(previous());


    if (match(TokenType.IDENTIFIER)) {
      return new Expr.Variable(previous());
    }


    if (match(TokenType.LEFT_PAREN)) {
      Expr expr = expression();
      consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }


    throw error(peek(), "Expect expression.");

  }

  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }

  private Token consume(TokenType type, String message) {
    if (check(type)) return advance();

    throw error(peek(), message);
  }

  private boolean check(TokenType type) {
    if (isAtEnd()) return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd()) current++;
    return previous();
  }

  private boolean isAtEnd() {
    return peek().type == TokenType.EOF;
  }

  private Token peek() {
    return tokens.get(current);
  }

  private Token previous() {
    return tokens.get(current - 1);
  }

  private ParseError error(Token token, String message) {    
    return new ParseError();
  }

  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type == TokenType.SEMICOLON) return;

      switch (peek().type) {
        case CLASS:        
        case VAR:
        case FOR:
        case IF:
        case WHILE:
        case PRINT:
        case RETURN:
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
        case PUBLIC:
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
        case NIL:
        case NUMBER:
        case OR:
        case PRINTLN:
        case PRIVATE:
        case PROTECTED:
        case SEMICOLON:
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
