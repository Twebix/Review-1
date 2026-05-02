import java.nio.file.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {

        String code = new String(Files.readAllBytes(Paths.get("input.txt")));

        System.out.println("SOURCE CODE:\n" + code);

        ArrayList<Token> tokens = Lexer.tokenize(code);

        System.out.println("\n--- TOKENS ---");
        for (Token t : tokens) {
            System.out.println(t);
        }

        SymbolTable sym = new SymbolTable();
        Parser parser = new Parser(tokens, sym);

        parser.parse();

        sym.printTable();
    }
}