
import java.util.*;

public class LexerS2 {

    private final String source;
    private int start = 0;
    private int current = 0;
    private int line = 1;


    private final List<TokenS2> tokens = new ArrayList<>();
    private static final Map<String, TokenTypeS2> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("and",         TokenTypeS2.AND);
        keywords.put("class",       TokenTypeS2.CLASS);
        keywords.put("else",        TokenTypeS2.ELSE);
        keywords.put("false",       TokenTypeS2.FALSE);
        keywords.put("for",         TokenTypeS2.FOR);
        keywords.put("if",          TokenTypeS2.IF);
        keywords.put("null",         TokenTypeS2.NULL);
        keywords.put("or",          TokenTypeS2.OR);
        keywords.put("print",       TokenTypeS2.PRINT);
        keywords.put("return",      TokenTypeS2.RETURN);
        keywords.put("super",       TokenTypeS2.SUPER);
        keywords.put("this",        TokenTypeS2.THIS);
        keywords.put("true",        TokenTypeS2.TRUE);
        keywords.put("var",         TokenTypeS2.VAR);
        keywords.put("while",       TokenTypeS2.WHILE);
        keywords.put("public",      TokenTypeS2.PUBLIC);
        keywords.put("abstract",    TokenTypeS2.ABSTRACT);
        keywords.put("static",      TokenTypeS2.STATIC);
        keywords.put("private",     TokenTypeS2.PRIVATE);
        keywords.put("protected",   TokenTypeS2.PROTECTED);
        keywords.put("System",      TokenTypeS2.SYSTEM);
        keywords.put("out",         TokenTypeS2.OUT);
        keywords.put("println",     TokenTypeS2.PRINTLN);
        keywords.put("boolean",     TokenTypeS2.BOOLEAN);
        keywords.put("break",       TokenTypeS2.BREAK);
        keywords.put("byte",        TokenTypeS2.BYTE);
        keywords.put("catch",       TokenTypeS2.CATCH);
        keywords.put("continue",    TokenTypeS2.CONTINUE);
        keywords.put("const",       TokenTypeS2.CONST);
        keywords.put("default",     TokenTypeS2.DEFAULT);
        keywords.put("enum",        TokenTypeS2.ENUM);
        keywords.put("extends",     TokenTypeS2.EXTENDS);
        keywords.put("final",       TokenTypeS2.FINAL);
        keywords.put("finally",     TokenTypeS2.FINALLY);
        keywords.put("interface",   TokenTypeS2.INTERFACE);
        keywords.put("main",        TokenTypeS2.MAIN);
        keywords.put("void",        TokenTypeS2.VOID);
        keywords.put("volatile",    TokenTypeS2.VOLATILE);
        keywords.put("throw",       TokenTypeS2.THROW);
        keywords.put("try",         TokenTypeS2.TRY);
        keywords.put("double",      TokenTypeS2.DOUBLE);
        keywords.put("long",        TokenTypeS2.LONG);
        keywords.put("int",         TokenTypeS2.INT);
        keywords.put("char",        TokenTypeS2.CHAR);
        keywords.put("short",       TokenTypeS2.SHORT);
        keywords.put("float",       TokenTypeS2.FLOAT);
        keywords.put("switch",      TokenTypeS2.SWITCH);
        keywords.put("case",        TokenTypeS2.CASE);
        keywords.put("do",          TokenTypeS2.DO);
        keywords.put("new",         TokenTypeS2.NEW);
        keywords.put("String",      TokenTypeS2.STRING);
        keywords.put("implements",  TokenTypeS2.IMPLEMENTS);
        keywords.put("package",     TokenTypeS2.PACKAGE);
        keywords.put("import",      TokenTypeS2.IMPORT);
        keywords.put("ArithmeticException", TokenTypeS2.EXCEPTION);
        keywords.put("ArrayIndexOutOfBoundsException", TokenTypeS2.EXCEPTION);
        keywords.put("ClassNotFoundException", TokenTypeS2.EXCEPTION);
        keywords.put("FileNotFoundException", TokenTypeS2.EXCEPTION);
        keywords.put("IOException", TokenTypeS2.EXCEPTION);
        keywords.put("InterruptedException", TokenTypeS2.EXCEPTION);
        keywords.put("NoSuchFieldException", TokenTypeS2.EXCEPTION);
        keywords.put("NoSuchMethodException", TokenTypeS2.EXCEPTION);
        keywords.put("NullPointerException", TokenTypeS2.EXCEPTION);
        keywords.put("NumberFormatException", TokenTypeS2.EXCEPTION);
        keywords.put("RuntimeException", TokenTypeS2.EXCEPTION);
        keywords.put("StringIndexOutOfBoundsException", TokenTypeS2.EXCEPTION);
    }

    LexerS2(String source) {
        this.source = source;
     String codePath = ProjectGUIS2.getInputPath();   //UNCOMMENT THIS FOR GUI
    }


    List<TokenS2> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new TokenS2(TokenTypeS2.EOF, "", null, line));
        return tokens;
    }
    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenTypeS2 type) {
        addToken(type, null);
    }

    private void addToken(TokenTypeS2 type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new TokenS2(type, text, literal, line));
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenTypeS2.LEFT_PAREN); break;
            case ')': addToken(TokenTypeS2.RIGHT_PAREN); break;
            case '{': addToken(TokenTypeS2.LEFT_BRACE); break;
            case '}': addToken(TokenTypeS2.RIGHT_BRACE); break;
            case '[': addToken(TokenTypeS2.LEFT_BRACKET); break;
            case ']': addToken(TokenTypeS2.RIGHT_BRACKET); break;
            case ',': addToken(TokenTypeS2.COMMA); break;
            case '.': addToken(TokenTypeS2.DOT); break;
            case ';': addToken(TokenTypeS2.SEMICOLON); break;
            case '!':
                addToken(match('=') ? TokenTypeS2.BANG_EQUAL : TokenTypeS2.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenTypeS2.EQUAL_EQUAL : TokenTypeS2.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenTypeS2.LESS_EQUAL : TokenTypeS2.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenTypeS2.GREATER_EQUAL : TokenTypeS2.GREATER);
                break;
            case '-':
                addToken(match('-') ? TokenTypeS2.MINUS_MINUS : TokenTypeS2.MINUS);
                break;
            case '+':
                addToken(match('+') ? TokenTypeS2.PLUS_PLUS : TokenTypeS2.PLUS);
                break;
            // case '+':
            //     addToken(match('=') ? TokenTypeS2.PLUSEQUAL : TokenTypeS2.PLUS);
            //     break;
            // case '-':
            //     addToken(match('=') ? TokenTypeS2.MINUSEQUAL :
            //             TokenTypeS2.MINUS);
            //     break;
            case '*':
                addToken(match('=') ? TokenTypeS2.MULTEQUAL :
                        TokenTypeS2.STAR);
                break;
            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                }
                else if (match('='))
                    addToken(TokenTypeS2.DIVEQUAL);
                else {
                    addToken(TokenTypeS2.SLASH);
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

            case '"': string(); break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                }
                else {
                    //error(line, "Unexpected character.");
                }

                break;
        } //end switch
    } // end scanToken()

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenTypeS2 type = keywords.get(text);
        if (text=="package") addToken(TokenTypeS2.PACKAGE);
        if (text =="import") addToken(TokenTypeS2.IMPORT);
        if (text == "ArithmeticException" ||
            text == "ArrayIndexOutOfBoudnsException" ||
            text == "ClassNotFoundException" ||
            text == "FileNotFoundException" ||
            text == "IOException" ||
            text == "InterruptedException" ||
            text == "NoSuchFieldException" ||
            text == "NoSuchMethodException" ||
            text == "NullPointerException" ||
            text == "NumberFormatException" ||
            text == "RuntimeException" ||
            text == "StringIndexOutOfBoundsException"
            ) addToken(TokenTypeS2.EXCEPTION);
        if (type == null) type = TokenTypeS2.IDENTIFIER;
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();
            while (isDigit(peek())) advance();
        }

        addToken(TokenTypeS2.NUMBER,source.substring(start, current));
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            //Main.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();
        
        String value = source.substring(start, current);
        addToken(TokenTypeS2.STRING, value);
    }




}
