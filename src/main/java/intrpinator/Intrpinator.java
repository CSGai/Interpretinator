package main.java.intrpinator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Intrpinator {
    static boolean errorFlag = false;
    public static void main(String[] args) throws IOException {
        // for testing:
        runFile("D:\\Users\\GAI\\dev\\java\\Interpertinator\\src\\main\\misc\\testRead.txt");

//        if (args.length > 1) {
//            System.exit(64);
//        } else if (args.length == 1) {
//            runFile(args[0]);
//        } else {
//            runPrompt();
//        }

    }

    private static void runPrompt() throws IOException {
        StringBuilder programData = new StringBuilder();
        for (;;) {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
            String line = buffer.readLine();
            programData.append(line).append("\n");
            if (line == null) break;
            else if (line.equals("exit()")) System.exit(64);
            else if (line.equals("reset()")) programData.setLength(0);
            run(programData.toString());
            errorFlag = false;
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, StandardCharsets.UTF_8));
    }

    private static void run(String sourceCode) throws RuntimeException {
        if (errorFlag) System.exit(65);
        Lexer lex = new Lexer(sourceCode);
        List<Token> tokens =  lex.scanTokens();
        for (Token token : tokens) {
            System.out.println(token);
        }
        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        // Stop if there was a syntax error.
        if (errorFlag) return;

        System.out.println(new AstPrinter().print(expression));
    }

    static void error(int line, String message) {
        report(line, "", message);
    }
    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) report(token.line, " at end", message);
        else report(token.line, token.lexeme, message);
    }
    private static void report(int line, String location, String message) {
        System.err.println("[line " + line + "] Error at " + location + " -> " + message);
        errorFlag = true;
    }
}
