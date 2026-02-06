package main.java.intrpinator;


class Interpreter implements Expr.Visitor<Object>{

    void interpret(Expr expression) {
    try {
        Object value = eval(expression);
        System.out.println(stringify(value));
    }
    catch (RuntimeError error) {
        Intrpinator.runtimeError(error);
    }
    }


    // visitors
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
            case MINUS -> {
                checkNumberOperand(expr.operator, right);
                yield -(double) right;
            }
            case BANG -> !Truthful(right);
            default -> null;
        };
    }
    @Override
    public Object visitTernaryExpr(Expr.Ternary expr) {
        Object condition = eval(expr.condition);
        Object then = eval(expr.thenBranch);
        Object otherwise = eval(expr.elseBranch);
        if (Truthful(condition)) return then;
        return otherwise;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = eval(expr.left);
        Object right = eval(expr.right);

        switch (expr.operator.type) {
            // math
            case PLUS:
                if (left instanceof Double l && right instanceof Double r) return l + r;
                if (left instanceof String && right instanceof String) return "" + left + right;
                throw new RuntimeError(expr.operator,"Operands must be two numbers or two strings.");
            case MINUS:
                if (left instanceof Double l && right instanceof Double r) return l - r;
                if (left instanceof String l && right instanceof String r) return l.replace(r, "");
                throw new RuntimeError(expr.operator,"Operands must be two numbers or two strings.");
            case STAR:
                if (left instanceof Double l && right instanceof Double r) return l * r;
                if (left instanceof String && right instanceof Double) return ((String) left).repeat((int)right);
                throw new RuntimeError(expr.operator,"Operands must be two numbers or a string and a number.");
            case SLASH: checkNumberOperands(expr.operator, left, right); return (double)left / (double)right;
            // comparison
            case LESS: checkNumberOperands(expr.operator, left, right); return (double)left > (double)right;
            case GREATER: checkNumberOperands(expr.operator, left, right); return (double)left < (double)right;
            case LESS_EQUAL: checkNumberOperands(expr.operator, left, right); return (double)left >= (double)right;
            case GREATER_EQUAL: checkNumberOperands(expr.operator, left, right); return (double)left <= (double)right;
            // equality
            case EQUAL_EQUAL: return equality(left, right);
            case BANG_EQUAL: return !equality(left, right);
            // special
            case COMMA: return right;
            default: return null;
        }
    }

    // Helper Methods
    private void checkNumberOperand(Token operator, Object right) {
        if (right instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }
    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private Boolean equality(Object left, Object right) {
        if (left == null & right == null) return true;
        if (left == null) return false;
        return left.equals(right);
    }
    private Boolean Truthful(Object obj) {
        return switch (obj) {
            case null -> false;
            case Boolean bool -> bool;
            case String str -> str.isEmpty();
            case Object[] arr when arr.length == 0 -> false;
            default -> true;
        };
    }

    private String stringify(Object obj) {
        if (obj == null) return "zilch";
        String strObj = obj.toString();
        if (obj instanceof Double) {
            if (strObj.endsWith(".0")) strObj = strObj.substring(0, strObj.length() -2);
            return strObj;
        }
        return strObj;
    }
    private Object eval(Expr expr) {
        return expr.accept(this);
    }
}
