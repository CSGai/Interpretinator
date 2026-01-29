package main.java.intrpinator;

import java.util.List;

import static main.java.intrpinator.TokenType.*;

class Parser {
    private static class ParseError extends RuntimeException {}

    final private List<Token> tokens;
    private int current_idx = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    // Heiarchy
    private Expr expression() {
        // extra comma logic for blocks
        return sequence();
    }
    private Expr sequence() {
        Expr expr = ternary();

        while (match(COMMA)) {
            Token comma = previous();
            Expr right = ternary();
            expr = new Expr.Binary(expr, comma, right);
        }
        return expr;
    }
    private Expr ternary() {
        Expr expr = equality();

        if (match(QUESTION)) {
            Expr left = expression();
            consume(COLON, "Expect : after 'then' branch");
            Expr right = ternary();
            expr = new Expr.Ternary(expr, left, right);
        }
        return expr;
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
    private Expr comparison() {
        Expr expr = term();

        while (match(LESS, GREATER, LESS_EQUAL, GREATER_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr term() {
        Expr expr = factor();

        while (match(PLUS, MINUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr factor() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }
    private Expr unary() {
        if (match(MINUS, BANG)) {
            Token operator = previous();
            Expr expr = unary();
            return new Expr.Unary(operator, expr);
        }
        return primary();
    }
    private Expr primary() {
        Token token = peek();

        switch (token.type) {
            case TRUE: advance(); return new Expr.Literal(true);
            case FALSE: advance(); return new Expr.Literal(false);
            case NULL: advance(); return new Expr.Literal(null);
            case NUMBER, STRING: advance(); return new Expr.Literal(token.literal);
            // ################################################################
            // TEMPRORAY JUST TO MAKE CODE RUN WITH NON NUMBERS AND STRINGS
            case IDENTIFIER: advance(); return new Expr.Literal(token.lexeme);
            // ################################################################
            case LEFT_PAREN:
                Expr expr = expression();
                consume(RIGHT_PAREN, "Expect ')' after expression");
                return new Expr.Grouping(expr);
            default:
                throw error(peek(), "Expected expression.");
        }
    }

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

    private Token consume(TokenType endToken, String message) {
        if (!endOfFile() && endToken == peek().type) return advance();
        throw error(peek(), message);
    }

    private Token advance() {
        if (!endOfFile()) current_idx++;
        return previous();
    }

    // non-consume
    private boolean endOfFile() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current_idx);
    }

    private Token previous() {
        return tokens.get(current_idx - 1);
    }

    // error handeling
    private ParseError error(Token token, String message) {
        Intrpinator.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!endOfFile()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUNCTION:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }
            advance();
        }
    }
}
