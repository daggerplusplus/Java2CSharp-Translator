import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Parser {
  private static class ParseError extends RuntimeException {
  }

  private final List<Token> tokens;
  private int current = 0;
  private final List<TokenType> validRet = new ArrayList<TokenType>(Arrays.asList(TokenType.VOID, TokenType.INT,
      TokenType.STRING, TokenType.CHAR, TokenType.FLOAT, TokenType.DOUBLE, TokenType.BOOLEAN));

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

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
      if (match(TokenType.CLASS)) {
        return classDeclaration();
      }
      if (match(TokenType.INTERFACE)) {
        return interfaceDeclaration();
      }
      // If we have modifiers but not class or interface type then it either a
      // variable or function declaration
      if (peek().type == TokenType.IDENTIFIER || peek().type == TokenType.MAIN) {
        // if IDENTIFIER -> '(' then we go to function declaration else it will be a
        // variable declaration
        if (next().type == TokenType.LEFT_PAREN) {
          return function("function");
        } else if (next().type == TokenType.EQUAL || next().type == TokenType.SEMICOLON) { 
          return varDeclaration();
        }
      }

      return statement();

    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  private Stmt classDeclaration() {
    Token name = consume(TokenType.IDENTIFIER, "Expect class name.");

    List<Expr.Variable> superclass = new ArrayList<>();
    if (match(TokenType.EXTENDS)) {
      while (check(TokenType.IDENTIFIER) || check(TokenType.COMMA)) {
        if (check(TokenType.COMMA)) {
          consume(TokenType.COMMA, "");
        }
        consume(TokenType.IDENTIFIER, "Expect superclass name.");
        superclass.add(new Expr.Variable(previous()));
      }
    }

    List<Expr.Variable> implementinterface = new ArrayList<>();
    if (match(TokenType.IMPLEMENTS)) {
      while (!check(TokenType.LEFT_BRACE)) {
        consume(TokenType.IDENTIFIER, "Expect interface name.");
        implementinterface.add(new Expr.Variable(previous()));
        if (check(TokenType.COMMA)) {
          consume(TokenType.COMMA, "");
        }
      }
    }
    consume(TokenType.LEFT_BRACE, "Expect '{' before class body.");

    List<Stmt> fields = block();

    return new Stmt.Class(name, superclass, implementinterface, fields);

  }

  private Stmt interfaceDeclaration() {
    Token name = consume(TokenType.IDENTIFIER, "Expect interface name.");

    List<Expr.Variable> extender = new ArrayList<>();
        if (match(TokenType.EXTENDS)) {
            while(check(TokenType.IDENTIFIER) || check(TokenType.COMMA)){
                if(check(TokenType.COMMA)){
                    consume(TokenType.COMMA, "");
                }
                consume(TokenType.IDENTIFIER, "Expect superclass name.");
                extender.add(new Expr.Variable(previous()));
            }
        }

    consume(TokenType.LEFT_BRACE, "Expect '{' before body.");  
    List<Stmt.InterfaceFunction> methods = new ArrayList<>();
    List<Stmt> mods = new ArrayList<>();
    while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
      mods.add(declaration());
      methods.add(Ifunction("method"));
    }
    consume(TokenType.RIGHT_BRACE, "Expect '}' after class body.");
    return new Stmt.Interface(name, extender, methods, mods);
  }

  private Stmt varDeclaration() {
    Token name = consume(TokenType.IDENTIFIER, "Expect variable name.");
    List<Expr> initializer = new ArrayList<>();
    if (match(TokenType.EQUAL)) {
      while (!peek().type.equals(TokenType.SEMICOLON)) {
        Expr x = expression();
        initializer.add(x);
      }      
    }   
    return new Stmt.Var(name, initializer);
  }

  private Stmt statement() {

    if (match(TokenType.FOR)) {
      return forStatement();
    }

    if (match(TokenType.IF)) {
      return ifStatement();
    }
    if (match(TokenType.ELSE)) {
      if (match(TokenType.IF)) {
        return elseifStatement();
      }
      return elseStatement();
    }
    if (match(TokenType.SYSTEM)) {
      if (match(TokenType.DOT)) {
        if (match(TokenType.OUT)) {
          if (match(TokenType.DOT)) {
            if (match(TokenType.PRINT))
              return printStatement();
            if (match(TokenType.PRINTLN))
              return printlnStatement();
          }

        }
      }
    }

    if (match(TokenType.RETURN))
      return returnStatement();

    if (match(TokenType.WHILE))
      return whileStatement();

    if (match(TokenType.DO))
      return doStatement();

    if (match(TokenType.SWITCH))
      return switchStatement();

    if (match(TokenType.TRY))
      return tryStatement();

    if (match(TokenType.CATCH))
      return catchStatement();

    if (match(TokenType.FINALLY))
      return finallyStatement();

    if (match(TokenType.LEFT_BRACE))
      return new Stmt.Block(block());  

    if (match(TokenType.ENUM))
      return enumStatement();

    if (match(TokenType.CONTINUE))
      return continueStatement();

    if (match(TokenType.BREAK))
      return breakStatement();
    
    if(peek().type == TokenType.IDENTIFIER && next().type == TokenType.DOT){     
      return getStatement();
    }

    return expressionStatement();
  }

  private Stmt forStatement() {
    consume(TokenType.LEFT_PAREN, "Expect '(' after 'for'.");

    Stmt initializer;
    Token l = null;
    if (match(TokenType.SEMICOLON)) {
      initializer = null;
    }  else if (peek().type == TokenType.INT) {
      Token type = consume(TokenType.INT, "Expect Type.");
      l = type;
      initializer = varDeclaration();
      consume(TokenType.SEMICOLON, "Expect ';' after loop condition.");

    }  else {      
      initializer = expressionStatement();
      consume(TokenType.SEMICOLON, "Expect ';' after loop condition.");
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

    return new Stmt.For(initializer, condition, increment, body, l);   
  }

  private Stmt ifStatement() {

    consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
    Expr condition = expression();
    consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.");

    consume(TokenType.LEFT_BRACE, "Expect '{' before body.");
    List<Stmt> thenBranch = block();

    return new Stmt.If(condition, thenBranch);
  }

  private Stmt elseifStatement() {

    consume(TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
    Expr condition = expression();
    consume(TokenType.RIGHT_PAREN, "Expect ')' after if condition.");

    consume(TokenType.LEFT_BRACE, "Expect '{' before body.");
    List<Stmt> thenBranch = block();

    return new Stmt.ElseIf(condition, thenBranch);
  }

  private Stmt elseStatement() {

    consume(TokenType.LEFT_BRACE, "Expect '{' before body.");
    List<Stmt> thenBranch = block();

    return new Stmt.Else(thenBranch);
  }

  private Stmt printStatement() {
    consume(TokenType.LEFT_PAREN, "Expect '(' after print statement.");
    Expr value = expression();
    consume(TokenType.RIGHT_PAREN, "Expect ')' after print statement.");
    
    return new Stmt.Print(value);
  }

  private Stmt printlnStatement() {
    consume(TokenType.LEFT_PAREN, "Expect '(' after println statement.");
    Expr value = expression();
    consume(TokenType.RIGHT_PAREN, "Expect ')' after println statement.");
    
    return new Stmt.println(value);
  }

  private Stmt returnStatement() {
    Token keyword = previous();
    Expr value = null;
    if (!check(TokenType.SEMICOLON)) {
      value = expression();
    }  
    return new Stmt.Return(keyword, value);
  }

  private Stmt continueStatement() {
    Token keyword = previous();    
    return new Stmt.Continue(keyword);
  }

  private Stmt breakStatement() {
    Token keyword = previous();
    return new Stmt.Break(keyword);
  }

  private Stmt whileStatement() {
    consume(TokenType.LEFT_PAREN, "Expect '(' after 'while'.");
    Expr condition = expression();
    consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");
    if (match(TokenType.SEMICOLON)) {
      return new Stmt.WhileDo(condition);
    }
    Stmt body = statement();

    return new Stmt.While(condition, body);
  }

  private Stmt doStatement() {
    List<Stmt> stm = new ArrayList<>();

    consume(TokenType.LEFT_BRACE, "Expect '{' after do");
    while (!check(TokenType.RIGHT_BRACE) && !isAtEnd()) {
      stm.add(statement());
    }
    consume(TokenType.RIGHT_BRACE, "Expect '}' after block.");

    List<Stmt> body = stm;
    return new Stmt.Do(body);
  }

  private Stmt switchStatement() {
    consume(TokenType.LEFT_PAREN, "Expect '(' after expression");
    Expr condition = expression();
    consume(TokenType.RIGHT_PAREN, "Expect ')' after condition");
    consume(TokenType.LEFT_BRACE, "Expect '{' before body");

    List<Stmt> cases = new ArrayList<>();
    List<Expr> caseVal = new ArrayList<>();

    while (!check(TokenType.DEFAULT)) {
      if (match(TokenType.CASE)) {
        caseVal.add(expression());
        consume(TokenType.COLON, "Expect ':' after case value");
        cases.add(statement());
        consume(TokenType.SEMICOLON, "Expect ';' after statement");
        consume(TokenType.BREAK, "Expect 'break'");
        consume(TokenType.SEMICOLON, "Expect ';' after statement");
      }
    }

    Stmt defaultBranch = null;    
    if (match(TokenType.DEFAULT)) {
      consume(TokenType.COLON, "Expect ':' after case value");
      defaultBranch = statement();
      consume(TokenType.SEMICOLON, "Expect ';' after statement");
      consume(TokenType.BREAK, "Expect 'break'");
      consume(TokenType.SEMICOLON, "Expect ';' after statement");
    }
    consume(TokenType.RIGHT_BRACE, "Expect '}' after switch body");
    return new Stmt.Switch(condition, cases, caseVal, defaultBranch);
  }

  private Stmt tryStatement() {
    Token name = previous();
    consume(TokenType.LEFT_BRACE, "Expect '{' after expression");
    List<Stmt> body = block();
    
    return new Stmt.Try(name, body);
  }

  private Stmt catchStatement() {
    Token name = previous();
    consume(TokenType.LEFT_PAREN, "Expect '('");
    List<Token> parameters = new ArrayList<>();
    List<Token> parametersTypes = new ArrayList<>();
    if (!check(TokenType.RIGHT_PAREN)) {
      do {
        if (parameters.size() >= 255) {
          error(peek(), "Can't have more than 255 parameters.");
        }
        if (match(TokenType.EXCEPTION))
          parametersTypes.add(previous());

        // get param types
        parameters.add(consume(TokenType.IDENTIFIER, "Expect parameter name."));
      } while (match(TokenType.COMMA));
    }
    consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");
    consume(TokenType.LEFT_BRACE, "Expect '{' after expression");
    List<Stmt> body = block(); 

    return new Stmt.Catch(name, body, parametersTypes, parameters);
  }

  private Stmt finallyStatement() {
    Token name = previous();
    consume(TokenType.LEFT_BRACE, "Expect '{' after expression");
    List<Stmt> body = block();   

    return new Stmt.Finally(name, body);
  } 

  private Stmt enumStatement() {
    Token id = consume(TokenType.IDENTIFIER, "Expect identifier after 'enum'");
    consume(TokenType.LEFT_BRACE, "Expect '{' after identifier");
   
    List<Token> body = new ArrayList<>();
    do {
      body.add(consume(TokenType.IDENTIFIER, "Expect identifer in enum body"));
    } while (match(TokenType.COMMA));
    consume(TokenType.RIGHT_BRACE, "Expect '}' after enum body");
    return new Stmt.Enum(id, body);
  }

  private Stmt expressionStatement() {
    Expr expr = expression();
    if (peek().type == TokenType.SEMICOLON) {
      primary();      
    }
    return new Stmt.Expression(expr);
  }

  private Stmt.Function function(String kind) {
    Token name;
    if (check(TokenType.IDENTIFIER)) {
      name = consume(TokenType.IDENTIFIER, "Expect " + kind + " name.");
    } else {
      name = consume(TokenType.MAIN, "Expect " + kind + " name.");
    }

    consume(TokenType.LEFT_PAREN, "Expect '(' after " + kind + " name.");
    List<Stmt> paramtyp = new ArrayList<>();
    List<Stmt> paramary = new ArrayList<>();
    List<Stmt> paramtid = new ArrayList<>();
    if (!check(TokenType.RIGHT_PAREN)) {

      do {
        Stmt type = null;
        Stmt array = null;
        Stmt id;
        if (paramtid.size() >= 255) {
          error(peek(), "Can't have more than 255 parameters.");
        }
        if (validRet.contains(peek().type) || (peek().type == TokenType.IDENTIFIER && next().type == TokenType.IDENTIFIER)) {          
          type = declaration();
          if (peek().type == TokenType.LEFT_BRACKET) {
            array = declaration();
          }
        }
        id = declaration();
        if (type == null) {
          paramtyp.add(id);
        } else {
          paramtyp.add(type);
        }
        if (array == null) {
          paramary.add(id);
        } else {
          paramary.add(array);
        }
        paramtid.add(id);
      } while (match(TokenType.COMMA));
    }
    consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");
    // pass throws clause no equivalent in c#
    if (match(TokenType.THROWS)) {
      while (peek().type != TokenType.LEFT_BRACE) {
        if (check(TokenType.EXCEPTION)) {
          consume(TokenType.EXCEPTION, "Expect Exception after 'throws");
        } else {
          consume(TokenType.COMMA, "Expect a ','");
        }

      }
    }

    List<Stmt> body = new ArrayList<>();
    if (match(TokenType.SEMICOLON)) {
      body.add(null);
      return new Stmt.Function(name, paramtyp, paramary, paramtid, body);
    }
    consume(TokenType.LEFT_BRACE, "Expect '{' before " + kind + " body.");
    body = block();    
    return new Stmt.Function(name, paramtyp, paramary, paramtid, body);

  }

  private Stmt.InterfaceFunction Ifunction(String kind) {

    Token name = consume(TokenType.IDENTIFIER, "Expect " + kind + " name.");


    consume(TokenType.LEFT_PAREN, "Expect '(' after " + kind + " name.");
    List<Stmt> paramtyp = new ArrayList<>();
    List<Stmt> paramary = new ArrayList<>();
    List<Stmt> paramtid = new ArrayList<>();
    if (!check(TokenType.RIGHT_PAREN)) {

      do {
        Stmt type = null;
        Stmt array = null;
        Stmt id;
        if (paramtid.size() >= 255) {
          error(peek(), "Can't have more than 255 parameters.");
        }
        if (validRet.contains(peek().type) || peek().type == TokenType.IDENTIFIER) {          
          type = declaration();
          if (peek().type == TokenType.LEFT_BRACKET) {
            array = declaration();
          }
        }
        id = declaration();
        if (type == null) {
          paramtyp.add(id);
        } else {
          paramtyp.add(type);
        }
        if (array == null) {
          paramary.add(id);
        } else {
          paramary.add(array);
        }
        paramtid.add(id);
      } while (match(TokenType.COMMA));
    }
    consume(TokenType.RIGHT_PAREN, "Expect ')' after parameters.");

    consume(TokenType.SEMICOLON, "Expect ';' after method declaration");
    return new Stmt.InterfaceFunction(name, paramtyp, paramary, paramtid);   
  }

  private Stmt getStatement(){
    System.out.println("1");
    Expr expr = new Expr.Variable(consume(TokenType.IDENTIFIER, ""));
     System.out.println("2");

    while (true) {
      if (match(TokenType.LEFT_PAREN)) {
        expr = finishCall(expr);

      } else if (match(TokenType.DOT)) {
        Expr name = expression();
        expr = new Expr.Get(expr, name);

      } else {
        break;
      }
    }
    return new Stmt.Get(expr);

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

        if (match(TokenType.EQUAL, TokenType.PLUSEQUAL, TokenType.MINUSEQUAL,
            TokenType.MULTEQUAL, TokenType.DIVEQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;              
                return new Expr.Assign(name, value, equals);
            }
            else if (expr instanceof Expr.ArrayGrouping){
                return new Expr.AssignArray(expr, value, equals);

            }
            else if (expr instanceof Expr.Get) {
                Expr.Get get = (Expr.Get)expr;
                return new Expr.Set(get.object, get.name, value, equals);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

  private Expr or() {
    Expr expr = and();

    while (match(TokenType.OR) || match(TokenType.OR_OR)) {
      Token operator = previous();
      Expr right = and();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }

  private Expr and() {
    Expr expr = equality();

    while (match(TokenType.AND) || match(TokenType.AND_AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);

        }

    return expr;
  }

  private Expr equality() {
    Expr expr = comparison();

    while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr comparison() {
    Expr expr = term();

    while (match(TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL)) {
      Token operator = previous();
      Expr right = term();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr term() {
    Expr expr = factor();
    while (match(TokenType.MINUS, TokenType.PLUS)) {
      Token operator = previous();
      Expr right = factor();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr factor() {
    Expr expr = unary();

    while (match(TokenType.SLASH, TokenType.STAR)) {
      Token operator = previous();
      Expr right = unary();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  private Expr unary() {
    if (match(TokenType.BANG, TokenType.MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    } else if (peek().type == TokenType.IDENTIFIER
        && (next().type == TokenType.PLUS_PLUS || next().type == TokenType.MINUS_MINUS)) {
      match(TokenType.IDENTIFIER);
      Expr left = new Expr.Variable(previous());
      match(TokenType.PLUS_PLUS, TokenType.MINUS_MINUS);
      Token operator = previous();
      if (peek().type == TokenType.SEMICOLON && peek().line != next().line) {
        return new Expr.Unary3(left, operator, new Expr.NewLine());
      }
      return new Expr.Unary2(left, operator);
    }

    return call();

  }

  private Expr finishCall(Expr callee) {
    List<Expr> arguments = new ArrayList<>();
    if (!check(TokenType.RIGHT_PAREN)) {
      do {

        if (arguments.size() >= 255) {
          error(peek(), "Can't have more than 255 arguments.");
        }

        arguments.add(primary());
      } while (match(TokenType.COMMA));
    }

    consume(TokenType.RIGHT_PAREN, "Expect ')' after arguments.");

    return new Expr.Call(callee, arguments);
  }

  private Expr call() {
    Expr expr = primary();

    while (true) {
      if (match(TokenType.LEFT_PAREN)) {
        expr = finishCall(expr);

      } else if (match(TokenType.DOT)) {
        Expr name = expression();
        expr = new Expr.Get(expr, name);

      } else {
        break;
      }
    }

    return expr;
  }

  private Expr primary() {
    if (match(TokenType.FALSE))
      return new Expr.Literal(false);
    if (match(TokenType.TRUE))
      return new Expr.Literal(true);
    if (match(TokenType.NULL))
      return new Expr.Literal(null);

    if (match(TokenType.NUMBER, TokenType.STRING)) {
      if (previous().literal == null) {
        return new Expr.Types(previous());

      }
      return new Expr.Literal(previous().literal);
    }

    if (match(TokenType.INT) || match(TokenType.VOID) || match(TokenType.CHAR) || match(TokenType.FLOAT)
        || match(TokenType.DOUBLE) || match(TokenType.BOOLEAN)) {
      return new Expr.Types(previous());
    }

    if (match(TokenType.PUBLIC) || match(TokenType.PRIVATE) || match(TokenType.ABSTRACT)
        || match(TokenType.STATIC) || match(TokenType.FINAL) || match(TokenType.PROTECTED)) {
      return new Expr.Modifiers(previous());
    }

    if (match(TokenType.SUPER)) {
      Token keyword = previous();
      consume(TokenType.DOT, "Expect '.' after 'super'.");
      Token method = consume(TokenType.IDENTIFIER, "Expect superclass method name.");
      return new Expr.Super(keyword, method);
    }

    if (match(TokenType.THIS))
      return new Expr.This(previous());
    if (match(TokenType.MAIN))
      return new Expr.Main(previous());
    if (match(TokenType.IMPORT))
      return new Expr.Import(previous());
    if (match(TokenType.PACKAGE))
      return new Expr.Package(previous());

    if (match(TokenType.IDENTIFIER)) {
            if(peek().type == TokenType.LEFT_PAREN || peek().type == TokenType.DOT)
            {
                Expr expr = new Expr.Variable(previous());
                while (true) {
                    if (match(TokenType.LEFT_PAREN)) {
                        expr = finishCall(expr);

                    } else if (match(TokenType.DOT)) {                        
                                Expr ex = expression();    
                                if(peek().type == TokenType.SEMICOLON){
                                  expr = new Expr.Get(expr, ex);
                                }  
                                else{
                                  expr = new Expr.Get(expr, ex);
                                }                 
                        

                    } else {
                        break;
                    }
                }
                return expr;
            }
            return new Expr.Variable(previous());
        }

    if (match(TokenType.DOT))
      return new Expr.Dot(previous());

    if (match(TokenType.NEW)) {
      return new Expr.New(previous());
    }

    if (match(TokenType.EXCEPTION)) {
      return new Expr.Exception(previous());
    }

    if (match(TokenType.THROW)) {
      consume(TokenType.NEW, "Expect 'new' after throw keyword");
      Token exp = consume(TokenType.EXCEPTION, "");
      Stmt grp = statement();

      return new Expr.Throw(exp, grp);
    }

    if (match(TokenType.SEMICOLON)) {
      return new Expr.NewLine();
    }

    if (match(TokenType.LEFT_PAREN)) {
      if (peek().type == TokenType.RIGHT_PAREN) {
        consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
        return new Expr.Grouping2(previous());
      }
      List<Expr> expr = new ArrayList<>();
      while (!match(TokenType.RIGHT_PAREN)) {
        expr.add(expression());
        if (peek().type == TokenType.COMMA) {
          consume(TokenType.COMMA, "Expected ',' after expression.");
        }
      }      
      return new Expr.Grouping(expr);
    }

    if (match(TokenType.LEFT_BRACE)) {
            if(peek().type == TokenType.RIGHT_BRACE){
                consume(TokenType.RIGHT_BRACE, "Expect '}' after expression.");
                return new Expr.Array2Grouping2(previous());
            }
            List<Expr> expr = new ArrayList<>();
            while(!match(TokenType.RIGHT_BRACE))
            {
                expr.add(expression());
                if(peek().type == TokenType.COMMA){
                    consume(TokenType.COMMA, "Expected ',' after expression.");
                }
            }
            return new Expr.Array2Grouping(expr);
        }

    if (match(TokenType.LEFT_BRACKET)) {
            List<Expr> expr = new ArrayList<>();
            int dimention = 1;
            while(peek().type == TokenType.RIGHT_BRACKET && next().type == TokenType.LEFT_BRACKET){
                consume(TokenType.RIGHT_BRACKET, "" );
                consume(TokenType.LEFT_BRACKET, "");
                expr.add(null);
                dimention++;
            }
            while(peek().type != TokenType.RIGHT_BRACKET && next().type != TokenType.LEFT_BRACKET){
                expr.add(expression());
                if(match(TokenType.COMMA)){
                    consume(TokenType.COMMA, "Expected ',' after expression.");
                }
                if(peek().type == TokenType.RIGHT_BRACKET && next().type == TokenType.LEFT_BRACKET){
                    consume(TokenType.RIGHT_BRACKET, "" );
                    consume(TokenType.LEFT_BRACKET, "");
                    dimention++;
                }

            }
            consume(TokenType.RIGHT_BRACKET, "");
            return new Expr.ArrayGrouping(expr, dimention);
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
    if (check(type))
      return advance();

    throw error(peek(), message);
  }

  private boolean check(TokenType type) {
    if (isAtEnd())
      return false;
    return peek().type == type;
  }

  private Token advance() {
    if (!isAtEnd())
      current++;
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

  private Token next() {
    return tokens.get(current + 1);
  }
  private ParseError error(Token token, String message) {
    return new ParseError();
  }

  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type == TokenType.SEMICOLON)
        return;

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
        case NEW:
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
        case EXCEPTION:
        case RIGHT_BRACE:
        case RIGHT_BRACKET:
        case LEFT_BRACKET:
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
