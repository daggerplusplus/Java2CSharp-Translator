import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Lexer {
  private final String source;
  private int start = 0;
  private int current = 0;
  private int line = 1;
  private final List<Token> tokens = new ArrayList<>();
  private static final Map<String, TokenType> keywords;
  static {
    keywords = new HashMap<>();
    keywords.put("class", TokenType.CLASS);
    keywords.put("else", TokenType.ELSE);
    keywords.put("false", TokenType.FALSE);
    keywords.put("for", TokenType.FOR);
    keywords.put("if", TokenType.IF);
    keywords.put("null", TokenType.NULL);
    keywords.put("print", TokenType.PRINT);
    keywords.put("return", TokenType.RETURN);
    keywords.put("super", TokenType.SUPER);
    keywords.put("this", TokenType.THIS);
    keywords.put("true", TokenType.TRUE);
    keywords.put("var", TokenType.VAR);
    keywords.put("while", TokenType.WHILE);
    keywords.put("public", TokenType.PUBLIC);
    keywords.put("abstract", TokenType.ABSTRACT);
    keywords.put("static", TokenType.STATIC);
    keywords.put("private", TokenType.PRIVATE);
    keywords.put("protected", TokenType.PROTECTED);
    keywords.put("System", TokenType.SYSTEM);
    keywords.put("out", TokenType.OUT);
    keywords.put("println", TokenType.PRINTLN);
    keywords.put("boolean", TokenType.BOOLEAN);
    keywords.put("break", TokenType.BREAK);
    keywords.put("byte", TokenType.BYTE);
    keywords.put("catch", TokenType.CATCH);
    keywords.put("continue", TokenType.CONTINUE);
    keywords.put("const", TokenType.CONST);
    keywords.put("default", TokenType.DEFAULT);
    keywords.put("enum", TokenType.ENUM);
    keywords.put("extends", TokenType.EXTENDS);
    keywords.put("final", TokenType.FINAL);
    keywords.put("finally", TokenType.FINALLY);
    keywords.put("interface", TokenType.INTERFACE);
    keywords.put("main", TokenType.MAIN);
    keywords.put("void", TokenType.VOID);
    keywords.put("volatile", TokenType.VOLATILE);
    keywords.put("throw", TokenType.THROW);
    keywords.put("throws", TokenType.THROWS);
    keywords.put("try", TokenType.TRY);
    keywords.put("double", TokenType.DOUBLE);
    keywords.put("long", TokenType.LONG);
    keywords.put("int", TokenType.INT);
    keywords.put("char", TokenType.CHAR);
    keywords.put("short", TokenType.SHORT);
    keywords.put("float", TokenType.FLOAT);
    keywords.put("switch", TokenType.SWITCH);
    keywords.put("case", TokenType.CASE);
    keywords.put("do", TokenType.DO);
    keywords.put("new", TokenType.NEW);
    keywords.put("String", TokenType.STRING);
    keywords.put("implements", TokenType.IMPLEMENTS);
    keywords.put("package", TokenType.PACKAGE);
    keywords.put("import", TokenType.IMPORT);
    keywords.put("ArithmeticException", TokenType.EXCEPTION);
    keywords.put("ArrayIndexOutOfBoundsException", TokenType.EXCEPTION);
    keywords.put("ClassNotFoundException", TokenType.EXCEPTION);
    keywords.put("FileNotFoundException", TokenType.EXCEPTION);
    keywords.put("IOException", TokenType.EXCEPTION);
    keywords.put("InterruptedException", TokenType.EXCEPTION);
    keywords.put("NoSuchFieldException", TokenType.EXCEPTION);
    keywords.put("NoSuchMethodException", TokenType.EXCEPTION);
    keywords.put("NullPointerException", TokenType.EXCEPTION);
    keywords.put("NumberFormatException", TokenType.EXCEPTION);
    keywords.put("RuntimeException", TokenType.EXCEPTION);
    keywords.put("StringIndexOutOfBoundsException", TokenType.EXCEPTION);
  }

  Lexer(String source) {
    this.source = source;    
  }

  List<Token> scanTokens() {
    while (!isAtEnd()) {
      // We are at the beginning of the next lexeme.
      start = current;
      scanToken();
    }

    tokens.add(new Token(TokenType.EOF, "", null, line));
    return tokens;
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private char advance() {
    return source.charAt(current++);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(':
        addToken(TokenType.LEFT_PAREN);
        break;
      case ')':
        addToken(TokenType.RIGHT_PAREN);
        break;
      case '{':
        addToken(TokenType.LEFT_BRACE);
        break;
      case '}':
        addToken(TokenType.RIGHT_BRACE);
        break;
      case '[':
        addToken(TokenType.LEFT_BRACKET);
        break;
      case ']':
        addToken(TokenType.RIGHT_BRACKET);
        break;
      case ',':
        addToken(TokenType.COMMA);
        break;
      case '.':
        addToken(TokenType.DOT);
        break;
      case ';':
        addToken(TokenType.SEMICOLON);
        break;
      case ':':
        addToken(TokenType.COLON);
        break;
      case '!':
        addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
        break;
      case '=':
        addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
        break;
      case '<':
        addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
        break;
      case '>':
        addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
        break;
      case '-':
        if (match('-')) {         
          addToken(TokenType.MINUS_MINUS);          
        }
        else if (match('=')) {
            addToken(TokenType.MINUSEQUAL);
        }
        else {
          addToken(TokenType.MINUS);
         }
        break;
      case '+':
        if (match('+')) {          
          addToken(TokenType.PLUS_PLUS);
          
        }
        else if (match('=')) {
            addToken(TokenType.PLUSEQUAL);
        }
        else {
          addToken(TokenType.PLUS);
         }
        break;
      case '&':
        addToken(match('&') ? TokenType.AND_AND : TokenType.AND);
        break;
      case '|':
        addToken(match('|') ? TokenType.OR_OR : TokenType.OR);
        break;
      case '*':
        addToken(match('=') ? TokenType.MULTEQUAL : TokenType.STAR);
        break;
      case '/':
        if (match('/')) {
          // A comment goes until the end of the line.
          while (peek() != '\n' && !isAtEnd())
            advance();
        } else if (match('='))
          addToken(TokenType.DIVEQUAL);
        else {
          addToken(TokenType.SLASH);
        }
        break;

      case ' ':
      case '\r':
      case '\t':
        // Ignore whitespace.
        break;

      case '\n':
        line++;
        break;

      case '"':
        string();
        break;
      default:
        if (isDigit(c)) {
          number();
        } else if (isAlpha(c)) {
          identifier();
        } else {
          // error(line, "Unexpected character.");
        }

        break;
    } // end switch
  } // end scanToken()

  private void identifier() {
    while (isAlphaNumeric(peek()))
      advance();

    String text = source.substring(start, current);
    TokenType type = keywords.get(text);
    if (text == "package")
      addToken(TokenType.PACKAGE);
    if (text == "import")
      addToken(TokenType.IMPORT);
    if (text == "ArithmeticException" || text == "ArrayIndexOutOfBoudnsException" || text == "ClassNotFoundException"
        || text == "FileNotFoundException" || text == "IOException" || text == "InterruptedException"
        || text == "NoSuchFieldException" || text == "NoSuchMethodException" || text == "NullPointerException"
        || text == "NumberFormatException" || text == "RuntimeException" || text == "StringIndexOutOfBoundsException")
      addToken(TokenType.EXCEPTION);
    if (type == null)
      type = TokenType.IDENTIFIER;
    addToken(type);
  }

  private void number() {
    while (isDigit(peek()))
      advance();

    // Look for a fractional part.
    if (peek() == '.' && isDigit(peekNext())) {
      // Consume the "."
      advance();
      while (isDigit(peek()))
        advance();
    }

    addToken(TokenType.NUMBER, source.substring(start, current));
  }

  private boolean match(char expected) {
    if (isAtEnd())
      return false;
    if (source.charAt(current) != expected)
      return false;

    current++;
    return true;
  }

  private char peek() {
    if (isAtEnd())
      return '\0';
    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length())
      return '\0';
    return source.charAt(current + 1);
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n')
        line++;
      advance();
    }

    if (isAtEnd()) {      
      return;
    }

    // The closing ".
    advance();

    String value = source.substring(start, current);
    addToken(TokenType.STRING, value);
  }

}
