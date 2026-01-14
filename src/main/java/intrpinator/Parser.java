package main.java.intrpinator;

import java.util.List;

import static main.java.intrpinator.TokenType.*;

class Parser {
    final private List<Token> tokens;
    private int current_idx = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }
//    private Expr comperison() {
//        ;
//    }

    // consume
    private boolean match(TokenType... targets) {
        if (endOfFile()) return false;

        boolean result = false;
        Token current_token = tokens.get(current_idx);

        for (TokenType type : targets) {
            result = type == current_token.type;
            if (result) break;
        }
        if (result) current_idx++;
        return result;
    }

    private Token advance() {
        if (endOfFile()) current_idx++;
        return previous();
    }

    // non-consume
    private Token peek() {
        return tokens.get(current_idx);
    }

    private Token previous() {
        return tokens.get(current_idx - 1);
    }

    private boolean endOfFile() {
        return peek().type == EOF;
    }

}
