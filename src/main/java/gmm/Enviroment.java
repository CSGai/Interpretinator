package main.java.gmm;

import java.util.HashMap;
import java.util.Map;

class Enviroment {
    private final Map<String, Object> values = new HashMap<>();

    void define(String name, Object value) {values.put(name, value);}
    void undefine(String name, Object value) {values.remove(name, value);}
    Object get(Token name) {
        if (values.containsKey(name.lexeme)) return values.get(name.lexeme);
        throw new RuntimeError(name, "undefined variable '" + name.lexeme + "'.");
    }
}
