package main.java.intrpinator;

import java.util.List;

import static main.java.intrpinator.TokenType.*;

class Parser {
    final private List<Token> scannedTokens;
    private int current_idx = 0;

    Parser(List<Token> scannedTokens) {
        this.scannedTokens = scannedTokens;
    }

//    public Expr parseTokens() {
//        while (scannedTokens.get(current_idx).type != EOF) {
//            ;
//        }
//        return null;
//    }
}
