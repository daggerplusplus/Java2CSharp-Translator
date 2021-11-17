import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ParserS2 {
  private static class ParseError extends RuntimeException {
  }

  private final List<TokenS2> tokens;
  private int current = 0;
  private final List<TokenTypeS2> validRet = new ArrayList<TokenTypeS2>(Arrays.asList(TokenTypeS2.VOID, TokenTypeS2.INT,
      TokenTypeS2.STRING, TokenTypeS2.CHAR, TokenTypeS2.FLOAT, TokenTypeS2.DOUBLE, TokenTypeS2.BOOLEAN));

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

  // public List<TokenS2> getModifiers()
  // {
  // List<TokenS2> modifiers = new ArrayList<TokenS2>();
  // while(validMod.contains(peek().type) || validRet.contains(peek().type)){
  // //Token modifier = consume(peek().type, "Expect access modifier");
  // modifiers.add(tokens.get(current));
  // advance();
  // }
  // return modifiers;
  // }

  // public boolean chekModifier()
  // {
  // if(validMod.contains(peek().type) || validRet.contains(peek().type)){
  // return true;
  // }
  // return false;
  // }

  // private StmtS2 modifiersDeclaration() {

  // List<TokenS2> modifiers = new ArrayList<TokenS2>();
  // modifiers = getModifiers();
  // StringBuilder builder = new StringBuilder();
  // for (TokenS2 m : modifiers) {
  // if (modifiers != modifiers.get(0)) builder.append(" ");
  // builder.append(m.lexeme);
  // }

  // return new StmtS2.Modifiers(modifiers);
  // }

  private StmtS2 declaration() {
    try {
      // get all modifiers;
      // if(chekModifier())
      // {
      // return modifiersDeclaration();
      // }

      if (match(TokenTypeS2.CLASS)) {
        return classDeclaration();
      }
      if (match(TokenTypeS2.INTERFACE)) {
        return interfaceDeclaration();
      }
      // If we have modifiers but not class or interface type then it either a
      // variable or function declaration
      if (peek().type == TokenTypeS2.IDENTIFIER || peek().type == TokenTypeS2.MAIN) {
        // if IDENTIFIER -> '(' then we go to function declaration else it will be a
        // variable declaration
        if (next().type == TokenTypeS2.LEFT_PAREN) {
          return function("function");
        } else if (next().type == TokenTypeS2.EQUAL) { // || next().type == TokenTypeS2.SEMICOLON) {
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

    List<ExprS2.Variable> superclass = new ArrayList<>();
    if (match(TokenTypeS2.EXTENDS)) {
      while (check(TokenTypeS2.IDENTIFIER) || check(TokenTypeS2.COMMA)) {
        if (check(TokenTypeS2.COMMA)) {
          consume(TokenTypeS2.COMMA, "");
        }
        consume(TokenTypeS2.IDENTIFIER, "Expect superclass name.");
        superclass.add(new ExprS2.Variable(previous()));
      }
    }

    List<ExprS2.Variable> implementinterface = new ArrayList<>();
    if (match(TokenTypeS2.IMPLEMENTS)) {
      while (!check(TokenTypeS2.LEFT_BRACE)) {
        consume(TokenTypeS2.IDENTIFIER, "Expect interface name.");
        implementinterface.add(new ExprS2.Variable(previous()));
        if (check(TokenTypeS2.COMMA)) {
          consume(TokenTypeS2.COMMA, "");
        }
      }
    }
    consume(TokenTypeS2.LEFT_BRACE, "Expect '{' before class body.");

    List<StmtS2> fields = block();

    return new StmtS2.Class(name, superclass, implementinterface, fields);

  }

  private StmtS2 interfaceDeclaration() {
    TokenS2 name = consume(TokenTypeS2.IDENTIFIER, "Expect interface name.");

    List<ExprS2.Variable> extender = new ArrayList<>();
        if (match(TokenTypeS2.EXTENDS)) {
            while(check(TokenTypeS2.IDENTIFIER) || check(TokenTypeS2.COMMA)){
                if(check(TokenTypeS2.COMMA)){
                    consume(TokenTypeS2.COMMA, "");
                }
                consume(TokenTypeS2.IDENTIFIER, "Expect superclass name.");
                extender.add(new ExprS2.Variable(previous()));
            }
        }

    consume(TokenTypeS2.LEFT_BRACE, "Expect '{' before body.");
    // List<StmtS2> methods = block();
    // return new StmtS2.Interface(name, extender, methods);

    List<StmtS2.InterfaceFunction> methods = new ArrayList<>();
    List<StmtS2> mods = new ArrayList<>();
    while (!check(TokenTypeS2.RIGHT_BRACE) && !isAtEnd()) {
      mods.add(declaration());
      methods.add(Ifunction("method"));
    }
    consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after class body.");
    return new StmtS2.Interface(name, extender, methods, mods);
  }

  private StmtS2 varDeclaration() {
    TokenS2 name = consume(TokenTypeS2.IDENTIFIER, "Expect variable name.");
    List<ExprS2> initializer = new ArrayList<>();
    if (match(TokenTypeS2.EQUAL)) {
      while (!peek().type.equals(TokenTypeS2.SEMICOLON)) {
        ExprS2 x = expression();
        initializer.add(x);
      }
      // initializer = expression();
    }
    // consume(TokenTypeS2.SEMICOLON, "Expect ';' after variable declaration.");
    return new StmtS2.Var(name, initializer);
  }

  private StmtS2 statement() {

    if (match(TokenTypeS2.FOR)) {
      return forStatement();
    }

    if (match(TokenTypeS2.IF)) {
      return ifStatement();
    }
    if (match(TokenTypeS2.ELSE)) {
      if (match(TokenTypeS2.IF)) {
        return elseifStatement();
      }
      return elseStatement();
    }
    if (match(TokenTypeS2.SYSTEM)) {
      if (match(TokenTypeS2.DOT)) {
        if (match(TokenTypeS2.OUT)) {
          if (match(TokenTypeS2.DOT)) {
            if (match(TokenTypeS2.PRINT))
              return printStatement();
            if (match(TokenTypeS2.PRINTLN))
              return printlnStatement();
          }

        }
      }
    }

    if (match(TokenTypeS2.RETURN))
      return returnStatement();

    if (match(TokenTypeS2.WHILE))
      return whileStatement();

    if (match(TokenTypeS2.DO))
      return doStatement();

    if (match(TokenTypeS2.SWITCH))
      return switchStatement();

    if (match(TokenTypeS2.TRY))
      return tryStatement();

    if (match(TokenTypeS2.CATCH))
      return catchStatement();

    if (match(TokenTypeS2.FINALLY))
      return finallyStatement();

    if (match(TokenTypeS2.LEFT_BRACE))
      return new StmtS2.Block(block());

    // if (match(TokenTypeS2.THROW))
    // return throwStatement();

    if (match(TokenTypeS2.ENUM))
      return enumStatement();

    if (match(TokenTypeS2.CONTINUE))
      return continueStatement();

    if (match(TokenTypeS2.BREAK))
      return breakStatement();

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
      consume(TokenTypeS2.SEMICOLON, "Expect ';' after loop condition.");

    } else {
      initializer = expressionStatement();
      consume(TokenTypeS2.SEMICOLON, "Expect ';' after loop condition.");
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

    return new StmtS2.For(initializer, condition, increment, body);

    // if (increment != null) {
    // body = new StmtS2.Block(Arrays.asList(body, new
    // StmtS2.Expression(increment)));
    // }

    // if (condition == null) condition = new ExprS2.Literal(true);
    // body = new StmtS2.For(condition, body);

    // if (initializer != null) {
    // body = new StmtS2.Block(Arrays.asList(initializer, body));
    // }
    // return body;
  }

  private StmtS2 ifStatement() {

    consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after 'if'.");
    ExprS2 condition = expression();
    consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after if condition.");

    consume(TokenTypeS2.LEFT_BRACE, "Expect '{' before body.");
    List<StmtS2> thenBranch = block();

    return new StmtS2.If(condition, thenBranch);
  }

  private StmtS2 elseifStatement() {

    consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after 'if'.");
    ExprS2 condition = expression();
    consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after if condition.");

    consume(TokenTypeS2.LEFT_BRACE, "Expect '{' before body.");
    List<StmtS2> thenBranch = block();

    return new StmtS2.ElseIf(condition, thenBranch);
  }

  private StmtS2 elseStatement() {

    consume(TokenTypeS2.LEFT_BRACE, "Expect '{' before body.");
    List<StmtS2> thenBranch = block();

    return new StmtS2.Else(thenBranch);
  }

  private StmtS2 printStatement() {
    consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after print statement.");
    ExprS2 value = expression();
    consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after print statement.");
    // consume(TokenTypeS2.SEMICOLON, "Expect ';' after value.");
    return new StmtS2.Print(value);
  }

  private StmtS2 printlnStatement() {
    consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after println statement.");
    ExprS2 value = expression();
    consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after println statement.");
    // consume(TokenTypeS2.SEMICOLON, "Expect ';' after value.");
    return new StmtS2.println(value);
  }

  private StmtS2 returnStatement() {
    TokenS2 keyword = previous();
    ExprS2 value = null;
    if (!check(TokenTypeS2.SEMICOLON)) {
      value = expression();
    }

    // consume(TokenTypeS2.SEMICOLON, "Expect ';' after return value.");
    return new StmtS2.Return(keyword, value);
  }

  private StmtS2 continueStatement() {
    TokenS2 keyword = previous();
    // consume(TokenTypeS2.SEMICOLON, "Expect ';' after continue");
    return new StmtS2.Continue(keyword);
  }

  private StmtS2 breakStatement() {
    TokenS2 keyword = previous();
    // consume(TokenTypeS2.SEMICOLON, "Expect ';' after continue");
    return new StmtS2.Break(keyword);
  }

  private StmtS2 whileStatement() {
    consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after 'while'.");
    ExprS2 condition = expression();
    consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after condition.");
    if (match(TokenTypeS2.SEMICOLON)) {
      return new StmtS2.WhileDo(condition);
    }
    StmtS2 body = statement();

    return new StmtS2.While(condition, body);
  }

  private StmtS2 doStatement() {
    System.out.println("in doStatement");
    List<StmtS2> stm = new ArrayList<>();

    consume(TokenTypeS2.LEFT_BRACE, "Expect '{' after do");
    while (!check(TokenTypeS2.RIGHT_BRACE) && !isAtEnd()) {
      stm.add(statement());
    }
    consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after block.");

    List<StmtS2> body = stm;
    return new StmtS2.Do(body);
  }

  private StmtS2 switchStatement() {
    consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after expression");
    ExprS2 condition = expression();
    consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after condition");
    consume(TokenTypeS2.LEFT_BRACE, "Expect '{' before body");

    List<StmtS2> cases = new ArrayList<>();
    List<ExprS2> caseVal = new ArrayList<>();

    while (!check(TokenTypeS2.DEFAULT)) {
      if (match(TokenTypeS2.CASE)) {
        caseVal.add(expression());
        consume(TokenTypeS2.COLON, "Expect ':' after case value");
        cases.add(statement());
        consume(TokenTypeS2.SEMICOLON, "Expect ';' after statement");
        consume(TokenTypeS2.BREAK, "Expect 'break'");
        consume(TokenTypeS2.SEMICOLON, "Expect ';' after statement");
      }
    }

    StmtS2 defaultBranch = null;
    ExprS2 defaultVal = null;
    if (match(TokenTypeS2.DEFAULT)) {
      consume(TokenTypeS2.COLON, "Expect ':' after case value");
      defaultBranch = statement();
      consume(TokenTypeS2.SEMICOLON, "Expect ';' after statement");
      consume(TokenTypeS2.BREAK, "Expect 'break'");
      consume(TokenTypeS2.SEMICOLON, "Expect ';' after statement");
    }
    consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after switch body");
    return new StmtS2.Switch(condition, cases, caseVal, defaultBranch);
  }

  private StmtS2 tryStatement() {
    TokenS2 name = previous();
    consume(TokenTypeS2.LEFT_BRACE, "Expect '{' after expression");
    List<StmtS2> body = block();
    // consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after condition");

    return new StmtS2.Try(name, body);
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
        if (match(TokenTypeS2.EXCEPTION))
          parametersTypes.add(previous());

        // get param types
        parameters.add(consume(TokenTypeS2.IDENTIFIER, "Expect parameter name."));
      } while (match(TokenTypeS2.COMMA));
    }
    consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after parameters.");
    consume(TokenTypeS2.LEFT_BRACE, "Expect '{' after expression");
    List<StmtS2> body = block();
    // consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after condition");

    return new StmtS2.Catch(name, body, parametersTypes, parameters);
  }

  private StmtS2 finallyStatement() {
    TokenS2 name = previous();
    consume(TokenTypeS2.LEFT_BRACE, "Expect '{' after expression");
    List<StmtS2> body = block();
    // consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after condition");

    return new StmtS2.Finally(name, body);
  }

  // private StmtS2 throwStatement() {
  // consume(TokenTypeS2.LEFT_BRACE, "Expect '{' after expression");
  // TokenS2 keyword = previous();
  // consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after condition");

  // return new StmtS2.Throw(keyword);
  // }

  private StmtS2 enumStatement() {
    TokenS2 id = consume(TokenTypeS2.IDENTIFIER, "Expect identifier after 'enum'");
    consume(TokenTypeS2.LEFT_BRACE, "Expect '{' after identifier");
    // StmtS2 body = statement();
    List<TokenS2> body = new ArrayList<>();
    do {
      body.add(consume(TokenTypeS2.IDENTIFIER, "Expect identifer in enum body"));
    } while (match(TokenTypeS2.COMMA));
    consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after enum body");
    return new StmtS2.Enum(id, body);
  }

  private StmtS2 expressionStatement() {
    ExprS2 expr = expression();
    if (peek().type == TokenTypeS2.SEMICOLON) {
      primary();
      // consume(TokenTypeS2.SEMICOLON, "Expect ';' after expression.");
    }
    return new StmtS2.Expression(expr);
  }

  private StmtS2.Function function(String kind) {
    TokenS2 name;
    if (check(TokenTypeS2.IDENTIFIER)) {
      name = consume(TokenTypeS2.IDENTIFIER, "Expect " + kind + " name.");
    } else {
      name = consume(TokenTypeS2.MAIN, "Expect " + kind + " name.");
    }

    consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after " + kind + " name.");
    List<StmtS2> paramtyp = new ArrayList<>();
    List<StmtS2> paramary = new ArrayList<>();
    List<StmtS2> paramtid = new ArrayList<>();
    if (!check(TokenTypeS2.RIGHT_PAREN)) {

      do {
        StmtS2 type = null;
        StmtS2 array = null;
        StmtS2 id;
        if (paramtid.size() >= 255) {
          error(peek(), "Can't have more than 255 parameters.");
        }
        if (validRet.contains(peek().type) || peek().type == TokenTypeS2.IDENTIFIER) {
          // parametersTypes.add(consume(peek().type, "Expect parameter type"));
          type = declaration();
          if (peek().type == TokenTypeS2.LEFT_BRACKET) {
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
      } while (match(TokenTypeS2.COMMA));
    }
    consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after parameters.");
    // pass throws clause no equivalent in c#
    if (match(TokenTypeS2.THROWS)) {
      while (peek().type != TokenTypeS2.LEFT_BRACE) {
        if (check(TokenTypeS2.EXCEPTION)) {
          consume(TokenTypeS2.EXCEPTION, "Expect Exception after 'throws");
        } else {
          consume(TokenTypeS2.COMMA, "Expect a ','");
        }

      }
    }

    List<StmtS2> body = new ArrayList<>();
    if (match(TokenTypeS2.SEMICOLON)) {
      body.add(null);
      return new StmtS2.Function(name, paramtyp, paramary, paramtid, body);
    }
    consume(TokenTypeS2.LEFT_BRACE, "Expect '{' before " + kind + " body.");
    body = block();
    // consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after" + kind + " body");
    return new StmtS2.Function(name, paramtyp, paramary, paramtid, body);

  }

  private StmtS2.InterfaceFunction Ifunction(String kind) {

    TokenS2 name = consume(TokenTypeS2.IDENTIFIER, "Expect " + kind + " name.");


    consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after " + kind + " name.");
    List<StmtS2> paramtyp = new ArrayList<>();
    List<StmtS2> paramary = new ArrayList<>();
    List<StmtS2> paramtid = new ArrayList<>();
    if (!check(TokenTypeS2.RIGHT_PAREN)) {

      do {
        StmtS2 type = null;
        StmtS2 array = null;
        StmtS2 id;
        if (paramtid.size() >= 255) {
          error(peek(), "Can't have more than 255 parameters.");
        }
        if (validRet.contains(peek().type) || peek().type == TokenTypeS2.IDENTIFIER) {
          // parametersTypes.add(consume(peek().type, "Expect parameter type"));
          System.out.println("token " + peek());
          type = declaration();
          if (peek().type == TokenTypeS2.LEFT_BRACKET) {
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
      } while (match(TokenTypeS2.COMMA));
    }
    consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after parameters.");

    consume(TokenTypeS2.SEMICOLON, "Expect ';' after method declaration");
    return new StmtS2.InterfaceFunction(name, paramtyp, paramary, paramtid);

    // consume(TokenTypeS2.LEFT_PAREN, "Expect '(' after " + kind + " name.");
    // List<TokenS2> parameters = new ArrayList<>();
    // List<TokenS2> parametersTypes = new ArrayList<>();
    // if (!check(TokenTypeS2.RIGHT_PAREN)) {
    //   // List<TokenTypeS2> validRet = new ArrayList<TokenTypeS2>(Arrays.asList(TokenTypeS2.VOID, TokenTypeS2.INT,
    //   //     TokenTypeS2.STRING, TokenTypeS2.CHAR, TokenTypeS2.FLOAT, TokenTypeS2.DOUBLE, TokenTypeS2.BOOLEAN));

    //   do {
    //     if (parameters.size() >= 255) {
    //       error(peek(), "Can't have more than 255 parameters.");
    //     }
    //     if (validRet.contains(peek().type)) {
    //       parametersTypes.add(consume(peek().type, "Expect parameter type"));
    //     }
    //     // get param types
    //     parameters.add(consume(TokenTypeS2.IDENTIFIER, "Expect parameter name."));
    //   } while (match(TokenTypeS2.COMMA));
    // }
    // consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after parameters.");
    // consume(TokenTypeS2.SEMICOLON, "Expect ';' after method declaration");
    // return new StmtS2.InterfaceFunction(name, paramtype, parameters);
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
        TokenS2 name = ((ExprS2.Variable) expr).name;
        return new ExprS2.Assign(name, value);

      } else if (expr instanceof ExprS2.Get) {
        ExprS2.Get get = (ExprS2.Get) expr;
        return new ExprS2.Set(get.object, get.name, value);
      }

      error(equals, "Invalid assignment target.");
    }

    return expr;
  }

  private ExprS2 or() {
    ExprS2 expr = and();

    while (match(TokenTypeS2.OR) || match(TokenTypeS2.OR_OR)) {
      TokenS2 operator = previous();
      ExprS2 right = and();
      expr = new ExprS2.Logical(expr, operator, right);
    }

    return expr;
  }

  private ExprS2 and() {
    ExprS2 expr = equality();

    while (match(TokenTypeS2.AND) || match(TokenTypeS2.AND_AND)) {
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
    if (match(TokenTypeS2.BANG, TokenTypeS2.MINUS)) {
      TokenS2 operator = previous();
      ExprS2 right = unary();
      return new ExprS2.Unary(operator, right);
    } else if (peek().type == TokenTypeS2.IDENTIFIER
        && (next().type == TokenTypeS2.PLUS_PLUS || next().type == TokenTypeS2.MINUS_MINUS)) {
      match(TokenTypeS2.IDENTIFIER);
      ExprS2 left = new ExprS2.Variable(previous());
      match(TokenTypeS2.PLUS_PLUS, TokenTypeS2.MINUS_MINUS);
      TokenS2 operator = previous();
      if (peek().type == TokenTypeS2.SEMICOLON && peek().line != next().line) {
        return new ExprS2.Unary3(left, operator, new ExprS2.NewLine());
      }
      return new ExprS2.Unary2(left, operator);
    }

    return call();

  }

  private ExprS2 finishCall(ExprS2 callee) {
    List<StmtS2> arguments = new ArrayList<>();
    if (!check(TokenTypeS2.RIGHT_PAREN)) {
      do {

        if (arguments.size() >= 255) {
          error(peek(), "Can't have more than 255 arguments.");
        }

        arguments.add(declaration());
      } while (match(TokenTypeS2.COMMA));
    }

    TokenS2 paren = consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after arguments.");

    return new ExprS2.Call(callee, paren, arguments);
  }

  private ExprS2 call() {
    ExprS2 expr = primary();

    while (true) {
      if (match(TokenTypeS2.LEFT_PAREN)) {
        expr = finishCall(expr);

      } else if (match(TokenTypeS2.DOT)) {
        TokenS2 name = consume(TokenTypeS2.IDENTIFIER, "Expect property name after '.'.");
        expr = new ExprS2.Get(expr, name);

      } else {
        break;
      }
    }

    return expr;
  }

  private ExprS2 primary() {
    if (match(TokenTypeS2.FALSE))
      return new ExprS2.Literal(false);
    if (match(TokenTypeS2.TRUE))
      return new ExprS2.Literal(true);
    if (match(TokenTypeS2.NULL))
      return new ExprS2.Literal(null);

    if (match(TokenTypeS2.NUMBER, TokenTypeS2.STRING)) {
      if (previous().literal == null) {
        return new ExprS2.Types(previous());

      }
      return new ExprS2.Literal(previous().literal);
    }

    if (match(TokenTypeS2.INT) || match(TokenTypeS2.VOID) || match(TokenTypeS2.CHAR) || match(TokenTypeS2.FLOAT)
        || match(TokenTypeS2.DOUBLE) || match(TokenTypeS2.BOOLEAN)) {
      return new ExprS2.Types(previous());
    }

    if (match(TokenTypeS2.PUBLIC) || match(TokenTypeS2.PRIVATE) || match(TokenTypeS2.ABSTRACT)
        || match(TokenTypeS2.STATIC) || match(TokenTypeS2.FINAL) || match(TokenTypeS2.PROTECTED)) {
      return new ExprS2.Modifiers(previous());
    }

    if (match(TokenTypeS2.SUPER)) {
      TokenS2 keyword = previous();
      consume(TokenTypeS2.DOT, "Expect '.' after 'super'.");
      TokenS2 method = consume(TokenTypeS2.IDENTIFIER, "Expect superclass method name.");
      return new ExprS2.Super(keyword, method);
    }

    if (match(TokenTypeS2.THIS))
      return new ExprS2.This(previous());
    if (match(TokenTypeS2.MAIN))
      return new ExprS2.Main(previous());
    if (match(TokenTypeS2.IMPORT))
      return new ExprS2.Import(previous());
    if (match(TokenTypeS2.PACKAGE))
      return new ExprS2.Package(previous());

    if (match(TokenTypeS2.IDENTIFIER)) {
            if(peek().type == TokenTypeS2.LEFT_PAREN || peek().type == TokenTypeS2.DOT)
            {
                ExprS2 expr = new ExprS2.Variable(previous());
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
            return new ExprS2.Variable(previous());
        }

    if (match(TokenTypeS2.DOT))
      return new ExprS2.Dot(previous());

    if (match(TokenTypeS2.NEW)) {
      return new ExprS2.New(previous());
    }

    if (match(TokenTypeS2.EXCEPTION)) {
      return new ExprS2.Exception(previous());
    }

    if (match(TokenTypeS2.THROW)) {
      consume(TokenTypeS2.NEW, "Expect 'new' after throw keyword");
      TokenS2 exp = consume(TokenTypeS2.EXCEPTION, "");
      StmtS2 grp = statement();

      return new ExprS2.Throw(exp, grp);
    }

    if (match(TokenTypeS2.SEMICOLON)) {
      return new ExprS2.NewLine();
    }

    if (match(TokenTypeS2.LEFT_PAREN)) {
      if (peek().type == TokenTypeS2.RIGHT_PAREN) {
        consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after expression.");
        return new ExprS2.Grouping2(previous());
      }
      List<ExprS2> expr = new ArrayList<>();
      while (!match(TokenTypeS2.RIGHT_PAREN)) {
        expr.add(expression());
        if (peek().type == TokenTypeS2.COMMA) {
          consume(TokenTypeS2.COMMA, "Expected ',' after expression.");
        }
      }
      // ExprS2 expr = expression();
      // consume(TokenTypeS2.RIGHT_PAREN, "Expect ')' after expression.");
      return new ExprS2.Grouping(expr);
    }

    if (match(TokenTypeS2.LEFT_BRACKET)) {
      if (peek().type == TokenTypeS2.RIGHT_BRACKET) {
        consume(TokenTypeS2.RIGHT_BRACKET, "Expect ']' after expression.");
        return new ExprS2.ArrayGrouping2(previous());
      }
      List<ExprS2> expr = new ArrayList<>();
      while (!match(TokenTypeS2.RIGHT_BRACKET)) {
        expr.add(expression());
        if (peek().type == TokenTypeS2.COMMA) {
          consume(TokenTypeS2.COMMA, "Expected ',' after expression.");
        }
      }
      // ExprS2 expr = expression();
      // consume(TokenTypeS2.RIGHT_BRACKET, "Expect ']' after expression.");
      return new ExprS2.ArrayGrouping(expr);
    }

    if (match(TokenTypeS2.LEFT_BRACE)) {
      if (peek().type == TokenTypeS2.RIGHT_BRACE) {
        consume(TokenTypeS2.RIGHT_BRACE, "Expect '}' after expression.");
        return new ExprS2.Array2Grouping2(previous());
      }
      List<ExprS2> expr = new ArrayList<>();
      while (!match(TokenTypeS2.RIGHT_BRACE)) {
        expr.add(expression());
        if (peek().type == TokenTypeS2.COMMA) {
          consume(TokenTypeS2.COMMA, "Expected ',' after expression.");
        }
      }
      // ExprS2 expr = expression();
      // consume(TokenTypeS2.RIGHT_BRACE, "Expect ']' after expression.");
      return new ExprS2.Array2Grouping(expr);
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
    if (check(type))
      return advance();

    throw error(peek(), message);
  }

  private boolean check(TokenTypeS2 type) {
    if (isAtEnd())
      return false;
    return peek().type == type;
  }

  private TokenS2 advance() {
    if (!isAtEnd())
      current++;
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
      if (previous().type == TokenTypeS2.SEMICOLON)
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
