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
        List<Token> Tokens =  lex.scanTokens();
        for ( Token t : Tokens) System.out.println(t);
    }

    static void error(int line, String message) {
        report(line, "", message);
    }
    private static void report(int line, String location, String message) {
        System.err.println("[line " + line + "] Error" + location + ": " + message);
        errorFlag = true;
    }
}
