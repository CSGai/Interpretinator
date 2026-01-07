package main.java.intrpinator;

class Token {
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.line = line;
        this.literal = literal;
        this.lexeme = lexeme;
    }

    public String toString() {
        return String.format("{lexeme: '%s'\nliteral: '%s\ntype: '%s'\nline: '%d'}", lexeme, literal, type, line);
    }
}
