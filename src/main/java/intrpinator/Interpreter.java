package main.java.intrpinator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static main.java.intrpinator.TokenType.*;
import static main.java.intrpinator.TokenType.GREATER_EQUAL;

class Interpreter implements Expr.Visitor<Object>{

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return eval(expr.expression);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = eval(expr.right);
        return switch (expr.operator.type) {
            case BANG -> !Truthful(right);
            case MINUS -> -(double) right;
            default -> null;
        };
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = eval(expr.left);
        Object right = eval(expr.right);
        return switch (expr.operator.type) {
            // math
            case MINUS -> {
                if (left instanceof Double l && right instanceof Double r) yield l - r;
                if (left instanceof String l && right instanceof String r) yield l.replaceAll(r, "");
            }
            case SLASH -> (double)left / (double)right;
            case PLUS -> {
                if (left instanceof Double l && right instanceof Double r) yield l + r;
                if (left instanceof String l && right instanceof String r) yield l + r;
            }
            case STAR -> {
                if (left instanceof Double l && right instanceof Double r) yield l * r;
                if (left instanceof String && right instanceof Double) yield ((String) left).repeat((int)right);
            }
            // comparison
            case LESS -> (double)left > (double)right;
            case GREATER -> (double)left < (double)right;
            case LESS_EQUAL -> (double)left >= (double)right;
            case GREATER_EQUAL -> (double)left <= (double)right;
            // equality
            case EQUAL_EQUAL -> left == right;
            case BANG_EQUAL -> left != right;
            // sequence
            case COMMA ->
            default -> null;
        };
    }

    @Override
    public Object visitTernaryExpr(Expr.Ternary expr) {
        return eval(expr.condition, expr.thenBranch, expr.elseBranch);
    }

    // Helper Methods
    private Boolean Truthful(Object obj) {
        return switch (obj) {
            case null -> false;
            case Boolean bool -> bool;
            case String str -> str.isEmpty();
            case Object[] arr when arr.length == 0 -> false;
            default -> true;
        };
    }

    private Object eval(Expr expr) {
        return expr.accept(this);
    }
}
