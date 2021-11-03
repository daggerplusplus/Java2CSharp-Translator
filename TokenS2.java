class TokenS2 {
    final TokenTypeS2 type;
    final String lexeme;
    final Object literal;
    final int line;

    TokenS2(TokenTypeS2 type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return "Lexeme: " + lexeme + "Token: " + type + "Literal: " + literal;
    }
}
